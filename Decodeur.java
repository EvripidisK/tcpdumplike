public class Decodeur {

	static void decoder(byte[] data, int linkType) {
		if (linkType != 1) {
			return;
		}
		if (data.length < 14) {
			return;
		}

		int off = 0;
		Ethernet eth = new Ethernet(data, off);
		System.out.println(eth.macDst + " <- " + eth.macSrc);
		System.out.printf("EtherType = 0x%04X%n", eth.etherType);
		off = off + 14;

		if (eth.etherType == 0x0800) {
			decoderIpv4(data, off);
		} else if (eth.etherType == 0x0806) {
			decoderArp(data, off);
		} else if (eth.etherType == 0x86DD) {
			decoderIpv6(data, off);
		} else {
			System.out.printf("EtherType inconnu : 0x%04X, on s'arrête%n", eth.etherType);
		}
	}

	static void decoderArp(byte[] data, int off) {
		if (data.length < off + 28) {
			System.out.println("ARP tronqué : en-tête incomplet");
			return;
		}

		Arp arp = new Arp(data, off);

		System.out.println("ARP " + arp.opCodeTexte() + "  (" + arp.typeProtocoleTexte() + ")"
				+ "  sender=" + arp.macSender + " / " + arp.ipSender
				+ "  target=" + arp.macTarget + " / " + arp.ipTarget);
	}

	// Décode l'en-tête IPv4 qui commence à l'offset off de data.
	static void decoderIpv4(byte[] data, int off) {
		int offIp = off;

		if (data.length < off + 20) {
			System.out.println("IPv4 tronqué : en-tête incomplet");
			return;
		}

		Ipv4 ip = new Ipv4(data, off);

		if (ip.version != 4 || ip.ihl < 5) {
			System.out.println("IPv4 invalide (version=" + ip.version + ", ihl=" + ip.ihl + ")");
			return;
		}

		int tailleEntete = ip.ihl * 4;

		if (data.length < off + tailleEntete) {
			System.out.println("IPv4 tronqué : options incomplètes");
			return;
		}
		if (ip.totalLength < tailleEntete) {
			System.out.println("IPv4 invalide : Total Length (" + ip.totalLength + ") < en-tête");
			return;
		}

		int finIp = offIp + ip.totalLength;
		String note = "";
		if (finIp > data.length) {
			finIp = data.length;
			note = "  (capture tronquée)";
		}

		System.out.println("IPv4 " + ip.srcIp + " -> " + ip.dstIp
				+ "  protocole=" + ip.protocol + "  en-tête=" + tailleEntete
				+ " octets  total=" + ip.totalLength + note);
		off = off + tailleEntete;

		if (ip.protocol == 6) {
			decoderTcp(data, off, finIp, ip.srcIp, ip.dstIp);
		} else if (ip.protocol == 17) {
			decoderUdp(data, off, finIp);
		} else if (ip.protocol == 1) {
			System.out.println("ICMP à partir de l'offset " + off + " (non géré)");
		} else {
			System.out.println("Protocole IP " + ip.protocol + " non géré");
		}
	}

	// Décode l'en-tête IPv6 qui commence à l'offset off de data.
	static void decoderIpv6(byte[] data, int off) {
		if (data.length < off + 40) {
			System.out.println("IPv6 tronqué : en-tête incomplet");
			return;
		}

		Ipv6 ip6 = new Ipv6(data, off);

		if (ip6.version != 6) {
			System.out.println("IPv6 invalide (version=" + ip6.version + ")");
			return;
		}

		int finIp = off + 40 + ip6.payloadLength;
		String note = "";
		if (finIp > data.length) {
			finIp = data.length;
			note = "  (capture tronquée)";
		}

		System.out.println("IPv6 " + ip6.srcIp + " -> " + ip6.dstIp
				+ "  nextHeader=" + ip6.nextHeader + "  hopLimit=" + ip6.hopLimit
				+ "  payload=" + ip6.payloadLength + note);
		off = off + 40;

		if (ip6.nextHeader == 6) {
			decoderTcp(data, off, finIp, ip6.srcIp, ip6.dstIp);
		} else if (ip6.nextHeader == 17) {
			decoderUdp(data, off, finIp);
		} else if (ip6.nextHeader == 58) {
			System.out.println("ICMPv6 à partir de l'offset " + off + " (non géré)");
		} else {
			System.out.println("Next Header " + ip6.nextHeader + " non géré");
		}
	}

	// Décode l'en-tête UDP qui commence à l'offset off. finIp = fin du paquet IP.
	static void decoderUdp(byte[] data, int off, int finIp) {
		if (finIp - off < 8) {
			System.out.println("UDP tronqué : en-tête incomplet");
			return;
		}

		Udp udp = new Udp(data, off);

		if (udp.udpLength < 8) {
			System.out.println("UDP invalide : length=" + udp.udpLength);
			return;
		}

		System.out.println("UDP " + udp.udpSrc + " -> " + udp.udpDst
				+ "  length=" + udp.udpLength);
		off = off + 8;
		int donnees = udp.udpLength - 8;
		System.out.println("Données UDP : " + donnees
				+ " octets, à partir de l'offset " + off);

		if (donnees >= 12 && (udp.udpSrc == 53 || udp.udpDst == 53)) {
			decoderDns(data, off, donnees);
		}
		if (donnees >= 236 && (udp.udpSrc == 67 || udp.udpSrc == 68
				|| udp.udpDst == 67 || udp.udpDst == 68)) {
			decoderDhcp(data, off, donnees);
		}
	}

	// Décode un message DNS qui commence à l'offset off, sur "donnees" octets.
	static void decoderDns(byte[] data, int off, int donnees) {
		if (donnees < 12) {
			System.out.println("DNS tronqué : en-tête incomplet");
			return;
		}

		Dns dns = new Dns(data, off);

		String sens;
		if (dns.reponse) {
			sens = "réponse";
		} else {
			sens = "requête";
		}
		System.out.println("DNS " + sens
				+ "  id=" + dns.id
				+ "  questions=" + dns.qdcount + "  réponses=" + dns.ancount);

		for (int i = 0; i < dns.questionsNoms.length; i++) {
			System.out.println("  Question : " + dns.questionsNoms[i]
					+ "  type=" + Dns.typeTexte(dns.questionsTypes[i]));
		}

		for (int i = 0; i < dns.reponsesNoms.length; i++) {
			System.out.println("  Réponse : " + dns.reponsesNoms[i]
					+ "  type=" + Dns.typeTexte(dns.reponsesTypes[i])
					+ "  -> " + dns.reponsesValeurs[i]);
		}
	}

	// Décode un message DHCP qui commence à l'offset off, sur "donnees" octets.
	static void decoderDhcp(byte[] data, int off, int donnees) {
		if (donnees < 236) {
			System.out.println("DHCP tronqué : message incomplet");
			return;
		}

		Dhcp dhcp = new Dhcp(data, off, donnees);

		String type;
		if (dhcp.op == 1) {
			type = "BOOTREQUEST";
		} else {
			type = "BOOTREPLY";
		}
		String typeMessage = "";
		if (dhcp.typeMessage >= 0) {
			typeMessage = "  " + Dhcp.typeTexte(dhcp.typeMessage);
		}

		System.out.println("DHCP " + type + typeMessage
				+ "  xid=0x" + Long.toHexString(dhcp.xid));
		System.out.println("  client=" + dhcp.macClient
				+ "  ciaddr=" + dhcp.ciaddr + "  yiaddr=" + dhcp.yiaddr);
	}

	// Décode l'en-tête TCP qui commence à l'offset off. finIp = fin du paquet IP.
	// srcIp et dstIp viennent de la couche IP (IPv4 ou IPv6) qui a appelé cette méthode.
	static void decoderTcp(byte[] data, int off, int finIp, String srcIp, String dstIp) {
		if (finIp - off < 20) {
			System.out.println("TCP tronqué : en-tête incomplet");
			return;
		}

		Tcp tcp = new Tcp(data, off);

		if (tcp.dataOffset < 5) {
			System.out.println("TCP invalide : dataOffset=" + tcp.dataOffset);
			return;
		}

		int tailleEntete = tcp.dataOffset * 4;

		if (finIp - off < tailleEntete) {
			System.out.println("TCP tronqué : options incomplètes");
			return;
		}

		int donnees = finIp - (off + tailleEntete);

		System.out.println("TCP " + tcp.tcpSrc + " -> " + tcp.tcpDst
				+ "  [" + tcp.flagsTexte() + "]"
				+ "  seq=" + tcp.seq + "  ack=" + tcp.ack
				+ "  win=" + tcp.window + "  en-tête=" + tailleEntete + " octets");
		off = off + tailleEntete;
		System.out.println("Données TCP : " + donnees + " octets, à partir de l'offset " + off);

		if (donnees > 0) {
			byte[] morceau = java.util.Arrays.copyOfRange(data, off, off + donnees);
			TcpStream.ajouter(srcIp, tcp.tcpSrc, dstIp, tcp.tcpDst, morceau);
		}

		if (donnees > 0 && (tcp.tcpSrc == 80 || tcp.tcpDst == 80)) {
			if (Http.estHttp(data, off, donnees)) {
				decoderHttp(data, off, donnees);
			}
		}
	}

	// Décode un message HTTP qui commence à l'offset off, sur "longueur" octets.
	static void decoderHttp(byte[] data, int off, int longueur) {
		Http http = new Http(data, off, longueur);

		String type;
		if (http.requete) {
			type = "requête";
		} else {
			type = "réponse";
		}
		System.out.println("HTTP " + type + "  : " + http.ligneDepart);
		System.out.println("Corps HTTP : " + http.corps.length() + " octets");
	}
}
