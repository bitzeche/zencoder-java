package de.bitzeche.video.transcoding.zencoder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
			root.appendChild(urlNode);
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
}
