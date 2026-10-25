public class Octets {
	// Assemble 4 octets en un entier non signé (rangé dans un long)
	static long lireInt32(byte[] t, int off, boolean bigEndian) {
		long b0 = t[off]     & 0xFF;
		long b1 = t[off + 1] & 0xFF;
		long b2 = t[off + 2] & 0xFF;
		long b3 = t[off + 3] & 0xFF;
		if (bigEndian) {
			return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
		} else {
			return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
		}
	}

	// Assemble 2 octets en un entier non signé (0 à 65535)
	static int lireInt16(byte[] t, int off, boolean bigEndian) {
		int b0 = t[off]     & 0xFF;
		int b1 = t[off + 1] & 0xFF;
		if (bigEndian) {
			return (b0 << 8) | b1;
		} else {
			return (b1 << 8) | b0;
		}
	}
}
