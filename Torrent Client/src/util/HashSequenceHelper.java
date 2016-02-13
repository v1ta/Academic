package util;

/**
 * Misc. methods for the application 
 * @author Joseph
 *
 */
public class HashSequenceHelper {
	
	public static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		
		if (bytes.length == 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder(bytes.length * 3);

		for (byte i : bytes) {
			byte upper = (byte) ((i >> 4) & 0x0f);
			byte lower = (byte) (i & 0x0f);

			sb.append('%').append(HEX[upper]).append(HEX[lower]);
		}
		return sb.toString();
	}

	public static boolean[] bitfieldToBoolArray(byte[] bitfield, int numPieces) {
		if (bitfield == null)
			return null;
		else {
			boolean[] retArray = new boolean[numPieces];
			for (int i = 0; i < retArray.length; i++) {
				int byteIndex = i / 8;
				int bitIndex = i % 8;

				if (((bitfield[byteIndex] << bitIndex) & 0x80) == 0x80)
					retArray[i] = true;
				else
					retArray[i] = false;

			}
			return retArray;
		}
	}

	public static byte[] boolToBitfieldArray(boolean[] verifiedPieces) {
		int length = verifiedPieces.length / 8;

		if (verifiedPieces.length % 8 != 0) {
			++length;
		}

		int index = 0;
		byte[] bitfield = new byte[length];

		for (int i = 0; i < bitfield.length; ++i) {
			for (int j = 7; j >= 0; --j) {

				if (index >= verifiedPieces.length) {
					return bitfield;
				}

				if (verifiedPieces[index++]) {
					bitfield[i] |= (byte) (1 << j);
				}
			}
		}
		return bitfield;
	}
}
