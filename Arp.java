class Arp {
	int typeProtocole;
	int opCode;
	String macSender;
	String macTarget;
	String ipSender;
	String ipTarget;

	Arp(byte[] data, int off) {
		this.typeProtocole = Octets.lireInt16(data, off + 2, true);
		this.opCode = Octets.lireInt16(data, off + 6, true);
		this.macSender = Ethernet.formatMac(data, off + 8);
		this.ipSender = Ipv4.formatIp(data, off + 14);
		this.macTarget = Ethernet.formatMac(data, off + 18);
		this.ipTarget = Ipv4.formatIp(data, off + 24);
	}

	String opCodeTexte() {
		switch (opCode) {
			case 1: return "request";
			case 2: return "reply";
			default: return "inconnu(" + opCode + ")";
		}
	}

	String typeProtocoleTexte() {
		switch (typeProtocole) {
			case 0x0800: return "IPv4";
			default: return String.format("0x%04X", typeProtocole);
		}
	}

	int taille() { return 28; }
}
