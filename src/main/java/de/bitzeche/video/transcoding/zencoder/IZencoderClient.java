/**
 * Copyright (C) 2012 Bitzeche GmbH <info@bitzeche.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bitzeche.video.transcoding.zencoder;

import org.w3c.dom.Document;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderNotificationJobState;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;


public interface IZencoderClient {

	/**
	 * Submits a new Zencoder Job
	 * @param job
	 * @return XML Response from zencoder
	 */
	public Document createJob(ZencoderJob job);
	
	/**
	 * Send a jobProgress request for a job.
	 * @param jobId ID for the requested job.
	 * @return State of job, or null if unable to parse response.
	 */
	public ZencoderNotificationJobState jobProgress(int jobId);
	
	/**
	 * Send a jobProgress request for a job.
	 * @param job
	 * @return State of job, or null if unable to parse response.
	 */
	public ZencoderNotificationJobState jobProgress(ZencoderJob job);
	
	/**
	 * Send a resubmit request for a job.
	 * @param jobId ID for the requested job.
	 * @return true if job was resubmitted successfully.  
	 * Attempting to resubmit an already finished job returns false. 
	 */
	public boolean resubmitJob(int jobId);
	
	/**
	 * Send a resubmit request for a job.
	 * @param job
	 * @return true if job was resubmitted successfully.  
	 * Attempting to resubmit an already finished job returns false. 
	 */
	public boolean resubmitJob(ZencoderJob job);

	/**
	 * Send a cancel request for a job.
	 * @param jobId ID for the requested job.
	 * @return true if job was cancelled successfully.  
	 * Attempting to cancel an already finished job returns false. 
	 */
	public boolean cancelJob(int jobId);

	/**
	 * Send a cancel request for a job.
	 * @param job
	 * @return true if job was cancelled successfully.  
	 * Attempting to cancel an already finished job returns false. 
	 */
	public boolean cancelJob(ZencoderJob job);

	@Deprecated
	public boolean deleteJob(int jobId);
	
	@Deprecated
	public boolean deleteJob(ZencoderJob job);
}
