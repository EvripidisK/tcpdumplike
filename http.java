class Http {
	boolean requete;
	String ligneDepart;
	String corps;

	Http(byte[] data, int off, int longueur) {
		String texte = new String(data, off, longueur);

		this.requete = !texte.startsWith("HTTP/1.");

		int finLigne = texte.indexOf("\r\n");
		this.ligneDepart = (finLigne >= 0) ? texte.substring(0, finLigne) : texte;

		int finEntetes = texte.indexOf("\r\n\r\n");
		this.corps = (finEntetes >= 0) ? texte.substring(finEntetes + 4) : "";
	}

	static boolean estHttp(byte[] data, int off, int longueur) {
		String debut = new String(data, off, Math.min(longueur, 8));
		return debut.startsWith("GET ") || debut.startsWith("POST ") || debut.startsWith("HEAD ")
			|| debut.startsWith("PUT ") || debut.startsWith("DELETE ") || debut.startsWith("HTTP/1.");
	}
}
