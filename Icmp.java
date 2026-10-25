class Icmp {
	int type;
	int code;
	int identifier;
	int sequence;
	String origine;

	Icmp(byte[] data, int off, int finIp) {
		this.type = data[off] & 0xFF;
		this.code = data[off + 1] & 0xFF;

		if (estEcho()) {
			this.identifier = Octets.lireInt16(data, off + 4, true);
			this.sequence   = Octets.lireInt16(data, off + 6, true);
		} else if (estErreur()) {
			int offOrig = off + 8;
			if (finIp - offOrig >= 20) {
				Ipv4 ipOrig = new Ipv4(data, offOrig);
				String desc = ipOrig.srcIp + " -> " + ipOrig.dstIp + "  (protocole " + ipOrig.protocol + ")";
				int offTransport = offOrig + ipOrig.taille();
				if ((ipOrig.protocol == 6 || ipOrig.protocol == 17) && finIp - offTransport >= 4) {
					int portSrc = Octets.lireInt16(data, offTransport, true);
					int portDst = Octets.lireInt16(data, offTransport + 2, true);
					desc += "  " + (ipOrig.protocol == 6 ? "TCP " : "UDP ") + portSrc + " -> " + portDst;
				}
				this.origine = desc;
			}
		}
	}

	boolean estEcho()   { return type == 0 || type == 8; }
	boolean estErreur() { return type == 3 || type == 11 || type == 12; }

	String typeTexte() {
		switch (type) {
			case 0:  return "Echo Reply";
			case 8:  return "Echo Request";
			case 3:  return "Destination Unreachable" + codeDestUnreachable();
			case 11: return "Time Exceeded";
			case 5:  return "Redirect";
			case 12: return "Parameter Problem";
			default: return "Type " + type;
		}
	}

	String codeDestUnreachable() {
		switch (code) {
			case 0:  return " (Network Unreachable)";
			case 1:  return " (Host Unreachable)";
			case 3:  return " (Port Unreachable)";
			default: return "";
		}
	}

	int taille() { return estEcho() ? 8 : 4; }
}
