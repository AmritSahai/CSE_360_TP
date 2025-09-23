package UsernameEvaluator;

public class UsernameEvaluator {
	/**********
	 * <p> Method: private checkForValidUserName(String input) </p>
	 * 
	 * <p> Description: This method is a mechanical transformation of a Finite State Machine diagram into a Java method.
	 * 
	 * @param input		The input string for the Finite State Machine
	 * @return			An output string that is empty if every things is okay or it is a String
	 * 						with a helpful description of the error
	 */
	public static String checkForValidUserName(String input) {
		// Check to ensure that there is input to process
		if(input.length() <= 0) {
			return "\n*** ERROR *** The input is empty";
		}
		
		// The local variables used to perform the Finite State Machine simulation
		int state = 0;							// This is the FSM state number
		int userNameSize = 0;					// Declare and initialize the UserName size

		// The Finite State Machines continues until the end of the input is reached or at some 
		// state the current character does not match any valid transition to a next state
		for (int i = 0; i < input.length(); i++) {
			
			char currChar = input.charAt(i);
			
			// The switch statement takes the execution to the code for the current state, where
			// that code sees whether or not the current character is valid to transition to a
			// next state
			switch (state) {
			case 0: 
				// State 0 has 1 valid transition that is addressed by an if statement.
				
				// The current character is checked against A-Z, a-z. If any are matched
				// the FSM goes to state 1
				
				// A-Z, a-z -> State 1
				if ((Character.isLetter(currChar))) {	// Check for A-Z
					state = 1;
					
					// Count the character 
					userNameSize++;
					
					// This only occurs once, so there is no need to check for the size getting
					// too large.
				}
				// If it is none of those characters, the FSM halts
				else {
					// The following code is a slight variation to support just console output.
					// State 0 is not a final state, so we can return a very specific error message.
					return "*** ERROR *** A UserName must start with A-Z or a-z.\n";
				}
				
				// The execution of this state is finished
				break;
			
			case 1: 
				// State 1 has two valid transitions, 
				//	1: a A-Z, a-z, 0-9 that transitions back to state 1
				//  2: a period, underscore, minus that transitions to state 2

				
				// A-Z, a-z, 0-9 -> State 1
				if (Character.isLetterOrDigit(currChar)) {		// Check for A-Z, a-z, 0-9
					state = 1;
					
					// Count the character
					userNameSize++;
				}
				// ., -, _ -> State 2
				else if ((currChar == '.') ||
							(currChar == '_') ||
							(currChar == '-')){							// Check for .,_,-
					state = 2;
					
					// Count the .
					userNameSize++;
				}				
				// If it is none of those characters, the FSM halts
				else {
					// The following code is a slight variation to support just console output.
					//State 1 can only have certain characters, so we can return a slightly specific error message.
					return "*** ERROR *** A UserName character may only contain the characters A-Z, a-z, 0-9, ., _, -.\n";
				}
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (userNameSize > 16) {
					// UserName is too long
					return "*** ERROR *** A UserName must have no more than 16 characters.\n";
				}
				break;			
				
			case 2: 
				// State 2 deals with a character after a period in the name.
				
				// A-Z, a-z, 0-9 -> State 1
				if (Character.isLetterOrDigit(currChar)) {		// Check for A-Z, a-z, 0-9
					state = 1;
					
					// Count the odd digit
					userNameSize++;
					
				}
				// If it is none of those characters, the FSM halts
				else {
					// The following code is a slight variation to support just console output.
					// State 2 is not a final state, so we can return a very specific error message
					return "*** ERROR *** A UserName character after a period, minus, or underscore must be A-Z, a-z, or 0-9.\n";
				}
				// The execution of this state is finished
				// If the size is larger than 16, the loop must stop
				if (userNameSize > 16) {
					// UserName is too long
					return "*** ERROR *** A UserName must have no more than 16 characters.\n";
				}
				break;			
			}
			
		}
		
		if (userNameSize < 4) {
			// UserName is too short
			return "*** ERROR *** A UserName must have at least 4 characters.\n";
		} 
		
		// This is for the case where we have a state that is outside of the valid range.
		// This should not happen
		return "";
	}
}
