package de.bitzeche.video.transcoding.zencoder;

import org.w3c.dom.Document;

public interface IZencoderClient {

	public Document createJob(ZencoderJob job);
}
