class Ipv4 {
	int version;
	int ihl;
	int totalLength;
	int protocol;
	String srcIp, dstIp;

	Ipv4(byte[] data, int off) {
		this.version     = (data[off] & 0xFF) >> 4;
		this.ihl         = data[off] & 0x0F;
		this.totalLength = Octets.lireInt16(data, off + 2, true);
		this.protocol    = data[off + 9] & 0xFF;
		this.srcIp       = formatIp(data, off + 12);
		this.dstIp       = formatIp(data, off + 16);
	}

	// 4 octets -> "192.168.1.10"
	static String formatIp(byte[] t, int off) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			if (i != 0)
				sb.append('.');
			sb.append(t[off + i] & 0xFF);
		}
		return sb.toString();
	}

	// Taille de l'en-tête en octets (IHL est en mots de 4 octets)
	int taille() { return ihl * 4; }

	// Position (dans data) où s'arrête ce paquet IP
	int fin(int off) { return off + totalLength; }
}
