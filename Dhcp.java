class Dhcp {
	int op;
	long xid;
	String ciaddr, yiaddr, siaddr, giaddr;
	String macClient;
	int typeMessage;

	Dhcp(byte[] data, int off, int longueur) {
		this.op     = data[off] & 0xFF;
		this.xid    = Octets.lireInt32(data, off + 4, true);
		this.ciaddr = Ipv4.formatIp(data, off + 12);
		this.yiaddr = Ipv4.formatIp(data, off + 16);
		this.siaddr = Ipv4.formatIp(data, off + 20);
		this.giaddr = Ipv4.formatIp(data, off + 24);
		this.macClient = Ethernet.formatMac(data, off + 28);

		this.typeMessage = -1;
		int posCookie = off + 236;
		if (longueur >= 240) {
			long cookie = Octets.lireInt32(data, posCookie, true);
			if (cookie == 0x63825363L) {
				int pos = posCookie + 4;
				int fin = off + longueur;
				while (pos < fin) {
					int code = data[pos] & 0xFF;
					if (code == 255) break;
					if (code == 0) { pos++; continue; }
					int lon = data[pos + 1] & 0xFF;
					if (code == 53 && lon == 1) {
						this.typeMessage = data[pos + 2] & 0xFF;
					}
					pos += 2 + lon;
				}
			}
		}
	}

	static String typeTexte(int type) {
		switch (type) {
			case 1: return "Discover";
			case 2: return "Offer";
			case 3: return "Request";
			case 4: return "Decline";
			case 5: return "Ack";
			case 6: return "Nak";
			case 7: return "Release";
			case 8: return "Inform";
			default: return "type " + type;
		}
	}

	int taille() { return 236; }
}
