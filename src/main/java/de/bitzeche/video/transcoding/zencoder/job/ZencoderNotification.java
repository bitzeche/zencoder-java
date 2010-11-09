package de.bitzeche.video.transcoding.zencoder.job;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ZencoderNotification {

	private String notificationString;

	public ZencoderNotification(String notificationDestination) {
		this.notificationString = notificationDestination;
	}

	public Element createXML(Document document) {
		Element root = document.createElement("notification");

		Node nfNode = document.createElement("format");
		nfNode.setTextContent("xml");
		root.appendChild(nfNode);

		Node urlNode = document.createElement("url");
		urlNode.setTextContent(this.notificationString);
		root.appendChild(urlNode);

		return root;
	}
}
