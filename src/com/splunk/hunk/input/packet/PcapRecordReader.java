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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import net.ripe.hadoop.pcap.PcapReader;
import net.ripe.hadoop.pcap.packet.Packet;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.splunk.mr.input.BaseSplunkRecordReader;
import com.splunk.mr.input.VixInputSplit;

public class PcapRecordReader extends BaseSplunkRecordReader {

	private static final Logger gLogger = Logger.getLogger(PcapRecordReader.class);

	private String packet_type_class = "net.ripe.hadoop.pcap.PcapReader";
	
	//private final LinkedList<Map<String, Object>> eventQueue = new LinkedList<Map<String, Object>>();
	private Text key = new Text();
	private Map<String, Object> value; // 1イベントあたりの内容
	private FSDataInputStream fsDataIn;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private long totalBytesToRead;

	// for pcap
	private PacketEventProcessor objectPreProcessor;
	private PcapReader pcapReader;
	private Iterator<Packet> packetIterator;

	// -- Interesting stuff start here

	@Override
	public Pattern getFilePattern() {
		return Pattern.compile("\\.pcap$");
	}

	@Override
	public void vixInitialize(VixInputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		//gLogger.info("takashi:TgzPacketRecordReader:vixInitialize is called");

		FileSystem fs = FileSystem.get(context.getConfiguration());
		fsDataIn = fs.open(split.getPath());
		pcapReader = initPcapReader(packet_type_class, new DataInputStream(fsDataIn));
		
		packetIterator = pcapReader.iterator();
		totalBytesToRead = split.getLength() - split.getStart();
		objectPreProcessor = new PacketEventProcessor();
	}
	
	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return new Text(objectMapper.writeValueAsString(value));
	}

	@Override
	public void serializeCurrentValueTo(OutputStream out) throws IOException,
			InterruptedException {
		objectMapper.writeValue(out, value);
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {

		while (packetIterator.hasNext())
		{
			value = createEventFromPacket(packetIterator.next());
			return true;
		};
		return false;	
	}

		/*
		while (eventQueue.isEmpty() && packetIterator.hasNext())
		{
			eventQueue.offer(createEventFromPacket(packetIterator.next()));
		}

		if (!eventQueue.isEmpty()) {
			setNextValue(eventQueue.pop());
			return true;
		} else {
			return false;
		}		
	}

	private void setNextValue(Map<String, Object> event) throws IOException {
		value = event;
	}		
*/		
	private Map<String, Object> createEventFromPacket(Packet packet) {
		// ここがキモ
		// Splunk に返すデータをMapで取得
		Map<String, Object> oData = objectPreProcessor.createEventFromPacket(packet);
		return oData;
	}
	
	@Override
	public float getProgress() throws IOException, InterruptedException {
		return totalBytesToRead;
	}

	// -- The end of the interesting stuff

	@Override
	public void close() throws IOException {
		IOUtils.closeQuietly(fsDataIn);
		super.close();
	}

	@Override
	public String getName() {
		return "pcap";
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return key;
	}

	@Override
	public String getOutputDataFormat() {
		return "json";
	}
	
	private PcapReader initPcapReader(String className, DataInputStream is) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends PcapReader> pcapReaderClass = (Class<? extends PcapReader>)Class.forName(className);
			Constructor<? extends PcapReader> pcapReaderConstructor = pcapReaderClass.getConstructor(DataInputStream.class);
			return pcapReaderConstructor.newInstance(is);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	

}
