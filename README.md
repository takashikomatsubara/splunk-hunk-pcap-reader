splunk-hunk-pcap-reader
===================

License
-------
This library is distributed under the LGPL.  
See: https://github.com/takashikomatsubara/splunk-hunk-pcap-reader/blob/master/LICENSE

Components
----------

This project supports three types of packets.

### DnsPacket

com/splunk/hunk/input/packet/DnsPcapRecordReader

This class can be used for DNS Packet Analysis.

### HttpPacket

com/splunk/hunk/input/packet/HttpPcapRecordReader

This class can be used for HTTP Packet Analysis.

### Pcap

com/splunk/hunk/input/packet/PcapRecordReader

This class can be used for Generic Packet Analysis.

### 3rd Party Libraries

Special Thanks for "Hadoop PCAP library" ( https://github.com/RIPE-NCC/hadoop-pcap ).

### How to compile

1.Obtain Splunk/Hunk installation package.
2.Extract the binaries
3.Copy bin/jars/SplunkMR-*.jar hunklib directory.
4.ant and find build/jar/splunk-hunk-pcap-reader.jar file.

### Note

This build package is for Hadoop 2.x (YARN). Not for Hadoop 1.x or 0.x.
If you want to use with Hadoop 1.x or 0.x, you can exchange necessary hadoop jar files in lib directory.

