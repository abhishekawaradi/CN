import java.util.Scanner;

/**
 * CRC-CCITT (16-bit) using the polynomial long-division method from the lab.
 * Polynomial: x^16 + x^12 + x^5 + 1 (0x1021), with a zero initial remainder.
 */
public class CRC1 {
    private static final String GENERATOR = "10001000000100001";
    private static final int CRC_WIDTH = 16;

    private static boolean isBinary(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) != '0' && value.charAt(i) != '1') {
                return false;
            }
        }
        return true;
    }

    /** Divide by the generator and return the last 16 remainder bits. */
    private static String calculateRemainder(String message) {
        StringBuilder padded = new StringBuilder(message);
        for (int i = 0; i < CRC_WIDTH; i++) {
            padded.append('0');
        }
        char[] data = padded.toString().toCharArray();
        char[] divisor = GENERATOR.toCharArray();

        for (int i = 0; i < message.length(); i++) {
            if (data[i] == '1') {
                for (int j = 0; j < divisor.length; j++) {
                    data[i + j] = data[i + j] == divisor[j] ? '0' : '1';
                }
            }
        }
        return new String(data, message.length(), CRC_WIDTH);
    }

    /** A received frame is valid when its modulo-2 remainder is all zeroes. */
    private static boolean hasZeroRemainder(String codeword) {
        char[] data = codeword.toCharArray();
        char[] divisor = GENERATOR.toCharArray();
        int messageLength = codeword.length() - CRC_WIDTH;

        for (int i = 0; i < messageLength; i++) {
            if (data[i] == '1') {
                for (int j = 0; j < divisor.length; j++) {
                    data[i + j] = data[i + j] == divisor[j] ? '0' : '1';
                }
            }
        }
        for (int i = messageLength; i < data.length; i++) {
            if (data[i] != '0') {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter message bits: ");
            String message = scanner.nextLine().trim();
            if (!isBinary(message)) {
                System.out.println("Message must contain one or more bits (0 or 1). ");
                return;
            }

            String checksum = calculateRemainder(message);
            String transmitted = message + checksum;
            System.out.println("Generator (CRC-CCITT): " + GENERATOR);
            System.out.println("Checksum (16 bits): " + checksum);
            System.out.println("Transmitted codeword: " + transmitted);

            System.out.print("Enter received codeword (Enter to verify the generated one): ");
            String received = scanner.nextLine().trim();
            if (received.isEmpty()) {
                received = transmitted;
            }
            if (received.length() <= CRC_WIDTH || !isBinary(received)) {
                System.out.println("Codeword must contain data bits followed by 16 CRC bits.");
                return;
            }

            if (hasZeroRemainder(received)) {
                System.out.println("Data stream is valid.");
            } else {
                System.out.println("Data stream is invalid. CRC error occurred.");
            }
        }
    }
}
