package de.bitzeche.video.transcoding.zencoder;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderS3AccessControlItem;

public class ZencoderThumbnail {

	private int number;
	private int interval;
	private ArrayList<Number> times = new ArrayList<Number>();
	private String size;
	private String baseUrl;
	private String prefix;
	/*
	 * S3
	 */
	private boolean isPublic = false;
	private ArrayList<ZencoderS3AccessControlItem> aclItems = new ArrayList<ZencoderS3AccessControlItem>();

	public Element createXML(Document document) {
		if (this.number == 0 && this.interval == 0 && this.times.size() == 0) {
			throw new IllegalArgumentException("No number, interval or times set");
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

	public void addAcl(ZencoderS3AccessControlItem item) {
		this.aclItems.add(item);
	}

	public void deleteAcl(ZencoderS3AccessControlItem item) {
		this.aclItems.remove(item);
	}

}
