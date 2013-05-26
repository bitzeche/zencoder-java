/**
 * Copyright (C) 2013 Bitzeche GmbH <info@bitzeche.de>
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

public enum ZencoderRegion {
	US("us"), EUROPE("europe"), ASIA("asia"), SA("sa"), AUSTRALIA("australia"),
	VIRGINIA("us-n-virginia"), OREGON("us-oregon"), CALIFORNIA("us-n-california"),
	DUBLIN("eu-dublin"), SINGAPORE("asia-singapore"), TOKYO("asia-tokyo"),
	SAOPAULO("sa-saopaulo"), SYDNEY("australia-sydney");
	
	private final String regionCode;
	
	private ZencoderRegion(String code) {
		this.regionCode = code;
	}
	
	public String getRegionCode() {
		return regionCode;
	}
}
