class Dhcp {
	int op;
	long xid;
	String ciaddr, yiaddr, siaddr, giaddr;
	String macClient;
	int typeMessage;

	Dhcp(byte[] data, int off, int longueur) {
		op = data[off] & 0xFF;
		xid = Octets.lireInt32(data, off + 4, true);
		ciaddr = Ipv4.formatIp(data, off + 12);
		yiaddr = Ipv4.formatIp(data, off + 16);
		siaddr = Ipv4.formatIp(data, off + 20);
		giaddr = Ipv4.formatIp(data, off + 24);
		macClient = Ethernet.formatMac(data, off + 28);

		typeMessage = -1;
		int posCookie = off + 236;
		if (longueur >= 240) {
			long cookie = Octets.lireInt32(data, posCookie, true);
			if (cookie == 0x63825363L) {
				int pos = posCookie + 4;
				int fin = off + longueur;
				while (pos < fin) {
					int code = data[pos] & 0xFF;
					if (code == 255) {
						break;
					}
					if (code == 0) {
						pos++;
						continue;
					}
					int lon = data[pos + 1] & 0xFF;
					if (code == 53 && lon == 1) {
						typeMessage = data[pos + 2] & 0xFF;
					}
					pos = pos + 2 + lon;
				}
			}
		}
	}

	static String typeTexte(int type) {
		if (type == 1) {
			return "Discover";
		} else if (type == 2) {
			return "Offer";
		} else if (type == 3) {
			return "Request";
		} else if (type == 4) {
			return "Decline";
		} else if (type == 5) {
			return "Ack";
		} else if (type == 6) {
			return "Nak";
		} else if (type == 7) {
			return "Release";
		} else if (type == 8) {
			return "Inform";
		} else {
			return "type " + type;
		}
	}
}
