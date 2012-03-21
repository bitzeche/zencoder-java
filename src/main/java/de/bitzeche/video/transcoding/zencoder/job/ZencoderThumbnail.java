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

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderThumbnailFormat;
import de.bitzeche.video.transcoding.zencoder.util.XmlUtility;

public class ZencoderThumbnail {

	private int number;
	private int interval;
	private List<Number> times = new ArrayList<Number>();
	private String size;
	private String baseUrl;
	private String prefix;
	private ZencoderThumbnailFormat format;

	/*
	 * S3
	 */
	private boolean isPublic = false;
	private List<ZencoderS3AccessControlItem> aclItems = new ArrayList<ZencoderS3AccessControlItem>();

	public Element createXML(Document document) {
		if (this.number == 0 && this.interval == 0 && this.times.size() == 0) {
			throw new IllegalArgumentException(
					"No number, interval or times set");
		}
		Element root = document.createElement("thumbnails");

		if (this.number != 0) {
			Node numberNode = document.createElement("number");
			numberNode.setTextContent("" + this.number);
			root.appendChild(numberNode);
		}
		if (this.interval != 0) {
			Node intervalNode = document.createElement("interval");
			intervalNode.setTextContent("" + this.interval);
			root.appendChild(intervalNode);
		}
		if (this.times.size() != 0) {
			Element timesNode = document.createElement("times");
			root.appendChild(timesNode);
			for (Number time : this.times) {
				Node tn = document.createElement("number");
				tn.setTextContent("" + time);
				timesNode.appendChild(tn);
			}
		}
		if (this.size != null) {
			Node sizeNode = document.createElement("size");
			sizeNode.setTextContent(this.size);
			root.appendChild(sizeNode);
		}
		
		if (this.format != null) {
			Node sizeNode = document.createElement("format");
			sizeNode.setTextContent(this.format.name().toLowerCase());
			root.appendChild(sizeNode);
		}

		if (this.baseUrl != null) {
			Node baseUrlNode = document.createElement("base_url");
			baseUrlNode.setTextContent(this.baseUrl);
			root.appendChild(baseUrlNode);
		}
		if (this.prefix != null) {
			Node prefixNode = document.createElement("prefix");
			prefixNode.setTextContent(this.prefix);
			root.appendChild(prefixNode);
		}

		Node publicNode = document.createElement("public");
		publicNode.setTextContent(this.isPublic ? "1" : "0");
		root.appendChild(publicNode);

		if (this.aclItems.size() != 0) {
			Element acl = document.createElement("access-controls");
			root.appendChild(acl);
			for (ZencoderS3AccessControlItem item : this.aclItems) {
				Element acls = item.createXML(document);
				if (acls != null) {
					acl.appendChild(acls);
				}
			}
		}
		return root;
	}

	public void addTime(float time) {
		times.add(time);
	}

	public int getNumber() {
		return number;
	}

	public int getInterval() {
		return interval;
	}

	public String getSize() {
		return size;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getPrefix() {
		return prefix;
	}
	public ZencoderThumbnailFormat getFormat() {
		return format;
	}

	public boolean isPublic() {
		return isPublic;
	}


	public void setFormat(ZencoderThumbnailFormat format) {
		this.format = format;
	}
	public void setNumber(int number) {
		if (number > 0) {
			this.number = number;
		}
	}

	public void setInterval(int interval) {
		if (interval > 0) {
			this.interval = interval;
		}
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void addAcl(ZencoderS3AccessControlItem item) {
		this.aclItems.add(item);
	}

	public void deleteAcl(ZencoderS3AccessControlItem item) {
		this.aclItems.remove(item);
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
