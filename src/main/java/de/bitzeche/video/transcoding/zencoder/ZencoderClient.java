/**
 * Copyright (C) ${year} Bitzeche GmbH <info@bitzeche.de>
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;

import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;

public class ZencoderClient implements IZencoderClient {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ZencoderClient.class);
	private ApacheHttpClient httpClient;
	private XPath xPath;
	private String ZENCODER_API_KEY;

	public ZencoderClient(String zencoderApiKey) {
		this.ZENCODER_API_KEY = zencoderApiKey;
		httpClient = ApacheHttpClient.create();
		httpClient.setFollowRedirects(true);
		xPath = XPathFactory.newInstance().newXPath();
	}

	@Override
	public Document createJob(ZencoderJob job) {
		Document data;
		try {
			data = job.createXML();
			if (data == null) {
				LOGGER.error("Got no XML from Job");
			}
			Element apikey = data.createElement("api_key");
			apikey.setTextContent(ZENCODER_API_KEY);
			data.getDocumentElement().appendChild(apikey);
			Document response = sendPostRequest(
					"https://app.zencoder.com/api/jobs?format=xml", data);
			String id = (String) xPath.evaluate("/api-response/job/id",
					response, XPathConstants.STRING);
			if (id != null) {
				job.setJobId(Integer.parseInt(id));
			}
			return response;
		} catch (ParserConfigurationException e) {
		} catch (XPathExpressionException e) {
		}
		return null;
	}

	public boolean resubmitJob(ZencoderJob job) {
		int id;
		if ((id = job.getJobId()) != 0) {
			return resubmitJob(id);
		}
		return false;
	}

	public boolean resubmitJob(int id) {
		String url = "https://app.zencoder.com/api/jobs/" + id
				+ "/resubmit?api_key=" + ZENCODER_API_KEY;
		ClientResponse res = sendGetRequest(url);
		return (res.getStatus() == 200);
	}

	public boolean cancelJob(ZencoderJob job) {
		int id;
		if ((id = job.getJobId()) != 0) {
			return cancelJob(id);
		}
		return false;
	}

	public boolean cancelJob(int id) {
		String url = "https://app.zencoder.com/api/jobs/" + id
				+ "/cancel?api_key=" + ZENCODER_API_KEY;
		ClientResponse res = sendGetRequest(url);
		return (res.getStatus() == 200);
	}

	public boolean deleteJob(ZencoderJob job) {
		int id;
		if ((id = job.getJobId()) != 0) {
			return deleteJob(id);
		}
		return false;
	}

	public boolean deleteJob(int id) {
		String url = "https://app.zencoder.com/api/jobs/" + id + "?api_key="
				+ ZENCODER_API_KEY;
		LOGGER.debug("calling to delete job: {}", url);
		WebResource webResource = httpClient.resource(url);
		ClientResponse res = webResource.delete(ClientResponse.class);
		return (res.getStatus() == 200);
	}

	protected ClientResponse sendGetRequest(String url) {
		LOGGER.debug("calling: {}", url);
		WebResource webResource = httpClient.resource(url);
		return webResource.get(ClientResponse.class);

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
