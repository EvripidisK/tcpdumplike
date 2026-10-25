import java.io.*;
 
public class FileOpen {
 
	// Parcourt tous les records après le global header.
	// Compte les records et signale où le fichier est coupé.
	static void parcourirRecords(DataInputStream dis, GlobalHeader gh) throws IOException {
		long offset = 24;
		int nbRecords = 0;
		int nbTronques = 0;
 
		while (true) {
 
			int premier = dis.read();
			if (premier == -1) {
				System.out.println("Fin du fichier : propre");
				break;
			}
 
			byte[] rh = new byte[16];
			rh[0] = (byte) premier;
			try {
				dis.readFully(rh, 1, 15);
			} catch (EOFException e) {
				System.out.println("Fichier coupé dans le record header du record "
						+ (nbRecords + 1) + " (offset " + offset + ")");
				break;
			}
 
			long inclLen = Octets.lireInt32(rh, 8, gh.isBigEndian);
			long origLen = Octets.lireInt32(rh, 12, gh.isBigEndian);
 
			if ((inclLen > gh.snaplen) || (inclLen > origLen)) {
				System.out.println("Record " + (nbRecords + 1) + " invalide (offset " + offset + ")");
				break;
			}
 
			byte[] data = new byte[(int) inclLen];
			try {
				dis.readFully(data);
			} catch (EOFException e) {
				System.out.println("Fichier coupé dans le Packet Data du record "
						+ (nbRecords + 1) + " (offset " + offset + ")");
				break;
			}
			nbRecords++;
			printHeaderRecords(rh, nbRecords, inclLen, origLen, gh);
			afficherHex(data);
 
			// test ethernet
			Decodeur.decoder(data, gh.linkType);
 
			if (inclLen < origLen) {
				nbTronques++;
			}
			offset += 16 + inclLen;
		}
 
		System.out.println("Records complets : " + nbRecords);
		System.out.println("Dont paquets tronqués par le snaplen : " + nbTronques);
		TcpStream.afficherTout();
	}
 
	static void printHeaderRecords(byte[] rh, int nbRecords, long inclLen, long origLen, GlobalHeader gh) {
		long tsSec  = Octets.lireInt32(rh, 0, gh.isBigEndian);
		long tsFrac = Octets.lireInt32(rh, 4, gh.isBigEndian);
		String fraction;
		String marque = "";
 
		if (gh.isNano)
			fraction = String.format("%09d", tsFrac);
		else
			fraction = String.format("%06d", tsFrac);
		if (inclLen < origLen)
			marque = " (tronqué)";
 
		System.out.println("#" + nbRecords + "  ts=" + tsSec + "." + fraction
				+ "  incl=" + inclLen + "  orig=" + origLen + marque);
	}
 
	static void afficherHex(byte[] data) {
		for (int i = 0; i < data.length; i += 16) {
			System.out.print(String.format("%04X  ", i));
			for (int j = i; j < i + 16 && j < data.length; j++)
				System.out.print(String.format("%02X ", data[j] & 0xFF));
			System.out.println();
		}
	}
 
	public static void main(String[] args) throws IOException {
		// Vérifier que l'on ait un seul argument
		if (args.length != 1) {
			System.err.println("usage : java FileOpen <fichier>");
			System.exit(1);
		}
		System.out.println("fichier : " + args[0]);
 
		try (DataInputStream dis = new DataInputStream(new FileInputStream(args[0]))) {
 
			byte[] header = new byte[24];
			dis.readFully(header);
 
			System.out.print("Global Header (hex) : ");
			for (byte b : header) {
				System.out.print(String.format("%02X ", b & 0xFF));
			}
			System.out.println();
 
			// Le constructeur reconnaît le magic number et lit tous les champs
			GlobalHeader gh;
			try {
				gh = new GlobalHeader(header);
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage() + " : ce n'est pas un pcap classique");
				System.exit(1);
				return;
			}
 
			System.out.println(String.format("Magic      : %08X", Octets.lireInt32(header, 0, true)));
			System.out.println("Ordre      : " + (gh.isBigEndian ? "big-endian" : "little-endian"));
			System.out.println("Précision  : " + (gh.isNano ? "nanosecondes" : "microsecondes"));
			System.out.println("Version    : " + gh.versionMajeure + "." + gh.versionMineure);
			System.out.println("Snaplen    : " + gh.snaplen);
			System.out.println("Link-type  : " + gh.linkType);
 
			parcourirRecords(dis, gh);
		}
	}
}
