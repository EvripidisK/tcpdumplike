class Ipv4 {
	int version;
	int ihl;
	int totalLength;
	int protocol;
	String srcIp, dstIp;

	Ipv4(byte[] data, int off) {
		version = (data[off] & 0xFF) >> 4;
		ihl = data[off] & 0x0F;
		totalLength = Octets.lireInt16(data, off + 2, true);
		protocol = data[off + 9] & 0xFF;
		srcIp = formatIp(data, off + 12);
		dstIp = formatIp(data, off + 16);
	}

	// 4 octets -> "192.168.1.10"
	static String formatIp(byte[] t, int off) {
		String ip = "";
		for (int i = 0; i < 4; i++) {
			if (i != 0) {
				ip = ip + ".";
			}
			ip = ip + (t[off + i] & 0xFF);
		}
		return ip;
	}
}
