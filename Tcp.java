class Tcp {
	int tcpSrc, tcpDst;
	long seq, ack;
	int dataOffset;
	int flags;
	int window;
	int tcpChecksum;
	int urgentPtr;

	Tcp(byte[] data, int off) {
		tcpSrc = Octets.lireInt16(data, off, true);
		tcpDst = Octets.lireInt16(data, off + 2, true);
		seq = Octets.lireInt32(data, off + 4, true);
		ack = Octets.lireInt32(data, off + 8, true);
		dataOffset = (data[off + 12] & 0xFF) >> 4;
		flags = data[off + 13] & 0xFF;
		window = Octets.lireInt16(data, off + 14, true);
		tcpChecksum = Octets.lireInt16(data, off + 16, true);
		urgentPtr = Octets.lireInt16(data, off + 18, true);
	}

	String flagsTexte() {
		String s = "";
		if ((flags & 0x02) != 0) {
			s = s + "SYN,";
		}
		if ((flags & 0x01) != 0) {
			s = s + "FIN,";
		}
		if ((flags & 0x04) != 0) {
			s = s + "RST,";
		}
		if ((flags & 0x08) != 0) {
			s = s + "PSH,";
		}
		if ((flags & 0x10) != 0) {
			s = s + "ACK,";
		}
		if ((flags & 0x20) != 0) {
			s = s + "URG,";
		}
		if (s.length() == 0) {
			return "-";
		}
		return s.substring(0, s.length() - 1);
	}
}
