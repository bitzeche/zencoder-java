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

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderS3AccessControlRight;
import de.bitzeche.video.transcoding.zencoder.util.XmlUtility;

public class ZencoderS3AccessControlItem {
	private String grantee;
	List<ZencoderS3AccessControlRight> rights;

	public ZencoderS3AccessControlItem(String grantee,
			List<ZencoderS3AccessControlRight> rights) {
		this.grantee = grantee;
		this.rights = rights;
	}

	public Element createXML(Document document) {
		Element root = document.createElement("access_control");

		Node granteeNode = document.createElement("grantee");
		granteeNode.setTextContent(this.grantee);
		root.appendChild(granteeNode);
		Element permissions = document.createElement("permissions");
		root.appendChild(permissions);

		for (ZencoderS3AccessControlRight right : rights) {
			Node permission = document.createElement("permission");
			permission.setTextContent(right.name());
			permissions.appendChild(permission);
		}
		return root;
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
