import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Textbook RSA demonstration that encrypts each UTF-8 byte independently. */
public class RSA {
    private static final int PRIME_BITS = 512;
    private static final BigInteger PUBLIC_EXPONENT = BigInteger.valueOf(65537);

    private final BigInteger modulus;
    private final BigInteger privateExponent;

    private RSA() {
        SecureRandom random = new SecureRandom();
        BigInteger p;
        BigInteger q;
        BigInteger phi;
        do {
            p = BigInteger.probablePrime(PRIME_BITS, random);
            q = BigInteger.probablePrime(PRIME_BITS, random);
            phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        } while (p.equals(q) || !phi.gcd(PUBLIC_EXPONENT).equals(BigInteger.ONE));

        modulus = p.multiply(q);
        privateExponent = PUBLIC_EXPONENT.modInverse(phi);
    }

    /** Educational byte-by-byte RSA; real systems must use padding and libraries. */
    private List<BigInteger> encrypt(byte[] plainText) {
        List<BigInteger> cipherText = new ArrayList<>();
        for (byte value : plainText) {
            int unsignedByte = value & 0xff;
            cipherText.add(BigInteger.valueOf(unsignedByte).modPow(PUBLIC_EXPONENT, modulus));
        }
        return cipherText;
    }

    private byte[] decrypt(List<BigInteger> cipherText) {
        byte[] plainText = new byte[cipherText.size()];
        for (int i = 0; i < cipherText.size(); i++) {
            plainText[i] = (byte) cipherText.get(i).modPow(privateExponent, modulus).intValueExact();
        }
        return plainText;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the plain text: ");
            String plainText = scanner.nextLine();

            RSA rsa = new RSA();
            System.out.println("Public exponent: " + PUBLIC_EXPONENT);
            System.out.println("Modulus: " + rsa.modulus);
            System.out.println("Note: the private exponent is kept inside the program.");

            List<BigInteger> encrypted = rsa.encrypt(plainText.getBytes(StandardCharsets.UTF_8));
            System.out.println("Encrypted byte values: " + encrypted);
            String decrypted = new String(rsa.decrypt(encrypted), StandardCharsets.UTF_8);
            System.out.println("Decrypted text: " + decrypted);
        }
    }
}
