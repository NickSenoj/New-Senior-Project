import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class PasswordTest {

	@Test  //If this test passes it means a password was successfully saved
		   //to our password file
	void encodePasswordSuccessfully() {
		PassEnc.generatePassword();
		File file = new File("FaceRecPass.txt");
		try {
			Scanner scan = new Scanner(file);
			assertTrue(scan.hasNextLine());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test   //For this test to run successfully you must first change the password to our example
	        //here e.g. "Password"
	void validatePasswordSuccessfully() {
		PassValidation.authenticateUser("Password");
		assertFalse(PassValidation.authenticateUser("Password") == false);
	}
	
	
	@Test  //Similar to Successfully but this should pass the test if a wrong password isn't entered
		   //if this test fails it means code is accepting any password
	void validateWrongPassword() {
		PassValidation.authenticateUser("WrongPassword");
		assertFalse(PassValidation.authenticateUser("WrongPassword") == true);
	}
	
	@Test  //Tests to make sure that similar passwords with possible 
		   //capitalization mistakes aren't accepted
	void validateSimilarPassword() {
		PassValidation.authenticateUser("password");
		assertFalse(PassValidation.authenticateUser("password") == true);
	}
	

}
