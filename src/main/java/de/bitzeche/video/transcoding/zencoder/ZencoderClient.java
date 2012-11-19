/**
 * Copyright (C) 2012 Bitzeche GmbH <info@bitzeche.de>
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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAPIVersion;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderNotificationJobState;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;
import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderOutput;

public class ZencoderClient implements IZencoderClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZencoderClient.class);
	private ApacheHttpClient httpClient;
	private final String zencoderAPIBaseUrl;
	private final String zencoderAPIKey;
	private final ZencoderAPIVersion zencoderAPIVersion;
	private XPath xPath;

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
		xPath = XPathFactory.newInstance().newXPath();
		zencoderAPIBaseUrl = zencoderAPIVersion.getBaseUrl();
	}

	/*
	 * Typical response:
<?xml version="1.0" encoding="UTF-8"?>
<api-response>
  <job>
    <test type="boolean">true</test>
    <outputs type="array">
      <output>
        <url>http://audio-bucket.jagtest.spotnote.s3.amazonaws.com/ApU001TestUserAx001.m4a</url>
        <label>test_aac</label>
        <id type="integer">29345822</id>
      </output>
    </outputs>
    <id type="integer">17941347</id>
  </job>
</api-response>
	 * 
	 */
	
	
	private Integer findIdFromOutputNode(Node output) throws XPathExpressionException
	{
		Double idDouble = (Double)xPath.evaluate("output/id", output, XPathConstants.NUMBER);
		return idDouble == null ? null : idDouble.intValue();
	}
	
	/**
	 * Complete output IDs from response.
	 * @param job
	 * @param response
	 */
	private void completeJobInfo(ZencoderJob job, Document response) {
		try {
			NodeList outputs = (NodeList) xPath.evaluate("/api-response/job/outputs",
				response, XPathConstants.NODESET);
			if (job.getOutputs().size() == 1)
			{
				Integer id = findIdFromOutputNode(outputs.item(0));
				if (id != null)	
				{
					job.getOutputs().get(0).setId(id);
				}
			}
			else  
			{
				//try via labels
				Map<String, Integer> ids = new HashMap<String, Integer>(); 
				int outputSize = outputs.getLength();
				for (int i=0; i<outputSize; i++) 
				{
					String label = (String) xPath.evaluate("output/label", outputs.item(i), XPathConstants.STRING);
					if (label != null && !label.isEmpty()) 
					{
						int id = findIdFromOutputNode(outputs.item(i));
						ids.put(label, new Integer(id));
					}
				}
				for (ZencoderOutput zcOutput : job.getOutputs())
				{
					Integer foundId = ids.get(zcOutput.getLabel());
					if (foundId != null)
					{
						zcOutput.setId(foundId);
					}
				}
			}
			
		} catch (XPathExpressionException e) {
			LOGGER.error("XPath threw Exception", e);
		}
	}
	
	
	@Override
	public Document createJob(ZencoderJob job)
			throws ZencoderErrorResponseException {
		Document data;
		try {
			data = job.createXML();
			if (data == null) {
				LOGGER.error("Got no XML from Job");
			}
			Element apikey = data.createElement("api_key");
			apikey.setTextContent(zencoderAPIKey);
			data.getDocumentElement().appendChild(apikey);
			Document response = sendPostRequest(
					zencoderAPIBaseUrl + "jobs?format=xml", data);
			String id = (String) xPath.evaluate("/api-response/job/id",
					response, XPathConstants.STRING);
			if (StringUtils.isNotEmpty(id)) {
				job.setJobId(Integer.parseInt(id));
				return response;
			}
			completeJobInfo(job, response);
			LOGGER.error("Error when sending request to Zencoder: ", response);
			throw new ZencoderErrorResponseException(response);
		} catch (ParserConfigurationException e) {
			LOGGER.error("Parser threw Exception", e);
		} catch (XPathExpressionException e) {
			LOGGER.error("XPath threw Exception", e);
		}
		return null;
	}

	public ZencoderNotificationJobState jobProgress(ZencoderJob job) {
		return jobProgress(job.getJobId());
	}
	
	public ZencoderNotificationJobState jobProgress(int id) {
		if (zencoderAPIVersion != ZencoderAPIVersion.API_V2) {
			LOGGER.warn("jobProgress is only available for API v2.  Returning null.");
			return null;
		}
		String url = zencoderAPIBaseUrl + "jobs/" + id
				+ "/progress.xml?api_key=" + zencoderAPIKey;
		System.out.println("Calling job state url: " + url);
		WebResource webResource = httpClient.resource(url);
		Document response = webResource.get(Document.class);
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

	public Document getJobDetails(ZencoderJob job) {
		return getJobDetails(job.getJobId());
	}
	
	public Document getJobDetails(int id) {
		String url = zencoderAPIBaseUrl + "jobs/" + id
				+ ".xml?api_key=" + zencoderAPIKey;
		WebResource webResource = httpClient.resource(url);
		Document response = webResource.get(Document.class);
		return response;
	}

	public boolean resubmitJob(ZencoderJob job) {
		int id;
		if ((id = job.getJobId()) != 0) {
			return resubmitJob(id);
		}
		return false;
	}

	public boolean resubmitJob(int id) {
		String url = zencoderAPIBaseUrl + "jobs/" + id + "/resubmit?api_key="
				+ zencoderAPIKey;
		ClientResponse res = sendPutRequest(url);
		int responseStatus = res.getStatus();
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
		String url = zencoderAPIBaseUrl + "jobs/" + id
				+ "/cancel.json?api_key=" + zencoderAPIKey;
		ClientResponse res = sendPutRequest(url);
		int responseStatus = res.getStatus();
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

	protected ClientResponse sendGetRequest(String url) {
		LOGGER.debug("calling: {}", url);
		WebResource webResource = httpClient.resource(url);
		return webResource.get(ClientResponse.class);

	}

	protected ClientResponse sendPutRequest(String url) {
		LOGGER.debug("calling: {}", url);
		WebResource webResource = httpClient.resource(url);
		return webResource.put(ClientResponse.class);

	}

	protected Document sendPostRequest(String url, Document xml) {
		try {
			LOGGER.debug("submitting: {}", XmltoString(xml));
		} catch (TransformerException e2) {
		}
		try {
			WebResource webResource = httpClient.resource(url);
			return webResource.accept(MediaType.APPLICATION_XML)
					.header("Content-Type", "application/xml")
					.post(Document.class, xml);
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
}
