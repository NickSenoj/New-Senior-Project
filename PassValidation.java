import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class PassValidation {

	public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}

	public static String toHexString(byte[] hash) {
		BigInteger number = new BigInteger(1, hash);

		StringBuilder hexString = new StringBuilder(number.toString(16));

		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString();

	}

	public boolean authenticateUser(String inputPassword) {
		try {
			File file = new File("FaceRecPass.txt");
			Scanner scan = new Scanner(file);
			String storedPassword = scan.nextLine();
			String fluff = "abcdefghij";
			return toHexString(getSHA(inputPassword + fluff)).equals(storedPassword);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args) throws FileNotFoundException {

		File file = new File("FaceRecPass.txt");
		Scanner scan = new Scanner(file);

		String password = scan.nextLine();

		try {
			System.out.println("Please Enter Password: ");
			Scanner keyboard = new Scanner(System.in);
			String userInput = keyboard.nextLine();
			String fluff = "abcdefghij";
			if (toHexString(getSHA(userInput + fluff)).equals(password)) {
				System.out.println("Signin Successful");
			} else {
				System.out.println("Wrong Password Entered");
				// System.out.println(password);
				// System.out.println(toHexString(getSHA(userInput + fluff)));
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Exception thrown for an incorrect algorithm: " + e);

		}

	}
}
