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

import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;
import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;

public interface IZencoderClient {

	/**
	 * Submits a new Zencoder Job
	 * 
	 * @param job
	 * @return XML Response from zencoder
	 */
	public Document createJob(ZencoderJob job)
			throws ZencoderErrorResponseException;

	public boolean resubmitJob(int jobId);

	public boolean resubmitJob(ZencoderJob job);

	public boolean cancelJob(int jobId);

	public boolean cancelJob(ZencoderJob job);

	public boolean deleteJob(int jobId);

	public boolean deleteJob(ZencoderJob job);
}
