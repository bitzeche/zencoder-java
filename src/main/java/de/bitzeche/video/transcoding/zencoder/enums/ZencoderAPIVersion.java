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
