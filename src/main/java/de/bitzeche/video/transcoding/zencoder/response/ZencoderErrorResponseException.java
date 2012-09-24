package de.bitzeche.video.transcoding.zencoder.response;

import org.w3c.dom.Document;

public class ZencoderErrorResponseException extends Exception {

	private static final long serialVersionUID = 7693985002277452696L;

	private final Document errorResponse;

	public ZencoderErrorResponseException(Document response) {
		this.errorResponse = response;
	}

	public Document getErrorResponse() {
		return errorResponse;
	}
}
