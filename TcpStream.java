import java.util.*;

public class TcpStream {

	static class Segment {
		boolean sensA;   // vrai si ce segment vient du même sens que le premier paquet du flux (le "A")
		byte[] donnees;

		Segment(boolean sensA, byte[] donnees) {
			this.sensA = sensA;
			this.donnees = donnees;
		}
	}

	// Mémoire de tous les flux vus : clé canonique -> liste de ses segments, dans l'ordre d'arrivée
	static Map<String, List<Segment>> flux = new LinkedHashMap<>();

	// Construit la clé canonique pour une paire (source, destination) donnée
	static String cle(String ipSrc, int portSrc, String ipDst, int portDst) {
		String bout1 = ipSrc + ":" + portSrc;
		String bout2 = ipDst + ":" + portDst;
		if (bout1.compareTo(bout2) <= 0) {
			return bout1 + "|" + bout2;
		} else {
			return bout2 + "|" + bout1;
		}
	}

	// Enregistre un segment TCP (à appeler depuis decoderTcp, pour chaque segment ayant des données)
	static void ajouter(String ipSrc, int portSrc, String ipDst, int portDst, byte[] donnees) {
		String cle = cle(ipSrc, portSrc, ipDst, portDst);

		String bout1 = ipSrc + ":" + portSrc;
		String bout2 = ipDst + ":" + portDst;
		boolean sensA = bout1.compareTo(bout2) <= 0;

		List<Segment> liste = flux.get(cle);
		if (liste == null) {
			liste = new ArrayList<>();
			flux.put(cle, liste);
		}
		liste.add(new Segment(sensA, donnees));
	}

	// À appeler une fois que tous les enregistrements du fichier ont été lus
	static void afficherTout() {
		for (Map.Entry<String, List<Segment>> entree : flux.entrySet()) {
			System.out.println("=== Flux " + entree.getKey() + " ===");
			for (Segment s : entree.getValue()) {
				if (s.donnees.length == 0) continue;
				System.out.print(s.sensA ? "[A] " : "[B] ");
				System.out.println(new String(s.donnees));
			}
			System.out.println();
		}
	}
}
