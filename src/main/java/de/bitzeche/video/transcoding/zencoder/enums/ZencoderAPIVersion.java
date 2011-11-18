package de.bitzeche.video.transcoding.zencoder.enums;

public enum ZencoderAPIVersion {
	API_DEV("https://app.zencoder.com/api/"), 
	API_V1(	"https://app.zencoder.com/api/v1/"), 
	API_V2(	"https://app.zencoder.com/api/v2/");

	private final String baseUrl;

	private ZencoderAPIVersion(String url) {
		this.baseUrl = url;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

}
