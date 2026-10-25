class Udp {
	int udpDst, udpSrc, udpLength, udpChecksum;

	Udp(byte[] data, int off) {
		udpSrc = Octets.lireInt16(data, off, true);
		udpDst = Octets.lireInt16(data, off + 2, true);
		udpLength = Octets.lireInt16(data, off + 4, true);
		udpChecksum = Octets.lireInt16(data, off + 6, true);
	}
}
