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

package de.bitzeche.video.transcoding.zencoder.job;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderRegion;
import de.bitzeche.video.transcoding.zencoder.util.XmlUtility;

public class ZencoderJob {

	private int JobId;

	private String inputPath;

	private ZencoderRegion zencoderRegion = null;

	private int downloadConnections = 5;

	private boolean isTest = false;

	private boolean isPrivate = false;

	private List<ZencoderOutput> outputs = new ArrayList<ZencoderOutput>();

    private List<ZencoderNotification> notifications = new ArrayList<ZencoderNotification>();

    /**
     * According to <a href="zencoder API documentation">https://app.zencoder.com/docs/api/encoding</a> pass_through is
     * "Optional information to store alongside this job".<br/>
     * When pass_through field is provided during job submission it becomes available in job notification
     * callback from the service.
     */
    private String passThrough;


    public ZencoderJob(String inputPath) {
		this.inputPath = inputPath;
	}

	public void addOutput(ZencoderOutput output) {
		this.outputs.add(output);
	}

	public List<ZencoderOutput> getOutputs() {
		return this.outputs;
	}
	
	public void deleteOutput(ZencoderOutput output) {
		this.outputs.remove(output);
	}

	public Document createXML() throws ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element root = document.createElement("api-request");
		document.appendChild(root);

		// input
		Node input = document.createElement("input");
		input.setTextContent(this.inputPath);
		root.appendChild(input);

		// region
		if (this.zencoderRegion != null) {
			Node region = document.createElement("region");
			region.setTextContent(this.zencoderRegion.getRegionCode());
			root.appendChild(region);
		}

		Node download_connections = document
				.createElement("download_connections");
		download_connections.setTextContent("" + this.downloadConnections);
		root.appendChild(download_connections);

		if (getPassThrough() != null) {
            Node passThroughNode = document.createElement("pass_through");
            passThroughNode.setTextContent(this.getPassThrough());
            root.appendChild(passThroughNode);
        }

		Node test = document.createElement("test");
		test.setTextContent((this.isTest ? "1" : "0"));
		root.appendChild(test);

		Node privateNode = document.createElement("private");
		privateNode.setTextContent((this.isPrivate ? "1" : "0"));
		root.appendChild(privateNode);

        // job notifications
        // API Note: Job-level notifications are supported only since API V2
        if (this.notifications.size() != 0) {
            Element notifis = document.createElement("notifications");
            notifis.setAttribute("type", "array");
            root.appendChild(notifis);
            for (ZencoderNotification item : this.notifications) {
                Element notif = item.createXML(document);
                if (notif != null) {
                    notifis.appendChild(notif);
                }
            }
        }

		if (outputs.size() == 0)
			return document;

		Element output_nodes = document.createElement("outputs");
		output_nodes.setAttribute("type", "array");
		root.appendChild(output_nodes);

		for (ZencoderOutput output : outputs) {
			Element out = output.createXML(document);
			if (out != null) {
				output_nodes.appendChild(out);
			}
		}
		return document;
	}

	public String toString() {
		Document document = null;
		try {
			document = createXML();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		if (document != null) {
			try {
				return XmlUtility.xmltoString(document);
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			}
		}
		return this.getClass().getSimpleName();
	}

	public int getJobId() {
		return JobId;
	}

	public String getInputPath() {
		return inputPath;
	}

	public ZencoderRegion getZencoderRegion() {
		return zencoderRegion;
	}

	public int getDownloadConnections() {
		return downloadConnections;
	}

    public String getPassThrough() {
        return passThrough;
    }

    public boolean isTest() {
		return isTest;
	}

    public void setJobId(int id) {
		JobId = id;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public void setZencoderRegion(ZencoderRegion zencoderRegion) {
		this.zencoderRegion = zencoderRegion;
	}

	public void setDownloadConnections(int downloadConnections) {
		this.downloadConnections = downloadConnections;
	}

	public void setTest(boolean isTest) {
		this.isTest = isTest;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

    public void addNotification(ZencoderNotification item) {
        this.notifications.add(item);
    }

    public void deleteNotification(ZencoderNotification item) {
        this.notifications.remove(item);
    }

    public void setPassThrough(String passThrough) {
        this.passThrough = passThrough;
    }
}
