import java.io.*;

public class GlobalHeader {

	// Les deux informations déduites du magic number
	boolean isBigEndian = false;
	boolean isNano = false;
	int versionMajeure; 
	int versionMineure;
	long snaplen;
	int linkType;
	
	GlobalHeader(byte[] header) {
		if (!checkMagicNumber(header)) {
		// magic inconnu : on ne peut pas continuer
			throw new IllegalArgumentException("Magic number inconnu");
		}
		this.versionMajeure = Octets.lireInt16(header,4,isBigEndian);
		this.versionMineure = Octets.lireInt16(header,6,isBigEndian);
		this.snaplen        = Octets.lireInt32(header,16,isBigEndian);
		this.linkType       = (int) Octets.lireInt16(header,20,isBigEndian);
	}


	// Lit les 4 premiers octets comme du big-endian 
	// puis compare la valeur obtenue aux 4 magic possibles.
	boolean checkMagicNumber(byte[] header) {
		long magic = Octets.lireInt32(header, 0, true);

		if (magic == 0xA1B2C3D4L) {         
			isBigEndian = true;  isNano = false;
		} else if (magic == 0xD4C3B2A1L) { 
			isBigEndian = false; isNano = false;
		} else if (magic == 0xA1B23C4DL) {
			isBigEndian = true;  isNano = true;
		} else if (magic == 0x4D3CB2A1L) { 
			isBigEndian = false; isNano = true;
		} else {
			return false;
		}
		return true;
	}
}

