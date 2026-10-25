class Ipv6 {
	int version;
	int payloadLength;
	int nextHeader;
	int hopLimit;
	String srcIp, dstIp;

	Ipv6(byte[] data, int off) {
		this.version       = (data[off] & 0xFF) >> 4;
		this.payloadLength = Octets.lireInt16(data, off + 4, true);
		this.nextHeader    = data[off + 6] & 0xFF;
		this.hopLimit       = data[off + 7] & 0xFF;
		this.srcIp          = formatIp6(data, off + 8);
		this.dstIp          = formatIp6(data, off + 24);
	}

	// 16 octets -> "fe80:0:0:0:211:22ff:fe33:4455" (sans compression ::)
	static String formatIp6(byte[] t, int off) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 8; i++) {
			if (i != 0)
				sb.append(':');
			int groupe = Octets.lireInt16(t, off + i * 2, true);
			sb.append(Integer.toHexString(groupe));
		}
		return sb.toString();
	}

	// Taille de l'en-tête IPv6 : toujours fixe, 40 octets (hors extension headers)
	int taille() { return 40; }

	// Position (dans data) où s'arrête ce paquet IPv6
	int fin(int off) { return off + 40 + payloadLength; }
}
