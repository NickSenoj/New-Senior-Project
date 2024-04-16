import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Scanner;

public class PassEnc {

	public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		// return md.digest(input.getBytes(StandardCharsets.UTF_8));
		return md.digest(input.getBytes());
	}

	public static String toHexString(byte[] hash) {
		BigInteger number = new BigInteger(1, hash);

		StringBuilder hexString = new StringBuilder(number.toString(16));

		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString();

	}

	public static void generatePassword() {
		try {
			Scanner UserInput = new Scanner(System.in);
			System.out.println("Enter new password: ");
			String string1 = UserInput.nextLine();
			String fluff = "abcdefghij";

			String password = toHexString(getSHA(string1 + fluff));
			Writer fw = new FileWriter("FaceRecPass.txt");
			fw.write(password);

			System.out.println("Password Saved");
			fw.close();
		} catch (NoSuchAlgorithmException | IOException e) {
			System.out.println("Exception: " + e);
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			Scanner UserInput = new Scanner(System.in);
			System.out.println("Enter new password: ");
			String string1 = UserInput.nextLine();
			String fluff = "abcdefghij";

			String password = toHexString(getSHA(string1 + fluff));
			Writer fw = new FileWriter("FaceRecPass.txt");
			fw.write(password);

			System.out.println("Password Saved");
			fw.close();
		}

		catch (NoSuchAlgorithmException e) {
			System.out.println("Exception for incorrect algorithm: " + e);
		}

	}
}
