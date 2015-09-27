package util;

/**
 * Misc. methods for the application 
 * @author Joseph
 *
 */
public class Util {
	
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
}
