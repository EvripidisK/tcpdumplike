class Ethernet {
	String macDst, macSrc;
	int etherType;

        Ethernet(byte[] data, int off) {
                this.macDst = formatMac(data, off);
                this.macSrc = formatMac(data, off + 6);
                this.etherType = Octets.lireInt16(data, off + 12, true);

        }
        static String formatMac(byte[] t, int off) {
                 StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                        if (i != 0)
                                sb.append(':');
                        sb.append(String.format("%02x", t[off + i]& 0xFF));
                }
        return sb.toString();
        }
	    int taille() { return 14; }
}
