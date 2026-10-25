class Arp {
	int typeProtocole;
	int opCode;
	String macSender;
	String macTarget;
	String ipSender;
	String ipTarget;

	Arp(byte[] data, int off) {
		typeProtocole = Octets.lireInt16(data, off + 2, true);
		opCode = Octets.lireInt16(data, off + 6, true);
		macSender = Ethernet.formatMac(data, off + 8);
		ipSender = Ipv4.formatIp(data, off + 14);
		macTarget = Ethernet.formatMac(data, off + 18);
		ipTarget = Ipv4.formatIp(data, off + 24);
	}

	String opCodeTexte() {
		if (opCode == 1) {
			return "request";
		} else if (opCode == 2) {
			return "reply";
		} else {
			return "inconnu(" + opCode + ")";
		}
	}

	String typeProtocoleTexte() {
		if (typeProtocole == 0x0800) {
			return "IPv4";
		}
		return String.format("0x%04X", typeProtocole);
	}
}
