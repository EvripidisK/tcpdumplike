class Udp {
        int udpDst, udpSrc, udpLength, udpChecksum;

        Udp(byte[] data, int off) {
                this.udpSrc = Octets.lireInt16(data, off , true);
                this.udpDst =Octets.lireInt16(data, off + 2 , true);
                this.udpLength =Octets.lireInt16(data, off + 4 , true);
                this.udpChecksum = Octets.lireInt16(data,off +6,true);
        }
		int taille() { return 8; }
		int fin(int off) { return off + udpLength; }
}

