/**
 * Copyright (C) 2013 Bitzeche GmbH <info@bitzeche.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bitzeche.video.transcoding.zencoder;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAPIVersion;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderNotificationJobState;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderOutput;
import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;

public class ZencoderClient implements IZencoderClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZencoderClient.class);
	private Client httpClient;
	private final String zencoderAPIBaseUrl;
	private final String zencoderAPIKey;
	private final ZencoderAPIVersion zencoderAPIVersion;
	private XPath xPath;
	
	private final int MAX_CONNECTION_ATTEMPTS = 5;
	private int currentConnectionAttempt = 0;

	public ZencoderClient(String zencoderApiKey) {
		this(zencoderApiKey, ZencoderAPIVersion.API_V1);
	}

	public ZencoderClient(String zencoderApiKey, ZencoderAPIVersion apiVersion) {
		this.zencoderAPIKey = zencoderApiKey;
		if (ZencoderAPIVersion.API_DEV.equals(apiVersion)) {
			LOGGER.warn("!!! Using development version of zencoder API !!!");
		}

		this.zencoderAPIVersion = apiVersion;

		httpClient = ApacheHttpClient.create();
		httpClient.setFollowRedirects(true);
		
		// set a 20 second timeout on the client
		httpClient.setConnectTimeout(20000);
		httpClient.setReadTimeout(20000);
		
		xPath = XPathFactory.newInstance().newXPath();
		zencoderAPIBaseUrl = zencoderAPIVersion.getBaseUrl();
	}

	/*
	 * Typical response: <?xml version="1.0" encoding="UTF-8"?> <api-response>
	 * <job> <test type="boolean">true</test> <outputs type="array"> <output>
	 * <url
	 * >http://audio-bucket.jagtest.spotnote.s3.amazonaws.com/ApU001TestUserAx001
	 * .m4a</url> <label>test_aac</label> <id type="integer">29345822</id>
	 * </output> </outputs> <id type="integer">17941347</id> </job>
	 * </api-response>
	 */

	private Integer findIdFromOutputNode(Node output)
			throws XPathExpressionException {
		Double idDouble = (Double) xPath.evaluate("output/id", output,
				XPathConstants.NUMBER);
		return idDouble == null ? null : idDouble.intValue();
	}

	/**
	 * Complete output IDs from response.
	 * 
	 * @param job
	 * @param response
	 */
	private void completeJobInfo(ZencoderJob job, Document response) {
		try {
			NodeList outputs = (NodeList) xPath.evaluate(
					"/api-response/job/outputs", response,
					XPathConstants.NODESET);
			if (job.getOutputs().size() == 1) {
				Integer id = findIdFromOutputNode(outputs.item(0));
				if (id != null) {
					job.getOutputs().get(0).setId(id);
				}
			} else {
				// try via labels
				Map<String, Integer> ids = new HashMap<String, Integer>();
				int outputSize = outputs.getLength();
				for (int i = 0; i < outputSize; i++) {
					String label = (String) xPath.evaluate("output/label",
							outputs.item(i), XPathConstants.STRING);
					if (label != null && !label.isEmpty()) {
						int id = findIdFromOutputNode(outputs.item(i));
						ids.put(label, new Integer(id));
					}
				}
				for (ZencoderOutput zcOutput : job.getOutputs()) {
					Integer foundId = ids.get(zcOutput.getLabel());
					if (foundId != null) {
						zcOutput.setId(foundId);
					}
				}
			}

		} catch (XPathExpressionException e) {
			LOGGER.error("XPath threw Exception", e);
		}
	}
	
	private void resetConnectionCount() {
		// reset to 0 for use in tracking connections next time
		currentConnectionAttempt = 0;
	}
	
	private Document createDocumentForException(String message) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document errorDocument = documentBuilder.newDocument();
			Element root = errorDocument.createElemet("error");
			errorDocument.appendChild(root);
			Node input = errorDocument.createElement("reason");
			input.setTextContent(message);
			root.appendChild(input);
			return errorDocument;
		} catch (ParserConfigurationException e) {
			LOGGER.error("Exception creating document for exception '" + message + "'", e);
			return null;
		}
	}

	@Override
	public Document createJob(ZencoderJob job)
			throws ZencoderErrorResponseException {
		if(currentConnectionAttempt > MAX_CONNECTION_ATTEMPTS) {
			resetConnectionCount();
			String message = "Reached maximum number of connection attempts for Zencoder, aborting creation of job";
			Document errorDocument = createDocumentForException(message);
			throw new ZencoderErrorResponseException(errorDocument);
		}
		Document data;
		try {
			data = job.createXML();
			if (data == null) {
				String message = "Got no XML from Job";
				LOGGER.error(message);
				resetConnectionCount();
				Document errorDocument = createDocumentForException(message);
				throw new ZencoderErrorResponseException(errorDocument);
			}
			Element apikey = data.createElement("api_key");
			apikey.setTextContent(zencoderAPIKey);
			data.getDocumentElement().appendChild(apikey);
			Document response = sendPostRequest(zencoderAPIBaseUrl
					+ "jobs?format=xml", data);
			// a null response means the call did not get through
			// we should try again, since the job has not been started
			if(response == null) {
				currentConnectionAttempt++;
				// maybe delay this call by a few seconds?
				return createJob(job);
			}
			String id = (String) xPath.evaluate("/api-response/job/id",
					response, XPathConstants.STRING);
			if (StringUtils.isNotEmpty(id)) {
				job.setJobId(Integer.parseInt(id));
				resetConnectionCount();
				return response;
			}
			completeJobInfo(job, response);
			LOGGER.error("Error when sending request to Zencoder: ", response);
			resetConnectionCount();
			throw new ZencoderErrorResponseException(response);
		} catch (ParserConfigurationException e) {
			LOGGER.error("Parser threw Exception", e);
		} catch (XPathExpressionException e) {
			LOGGER.error("XPath threw Exception", e);
		}
		resetConnectionCount();
		return null;
	}

	public ZencoderNotificationJobState jobProgress(ZencoderJob job) {
		return jobProgress(job.getJobId());
	}

	public ZencoderNotificationJobState jobProgress(int id) {
		return getJobState(id);
	}

	public ZencoderNotificationJobState getJobState(ZencoderJob job) {
		return getJobState(job.getJobId());
	}

	public ZencoderNotificationJobState getJobState(int id) {
		Document response = getJobProgress(id);
		if (response == null) {
			return null;
		}
		String stateString = null;
		try {
			stateString = (String) xPath.evaluate("/api-response/state",
					response, XPathConstants.STRING);
			return ZencoderNotificationJobState.getJobState(stateString);
		} catch (IllegalArgumentException ex) {
			LOGGER.error("Unable to find state for string '{}'", stateString);
		} catch (XPathExpressionException e) {
			LOGGER.error("XPath threw Exception", e);
		}
		return null;
	}

	public Document getJobProgress(ZencoderJob job) {
		return getJobProgress(job.getJobId());
	}

	public Document getJobProgress(int id) {
		if(currentConnectionAttempt > MAX_CONNECTION_ATTEMPTS) {
			resetConnectionCount();
			LOGGER.error("Reached maximum number of attempts for getting job progress. Aborting and returning null");
			return null;
		}
		if (zencoderAPIVersion != ZencoderAPIVersion.API_V2) {
			LOGGER.warn("jobProgress is only available for API v2.  Returning null.");
			return null;
		}
		String url = zencoderAPIBaseUrl + "jobs/" + id
				+ "/progress.xml?api_key=" + zencoderAPIKey;
		Document result = sendGetRequest(url);
		if(result == null) {
			currentConnectionAttempt++;
			// delay this call by a few seconds?
			return getJobProgress(id);
		}
		resetConnectionCount();
		return result;
	}

	public Document getJobDetails(ZencoderJob job) {
		return getJobDetails(job.getJobId());
	}

	public Document getJobDetails(int id) {
		if(currentConnectionAttempt > MAX_CONNECTION_ATTEMPTS) {
			resetConnectionCount();
			LOGGER.error("Reached maximum number of attempts for getting job details. Aborting and returning null");
			return null;
		}
		String url = zencoderAPIBaseUrl + "jobs/" + id + ".xml?api_key="
				+ zencoderAPIKey;
		Document result = sendGetRequest(url);
		if(result == null) {
			currentConnectionAttempt++;
			// delay this call by a few seconds?
			return getJobDetails(id);
		}
		resetConnectionCount();
		return result;
	}

	public boolean resubmitJob(ZencoderJob job) {
		int id;
		if ((id = job.getJobId()) != 0) {
			return resubmitJob(id);
		}
		return false;
	}

	public boolean resubmitJob(int id) {
		if(currentConnectionAttempt > MAX_CONNECTION_ATTEMPTS) {
			resetConnectionCount();
			LOGGER.error("Reached maximum number of attempts to resubmit job, aborting");
			return false;
		}
		String url = zencoderAPIBaseUrl + "jobs/" + id + "/resubmit?api_key="
				+ zencoderAPIKey;
		ClientResponse response = sendPutRequest(url);
		if(response == null) {
			currentConnectionAtttempt++;
			return resubmitJob(id);
		}
		int responseStatus = response.getStatus();
		resetConnectionCount();
		if (responseStatus == 200 || responseStatus == 204) {
			return true;
		} else if (responseStatus == 409) {
			LOGGER.debug("Already finished job {}", id);
			return true;
		}
		return false;
	}

	public boolean cancelJob(ZencoderJob job) {
		int id;
		if ((id = job.getJobId()) != 0) {
			return cancelJob(id);
		}
		return false;
	}

	public boolean cancelJob(int id) {
		if(currentConnectionAttempt > MAX_CONNECTION_ATTEMPTS) {
			resetConnectionCount();
			LOGGER.error("Reached maximum number of attempts to cancel job, aborting");
			return false;
		}
		String url = zencoderAPIBaseUrl + "jobs/" + id
				+ "/cancel.json?api_key=" + zencoderAPIKey;
		ClientResponse res = sendPutRequest(url);
		if(res == null) {
			currentConnectionAttempt++;
			return cancelJob(id);
		}
		int responseStatus = res.getStatus();
		resetConnectionCount();
		if (responseStatus == 200 || responseStatus == 204) {
			return true;
		} else if (responseStatus == 409) {
			LOGGER.debug("Already finished job {}", id);
			return true;
		}
		return false;
	}

	public boolean deleteJob(ZencoderJob job) {
		int id;
		if ((id = job.getJobId()) != 0) {
			return deleteJob(id);
		}
		return false;
	}

	@Deprecated
	public boolean deleteJob(int id) {
		LOGGER.warn("Deleting jobs is deprecated. Use cancel instead.");
		return cancelJob(id);

		// String url = zencoderAPIBaseUrl + "jobs/" + id + "?api_key="
		// + zencoderAPIKey;
		// LOGGER.debug("calling to delete job: {}", url);
		// WebResource webResource = httpClient.resource(url);
		// ClientResponse response = webResource.delete(ClientResponse.class);
		// int responseStatus = response.getStatus();
		// return (responseStatus == 200);
	}

	protected Document sendGetRequest(String url) {
		LOGGER.debug("calling: {}", url);
		try {
			WebResource webResource = httpClient.resource(url);
			Document response = webResource.get(Document.class);

			logXmlDocumentToDebug("Got response", response);
			return response;
		} catch (Exception e) {
			if(e instanceof SocketTimeoutException) {
				LOGGER.warn("Connection to Zencoder timed out");
			} else {
				LOGGER.warn(e.getMessage());
			}
		}
		return null;
	}

	protected ClientResponse sendPutRequest(String url) {
		LOGGER.debug("calling: {}", url);
		try {
			WebResource webResource = httpClient.resource(url);
			ClientResponse response = webResource.put(ClientResponse.class);

			LOGGER.debug("Got response: {}", response);
			return response;
		} catch (Exception e) {
			if(e instanceof SocketTimeoutException) {
				LOGGER.warn("Connection to Zencoder timed out");
			} else {
				LOGGER.warn(e.getMessage());
			}
		}
		return null;

	}

	protected Document sendPostRequest(String url, Document xml) {
		logXmlDocumentToDebug("submitting", xml);
		try {
			WebResource webResource = httpClient.resource(url);
			Document response = webResource.accept(MediaType.APPLICATION_XML)
					.header("Content-Type", "application/xml")
					.post(Document.class, xml);
			logXmlDocumentToDebug("Got response", response);
			return response;
		} catch (UniformInterfaceException e) {
			ClientResponse resp = e.getResponse();
			Document errorXml = resp.getEntity(Document.class);
			String errormessage = e.getMessage();
			try {
				errormessage = (String) xPath.evaluate(
						"/api-response/errors/error", errorXml,
						XPathConstants.STRING);
			} catch (XPathExpressionException e1) {
				// ignore
			}
			LOGGER.error("couldn't submit job: {}", errormessage);
			return errorXml;
		} catch (Exception e) {
			if(e instanceof SocketTimeoutException) {
				LOGGER.warn("Connection to Zencoder timed out");
			} else {
				LOGGER.warn(e.getMessage());
			}
		}
		return null;

	}

	private void logXmlDocumentToDebug(String message, Document response) {

		if (LOGGER.isDebugEnabled()) {
			try {
				LOGGER.debug(message + ": {}", XmltoString(response));
			} catch (TransformerException e2) {
			}
		}
	}

	protected static String XmltoString(Document document)
			throws TransformerException {
		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.transform(new DOMSource(document.getDocumentElement()),
				streamResult);
		return stringWriter.toString();
	}

	public void setHttpClient(Client httpClient) {
		this.httpClient = httpClient;
	}
}
