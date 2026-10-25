import java.util.*;

public class TcpStream {

	static class Segment {
		boolean sensA;
		byte[] donnees;

		Segment(boolean sensA, byte[] donnees) {
			this.sensA = sensA;
			this.donnees = donnees;
		}
	}

	// tous les flux vus : clé -> liste des segments dans l'ordre d'arrivée
	static Map<String, List<Segment>> flux = new LinkedHashMap<>();

	static String cle(String ipSrc, int portSrc, String ipDst, int portDst) {
		String bout1 = ipSrc + ":" + portSrc;
		String bout2 = ipDst + ":" + portDst;
		if (bout1.compareTo(bout2) <= 0) {
			return bout1 + "|" + bout2;
		} else {
			return bout2 + "|" + bout1;
		}
	}

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

	static void afficherTout() {
		for (Map.Entry<String, List<Segment>> entree : flux.entrySet()) {
			System.out.println("=== Flux " + entree.getKey() + " ===");
			List<Segment> liste = entree.getValue();
			for (int i = 0; i < liste.size(); i++) {
				Segment s = liste.get(i);
				if (s.donnees.length == 0) {
					continue;
				}
				if (s.sensA) {
					System.out.print("[A] ");
				} else {
					System.out.print("[B] ");
				}
				System.out.println(new String(s.donnees));
			}
			System.out.println();
		}
	}
}
