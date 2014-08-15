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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.bitzeche.video.transcoding.zencoder.util.XmlUtility;

import java.util.Map;

public class ZencoderNotification {

	private String notificationString;
	private String format;
	private Map<String, String> headers;

	public ZencoderNotification(String notificationDestination) {
		this.notificationString = notificationDestination;
	}

	public Element createXML(Document document) {
		Element root = document.createElement("notification");

		if (this.format != null) {
			Node nfNode = document.createElement("format");
			nfNode.setTextContent(this.format);
			root.appendChild(nfNode);
		}

        if (this.headers != null) {
            Node headersNode = document.createElement("headers");
            root.appendChild(headersNode);
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                Node header = document.createElement(headerEntry.getKey());
                header.setTextContent(headerEntry.getValue());
                headersNode.appendChild(header);
            }
        }

		Node urlNode = document.createElement("url");
		urlNode.setTextContent(this.notificationString);
		root.appendChild(urlNode);

		return root;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

    /**
     * @param headers see https://app.zencoder.com/docs/api/encoding/notifications/notification-headers
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

	public String toString() {
		Element elem;
		Document document;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			document = documentBuilder.newDocument();

			elem = createXML(document);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		if (elem != null) {
			document.appendChild(elem);
			try {
				return XmlUtility.xmltoString(document);
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			}
		}
		return this.getClass().getSimpleName();
	}
}
