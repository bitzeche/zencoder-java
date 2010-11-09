package de.bitzeche.video.transcoding.zencoder.job;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderRegion;

public class ZencoderJob {

	private int JobId;

	private String inputPath;

	private ZencoderRegion zencoderRegion = null;

	private int downloadConnections = 5;

	private boolean isTest = false;

	private ArrayList<ZencoderOutput> outputs = new ArrayList<ZencoderOutput>();

	public ZencoderJob(String inputPath) {
		this.inputPath = inputPath;
	}

	public void addOutput(ZencoderOutput output) {
		this.outputs.add(output);
	}

	public void deleteOutput(ZencoderOutput output) {
		this.outputs.remove(output);
	}

	public Document createXML() throws ParserConfigurationException {
		if (outputs.size() == 0)
			return null;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
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
			region.setTextContent(this.zencoderRegion.name().toLowerCase());
			root.appendChild(region);
		}

		Node download_connections = document.createElement("download_connections");
		download_connections.setTextContent("" + this.downloadConnections);
		root.appendChild(download_connections);

		Node test = document.createElement("test");
		test.setTextContent((this.isTest ? "1" : "0"));
		root.appendChild(test);
		
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
		}
		if (document != null) {

			StringWriter stringWriter = new StringWriter();
			StreamResult streamResult = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = transformerFactory.newTransformer();

				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				transformer.setOutputProperty(OutputKeys.METHOD, "xml");
				transformer.transform(new DOMSource(document.getDocumentElement()), streamResult);
				return stringWriter.toString();
			} catch (TransformerConfigurationException e) {
			} catch (TransformerException e) {
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
}
