class Ipv6 {
	int version;
	int payloadLength;
	int nextHeader;
	int hopLimit;
	String srcIp, dstIp;

	Ipv6(byte[] data, int off) {
		version = (data[off] & 0xFF) >> 4;
		payloadLength = Octets.lireInt16(data, off + 4, true);
		nextHeader = data[off + 6] & 0xFF;
		hopLimit = data[off + 7] & 0xFF;
		srcIp = formatIp6(data, off + 8);
		dstIp = formatIp6(data, off + 24);
	}

	// 16 octets -> "fe80:0:0:0:211:22ff:fe33:4455" (sans compression ::)
	static String formatIp6(byte[] t, int off) {
		String ip = "";
		for (int i = 0; i < 8; i++) {
			if (i != 0) {
				ip = ip + ":";
			}
			int groupe = Octets.lireInt16(t, off + i * 2, true);
			ip = ip + Integer.toHexString(groupe);
		}
		return ip;
	}
}
