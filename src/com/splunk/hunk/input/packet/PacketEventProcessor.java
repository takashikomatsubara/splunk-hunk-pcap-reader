// Copyright (C) 2013 Splunk Inc.
//
// Splunk Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.splunk.hunk.input.packet;

import java.util.HashMap;
import java.util.Map;

import net.ripe.hadoop.pcap.packet.Packet;

import org.apache.log4j.Logger;

public class PacketEventProcessor {

	private static final Logger gLogger = Logger.getLogger(PacketEventProcessor.class);

	public Map<String, Object> createEventFromPacket(Packet packet) {
		Map<String, Object> kvs = new HashMap<String, Object>();

		for(Map.Entry<String, Object> e : packet.entrySet()) {
			kvs.put(e.getKey(), "" + e.getValue());
		}
		return kvs;
	}


}
