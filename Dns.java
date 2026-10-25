class Dns {
	int id;
	boolean reponse;
	int qdcount, ancount, nscount, arcount;
	String[] questionsNoms;
	int[] questionsTypes;
	String[] reponsesNoms;
	int[] reponsesTypes;
	String[] reponsesValeurs;

	Dns(byte[] data, int off) {
		id = Octets.lireInt16(data, off, true);
		int flags = Octets.lireInt16(data, off + 2, true);
		if ((flags & 0x8000) != 0) {
			reponse = true;
		} else {
			reponse = false;
		}
		qdcount = Octets.lireInt16(data, off + 4, true);
		ancount = Octets.lireInt16(data, off + 6, true);
		nscount = Octets.lireInt16(data, off + 8, true);
		arcount = Octets.lireInt16(data, off + 10, true);

		int pos = off + 12;

		questionsNoms = new String[qdcount];
		questionsTypes = new int[qdcount];
		for (int i = 0; i < qdcount; i++) {
			NomLu n = lireNom(data, pos, off);
			questionsNoms[i] = n.nom;
			pos = n.suite;
			questionsTypes[i] = Octets.lireInt16(data, pos, true);
			pos = pos + 4; // type (2) + classe (2)
		}

		reponsesNoms = new String[ancount];
		reponsesTypes = new int[ancount];
		reponsesValeurs = new String[ancount];
		for (int i = 0; i < ancount; i++) {
			NomLu n = lireNom(data, pos, off);
			reponsesNoms[i] = n.nom;
			pos = n.suite;

			int type = Octets.lireInt16(data, pos, true);
			reponsesTypes[i] = type;
			pos = pos + 8; // type (2) + classe (2) + ttl (4)

			int rdlength = Octets.lireInt16(data, pos, true);
			pos = pos + 2;

			if (type == 1 && rdlength == 4) {
				reponsesValeurs[i] = Ipv4.formatIp(data, pos);
			} else {
				reponsesValeurs[i] = "type " + type + " (non détaillé)";
			}
			pos = pos + rdlength;
		}
	}

	static class NomLu {
		String nom;
		int suite;

		NomLu(String nom, int suite) {
			this.nom = nom;
			this.suite = suite;
		}
	}

	// off = position de lecture, base = début du message DNS
	// (les pointeurs de compression sont relatifs à base)
	static NomLu lireNom(byte[] data, int off, int base) {
		String nom = "";
		int pos = off;
		int finLecture = -1;
		boolean premier = true;
		int garde = 0;
		while (garde < 128) {
			garde++;
			int b = data[pos] & 0xFF;
			if (b == 0) {
				if (finLecture == -1) {
					finLecture = pos + 1;
				}
				break;
			}
			if ((b & 0xC0) == 0xC0) {
				int pointeur = ((b & 0x3F) << 8) | (data[pos + 1] & 0xFF);
				if (finLecture == -1) {
					finLecture = pos + 2;
				}
				pos = base + pointeur;
				continue;
			}
			if (!premier) {
				nom = nom + ".";
			}
			for (int i = 1; i <= b; i++) {
				nom = nom + (char) (data[pos + i] & 0xFF);
			}
			pos = pos + b + 1;
			premier = false;
		}
		return new NomLu(nom, finLecture);
	}

	static String typeTexte(int type) {
		if (type == 1) {
			return "A";
		} else if (type == 2) {
			return "NS";
		} else if (type == 5) {
			return "CNAME";
		} else if (type == 15) {
			return "MX";
		} else if (type == 16) {
			return "TXT";
		} else if (type == 28) {
			return "AAAA";
		} else {
			return "type " + type;
		}
	}
}
