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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.bitzeche.video.transcoding.zencoder.util.XmlUtility;

public class ZencoderWatermark {

	private String url;
	private String x = "-10";
	private String y = "-10";
	private int width;
	private int height;

	public ZencoderWatermark(String url) {
		this.url = url;
	}

	public Element createXML(Document document) {
		Element root = document.createElement("watermark");

		Node urlNode = document.createElement("url");
		urlNode.setTextContent(this.url);
		root.appendChild(urlNode);

		Node xNode = document.createElement("x");
		xNode.setTextContent(this.x);
		root.appendChild(xNode);

		Node yNode = document.createElement("y");
		yNode.setTextContent(this.y);
		root.appendChild(yNode);

		if (this.width != 0) {
			Node wNode = document.createElement("width");
			wNode.setTextContent("" + this.width);
			root.appendChild(wNode);
		}
		if (this.height != 0) {
			Node hNode = document.createElement("height");
			hNode.setTextContent("" + this.height);
			root.appendChild(hNode);
		}
		return root;
	}

	public String getUrl() {
		return url;
	}

	public String getX() {
		return x;
	}

	public String getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setX(String x) {
		this.x = x;
	}

	public void setY(String y) {
		this.y = y;
	}

	public void setWidth(int width) {
		if (width > 0) {
			this.width = width;
		}
	}

	public void setHeight(int height) {
		if (height > 0) {
			this.height = height;
		}
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
