class Http {
	boolean requete;
	String ligneDepart;
	String corps;

	Http(byte[] data, int off, int longueur) {
		String texte = new String(data, off, longueur);

		if (texte.startsWith("HTTP/1.")) {
			requete = false;
		} else {
			requete = true;
		}

		int finLigne = texte.indexOf("\r\n");
		if (finLigne >= 0) {
			ligneDepart = texte.substring(0, finLigne);
		} else {
			ligneDepart = texte;
		}

		int finEntetes = texte.indexOf("\r\n\r\n");
		if (finEntetes >= 0) {
			corps = texte.substring(finEntetes + 4);
		} else {
			corps = "";
		}
	}

	static boolean estHttp(byte[] data, int off, int longueur) {
		int n = longueur;
		if (n > 8) {
			n = 8;
		}
		String debut = new String(data, off, n);
		if (debut.startsWith("GET ") || debut.startsWith("POST ") || debut.startsWith("HEAD ")
				|| debut.startsWith("PUT ") || debut.startsWith("DELETE ") || debut.startsWith("HTTP/1.")) {
			return true;
		}
		return false;
	}
}
