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
