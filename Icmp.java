class Icmp {
	int type;
	int code;
	int identifier;
	int sequence;
	String origine;

	Icmp(byte[] data, int off, int finIp) {
		type = data[off] & 0xFF;
		code = data[off + 1] & 0xFF;

		if (type == 0 || type == 8) {
			identifier = Octets.lireInt16(data, off + 4, true);
			sequence = Octets.lireInt16(data, off + 6, true);
		} else if (type == 3 || type == 11 || type == 12) {
			int offOrig = off + 8;
			if (finIp - offOrig >= 20) {
				Ipv4 ipOrig = new Ipv4(data, offOrig);
				String desc = ipOrig.srcIp + " -> " + ipOrig.dstIp + "  (protocole " + ipOrig.protocol + ")";
				int offTransport = offOrig + ipOrig.ihl * 4;
				if ((ipOrig.protocol == 6 || ipOrig.protocol == 17) && finIp - offTransport >= 4) {
					int portSrc = Octets.lireInt16(data, offTransport, true);
					int portDst = Octets.lireInt16(data, offTransport + 2, true);
					if (ipOrig.protocol == 6) {
						desc = desc + "  TCP " + portSrc + " -> " + portDst;
					} else {
						desc = desc + "  UDP " + portSrc + " -> " + portDst;
					}
				}
				origine = desc;
			}
		}
	}

	String typeTexte() {
		if (type == 0) {
			return "Echo Reply";
		} else if (type == 8) {
			return "Echo Request";
		} else if (type == 3) {
			return "Destination Unreachable" + codeDestUnreachable();
		} else if (type == 11) {
			return "Time Exceeded";
		} else if (type == 5) {
			return "Redirect";
		} else if (type == 12) {
			return "Parameter Problem";
		} else {
			return "Type " + type;
		}
	}

	String codeDestUnreachable() {
		if (code == 0) {
			return " (Network Unreachable)";
		} else if (code == 1) {
			return " (Host Unreachable)";
		} else if (code == 3) {
			return " (Port Unreachable)";
		} else {
			return "";
		}
	}
}
