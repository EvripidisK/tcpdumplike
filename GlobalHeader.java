import java.io.*;

public class GlobalHeader {

	boolean isBigEndian = false;
	boolean isNano = false;
	int versionMajeure;
	int versionMineure;
	long snaplen;
	int linkType;

	GlobalHeader(byte[] header) {
		// on lit les 4 premiers octets en big-endian et on compare aux 4 magic possibles
		long magic = Octets.lireInt32(header, 0, true);

		if (magic == 0xA1B2C3D4L) {
			isBigEndian = true;
			isNano = false;
		} else if (magic == 0xD4C3B2A1L) {
			isBigEndian = false;
			isNano = false;
		} else if (magic == 0xA1B23C4DL) {
			isBigEndian = true;
			isNano = true;
		} else if (magic == 0x4D3CB2A1L) {
			isBigEndian = false;
			isNano = true;
		} else {
			throw new IllegalArgumentException("Magic number inconnu");
		}

		versionMajeure = Octets.lireInt16(header, 4, isBigEndian);
		versionMineure = Octets.lireInt16(header, 6, isBigEndian);
		snaplen = Octets.lireInt32(header, 16, isBigEndian);
		linkType = Octets.lireInt16(header, 20, isBigEndian);
	}
}
