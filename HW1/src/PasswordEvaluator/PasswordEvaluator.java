package PasswordEvaluator;

public class PasswordEvaluator {
	/**********
	 * <p> Method: private evaluatePassword(String input) </p>
	 * 
	 * <p> Description: This method is a mechanical transformation of a Directed Graph diagram 
	 * into a Java method. This method is used by both the GUI version of the application as well
	 * as the testing automation version.
	 * 
	 * @param input		The input string evaluated by the directed graph processing
	 * @return			An output string that is empty if every things is okay or it will be
	 * 						a string with a helpful description of the error follow by two lines
	 * 						that shows the input line follow by a line with an up arrow at the
	 *						point where the error was found.
	 */
	
	public static String evaluatePassword(String input) {
		//The input must be greater than zero, but if it is not, an error will be returned
		if(input.length() <= 0) {
			return "*** Error *** The password is empty!";
		}
		
		//The input must not exceed 32 characters or an error will be returned
		if(input.length() > 32) {
			return "*** Error *** The password exceeds the maximum 32 characters!";
		}
		
		// The following are the attributes associated with each of the requirements
		boolean foundUpperCase = false;				// Reset the Boolean flag
		boolean foundLowerCase = false;				// Reset the Boolean flag
		boolean foundNumericDigit = false;			// Reset the Boolean flag
		boolean foundSpecialChar = false;			// Reset the Boolean flag
		boolean foundLongEnough = false;			// Reset the Boolean flag
		
		// The Directed Graph simulation continues until the end of the input is reached or at some
		// state the current character does not match any valid transition
		for (int i = 0; i < input.length(); i++) {
			char currChar = input.charAt(i);

			// The cascading if statement sequentially tries the current character against all of
			// the valid transitions, each associated with one of the requirements
			if (Character.isUpperCase(currChar)) {
				System.out.println("Upper case letter found");
				foundUpperCase = true;
			} else if (Character.isLowerCase(currChar)) {
				System.out.println("Lower case letter found");
				foundLowerCase = true;
			} else if (Character.isDigit(currChar)) {
				System.out.println("Digit found");
				foundNumericDigit = true;
			} else if ("~`!@#$%^&*()_-+={}[]|\\:;\"'<>,.?/".indexOf(currChar) >= 0) {
				System.out.println("Special character found");
				foundSpecialChar = true;
			} else {
				return "*** Error *** An invalid character has been found!";
			}
			
			if (i >= 7) {
				System.out.println("At least 8 characters found");
				foundLongEnough = true;
			}
			
			System.out.println();
		}
		
		// Construct a String with a list of the requirement elements that were found.
		// If it gets here, there something was not found, so return an appropriate message
		if (foundUpperCase && foundLowerCase && foundNumericDigit &&
				foundSpecialChar && foundLongEnough) {
			return "";
		}
		
		String errMessage = "*** Error *** The password is missing the following requirements:\n\n";
		
		if (!foundUpperCase)
			errMessage += "- One upper case letter must be included (A-Z)\n";
		
		if (!foundLowerCase)
			errMessage += "- One lower case letter must be included (a-z)\n";
		
		if (!foundNumericDigit)
			errMessage += "- One numeric digit must be included (0-9)\n";
			
		if (!foundSpecialChar)
			errMessage += "- One special character must be included\n";
			
		if (!foundLongEnough)
			errMessage += "- The password must be at least 8 characters long\n";
		
		return errMessage + "\n- Conditions were not satisfied";
	}
}
