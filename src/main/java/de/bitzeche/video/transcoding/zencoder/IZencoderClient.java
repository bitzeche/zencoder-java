package de.bitzeche.video.transcoding.zencoder;

import org.w3c.dom.Document;

import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;


public interface IZencoderClient {

	/**
	 * Submits a new Zencoder Job
	 * @param job
	 * @return XML Response from zencoder
	 */
	public Document createJob(ZencoderJob job);
	
	public boolean resubmitJob(ZencoderJob job);

	public boolean cancelJob(ZencoderJob job);

	public boolean deleteJob(ZencoderJob job);
}
