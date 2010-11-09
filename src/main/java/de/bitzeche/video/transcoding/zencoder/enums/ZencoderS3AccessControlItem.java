package de.bitzeche.video.transcoding.zencoder.enums;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.bitzeche.video.transcoding.zencoder.job.ZencoderS3AccessControlRight;


public class ZencoderS3AccessControlItem {
	private String grantee;
	ArrayList<ZencoderS3AccessControlRight> rights;

	public ZencoderS3AccessControlItem(String grantee, ArrayList<ZencoderS3AccessControlRight> rights) {
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
}
