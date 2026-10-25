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
		this.id      = Octets.lireInt16(data, off, true);
		int flags    = Octets.lireInt16(data, off + 2, true);
		this.reponse = (flags & 0x8000) != 0;
		this.qdcount = Octets.lireInt16(data, off + 4, true);
		this.ancount = Octets.lireInt16(data, off + 6, true);
		this.nscount = Octets.lireInt16(data, off + 8, true);
		this.arcount = Octets.lireInt16(data, off + 10, true);

		int pos = off + 12;

		questionsNoms  = new String[qdcount];
		questionsTypes = new int[qdcount];
		for (int i = 0; i < qdcount; i++) {
			NomLu n = lireNom(data, pos, off);
			questionsNoms[i] = n.nom;
			pos = n.suite;
			questionsTypes[i] = Octets.lireInt16(data, pos, true);
			pos += 4; // type (2) + classe (2)
		}

		reponsesNoms    = new String[ancount];
		reponsesTypes   = new int[ancount];
		reponsesValeurs = new String[ancount];
		for (int i = 0; i < ancount; i++) {
			NomLu n = lireNom(data, pos, off);
			reponsesNoms[i] = n.nom;
			pos = n.suite;

			int type = Octets.lireInt16(data, pos, true);
			reponsesTypes[i] = type;
			pos += 2; // type
			pos += 2; // classe
			pos += 4; // ttl

			int rdlength = Octets.lireInt16(data, pos, true);
			pos += 2;

			if (type == 1 && rdlength == 4) {
				reponsesValeurs[i] = Ipv4.formatIp(data, pos);
			} else {
				reponsesValeurs[i] = "type " + type + " (non détaillé)";
			}
			pos += rdlength;
		}
	}

	static class NomLu {
		String nom;
		int suite;
		NomLu(String nom, int suite) { this.nom = nom; this.suite = suite; }
	}

	// off = position actuelle de lecture, base = début du message DNS dans data
	// (les pointeurs de compression sont relatifs à ce "base", pas à off ni à 0)
	static NomLu lireNom(byte[] data, int off, int base) {
		StringBuilder sb = new StringBuilder();
		int pos = off;
		int finLecture = -1;
		boolean premier = true;
		int garde = 0;
		while (garde++ < 128) {
			int b = data[pos] & 0xFF;
			if (b == 0) {
				if (finLecture == -1) finLecture = pos + 1;
				break;
			}
			if ((b & 0xC0) == 0xC0) {
				int pointeur = ((b & 0x3F) << 8) | (data[pos + 1] & 0xFF);
				if (finLecture == -1) finLecture = pos + 2;
				pos = base + pointeur;
				continue;
			}
			if (!premier) sb.append('.');
			for (int i = 1; i <= b; i++) sb.append((char) (data[pos + i] & 0xFF));
			pos += b + 1;
			premier = false;
		}
		return new NomLu(sb.toString(), finLecture);
	}

	static String typeTexte(int type) {
		switch (type) {
			case 1:  return "A";
			case 2:  return "NS";
			case 5:  return "CNAME";
			case 15: return "MX";
			case 16: return "TXT";
			case 28: return "AAAA";
			default: return "type " + type;
		}
	}
}
