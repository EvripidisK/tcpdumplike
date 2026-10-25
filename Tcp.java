class Tcp {
	int tcpSrc, tcpDst;
	long seq, ack;          // 32 bits non signés -> long
	int dataOffset;         // en mots de 4 octets
	int flags;              // octet 13 : CWR ECE URG ACK PSH RST SYN FIN
	int window;
	int tcpChecksum;
	int urgentPtr;
 
	Tcp(byte[] data, int off) {
		this.tcpSrc      = Octets.lireInt16(data, off,      true);
		this.tcpDst      = Octets.lireInt16(data, off + 2,  true);
		this.seq         = Octets.lireInt32(data, off + 4,  true);
		this.ack         = Octets.lireInt32(data, off + 8,  true);
		this.dataOffset  = (data[off + 12] & 0xFF) >> 4;
		this.flags       = data[off + 13] & 0xFF;
		this.window      = Octets.lireInt16(data, off + 14, true);
		this.tcpChecksum = Octets.lireInt16(data, off + 16, true);
		this.urgentPtr   = Octets.lireInt16(data, off + 18, true);
	}
 
	// Taille de l'en-tête en octets (Data Offset est en mots de 4 octets)
	int taille() { return dataOffset * 4; }
 
	// TCP n'a pas de champ longueur : les données vont de off + taille()
	// jusqu'à la fin du paquet IP (finIp, fournie par Ipv4.fin(...)).
	int longueurDonnees(int off, int finIp) { return finIp - (off + taille()); }
 
	boolean fin() { return (flags & 0x01) != 0; }
	boolean syn() { return (flags & 0x02) != 0; }
	boolean rst() { return (flags & 0x04) != 0; }
	boolean psh() { return (flags & 0x08) != 0; }
	boolean ackFlag() { return (flags & 0x10) != 0; }
	boolean urg() { return (flags & 0x20) != 0; }
 
	// Texte lisible des flags, par exemple "SYN,ACK"
	String flagsTexte() {
		StringBuilder sb = new StringBuilder();
		if (syn())     sb.append("SYN,");
		if (fin())     sb.append("FIN,");
		if (rst())     sb.append("RST,");
		if (psh())     sb.append("PSH,");
		if (ackFlag()) sb.append("ACK,");
		if (urg())     sb.append("URG,");
		if (sb.length() == 0) return "-";
		sb.setLength(sb.length() - 1);   // enlève la dernière virgule
		return sb.toString();
	}
}
 

