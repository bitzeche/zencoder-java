package de.bitzeche.video.transcoding.zencoder.enums;

public enum ZencoderNotificationJobState {
	
	PROCESSING, DONE, FAILED;
	
	public String getJobStateString() {
		return this.name().toLowerCase();
	}

}
