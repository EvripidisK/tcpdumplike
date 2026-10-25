class Ethernet {
	String macDst, macSrc;
	int etherType;

	Ethernet(byte[] data, int off) {
		macDst = formatMac(data, off);
		macSrc = formatMac(data, off + 6);
		etherType = Octets.lireInt16(data, off + 12, true);
	}

	static String formatMac(byte[] t, int off) {
		String mac = "";
		for (int i = 0; i < 6; i++) {
			if (i != 0) {
				mac = mac + ":";
			}
			mac = mac + String.format("%02x", t[off + i] & 0xFF);
		}
		return mac;
	}
}
