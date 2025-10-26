package entityClasses;

/*******
 * <p> Title: User Class </p>
 * 
 * <p> Description: This User class represents a user entity in the system.  It contains the user's
 *  details such as userName, password, and roles being played. </p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * 
 */ 

public class User {
	
	/*
	 * These are the private attributes for this entity object
	 */
	
	/** The user’s login name used for authentication. */
    private String userName;
    
    /** The user’s account password. */
    private String password;
    
    /** The user’s first (legal) name. */
    private String firstName;
    
    /** The user’s middle name (if provided). */
    private String middleName;
    
    /** The user’s last (family) name. */
    private String lastName;
    
    /** The user’s preferred first name used for display. */
    private String preferredFirstName;
    
    /** The user’s email address for notifications and contact. */
    private String emailAddress;
    
    /** Boolean indicating whether this user has the Admin role. */
    private boolean adminRole;
    
    /** Boolean indicating whether this user has the Student role. */
    private boolean Student;
    
    /** Boolean indicating whether this user has the Staff role. */
    private boolean Staff;
    
    
    /*****
     * <p> Method: User() </p>
     * 
     * <p> Description: This default constructor is not used in this system. </p>
     */
    public User() {
    	
    }

    
    /*****
     * <p> Method: User(String userName, String password, String fn, String mn, String ln, 
     * String pfn, String ea, boolean r1, boolean r2, boolean r3) </p>
     * 
     * <p> Description: Constructs a new User object with full profile information and role assignments. 
     * Supports the staff and admin features that involve user creation and management. </p>
     * 
     * @param userName the unique username for this user
     * @param password the user’s password
     * @param fn the user’s first name
     * @param mn the user’s middle name
     * @param ln the user’s last name
     * @param pfn the user’s preferred first name
     * @param ea the user’s email address
     * @param r1 true if this user has the Admin role
     * @param r2 true if this user has the Student role
     * @param r3 true if this user has the Staff role
     */
    // Constructor to initialize a new User object with userName, password, and role.
    public User(String userName, String password, String fn, String mn, String ln, String pfn, 
    		String ea, boolean r1, boolean r2, boolean r3) {
        this.userName = userName;
        this.password = password;
        this.firstName = fn;
        this.middleName = mn;
        this.lastName = ln;
        this.preferredFirstName = pfn;
        this.emailAddress = ea;
        this.adminRole = r1;
        this.Student = r2;
        this.Staff = r3;
    }

    
    /*****
     * <p> Method: void setAdminRole(boolean role) </p>
     * 
     * <p> Description: This setter defines the Admin role attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing the Admin role.
     * 
     */
    // Sets the role of the Admin user.
    public void setAdminRole(boolean role) {
    	this.adminRole=role;
    }

    
    /*****
     * <p> Method: void setStudentUser(boolean role) </p>
     * 
     * <p> Description: This setter defines the Student attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing Student.
     * 
     */
    // Sets the Student user.
    public void setStudentUser(boolean role) {
    	this.Student=role;
    }

    
    /*****
     * <p> Method: void setStaffUser(boolean role) </p>
     * 
     * <p> Description: This setter defines the Staff attribute. </p>
     * 
     * @param role is a boolean that specifies if this user in playing Staff.
     * 
     */
    // Sets the Staff user.
    public void setStaffUser(boolean role) {
    	this.Staff=role;
    }

    
    /*****
     * <p> Method: String getUserName() </p>
     * 
     * <p> Description: This getter returns the UserName. </p>
     * 
     * @return a String of the UserName
     * 
     */
    // Gets the current value of the UserName.
    public String getUserName() { return userName; }

    
    /*****
     * <p> Method: String getPassword() </p>
     * 
     * <p> Description: This getter returns the Password. </p>
     * 
     * @return a String of the password
	 *
     */
    // Gets the current value of the Password.
    public String getPassword() { return password; }

    
    /*****
     * <p> Method: String getFirstName() </p>
     * 
     * <p> Description: This getter returns the FirstName. </p>
     * 
     * @return a String of the FirstName
	 *
     */
    // Gets the current value of the FirstName.
    public String getFirstName() { return firstName; }

    
    /*****
     * <p> Method: String getMiddleName() </p>
     * 
     * <p> Description: This getter returns the MiddleName. </p>
     * 
     * @return a String of the MiddleName
	 *
     */
    // Gets the current value of the Student role attribute.
    public String getMiddleName() { return middleName; }

    
    /*****
     * <p> Method: String getLasteName() </p>
     * 
     * <p> Description: This getter returns the LastName. </p>
     * 
     * @return a String of the LastName
	 *
     */
    // Gets the current value of the Student role attribute.
    public String getLastName() { return lastName; }

    
    /*****
     * <p> Method: String getPreferredFirstName() </p>
     * 
     * <p> Description: This getter returns the PreferredFirstName. </p>
     * 
     * @return a String of the PreferredFirstName
	 *
     */
    // Gets the current value of the Student role attribute.
    public String getPreferredFirstName() { return preferredFirstName; }

    
    /*****
     * <p> Method: String getEmailAddress() </p>
     * 
     * <p> Description: This getter returns the EmailAddress. </p>
     * 
     * @return a String of the EmailAddress
	 *
     */
    // Gets the current value of the Student role attribute.
    public String getEmailAddress() { return emailAddress; }
    

    /*****
     * <p> Method: void setUserName(String s) </p>
     * 
     * <p> Description: This setter defines the UserName attribute. </p>
     * 
     * @param s is the user name string to assign to this user.
     * 
     */
    // Sets the UserName for this user.
    public void setUserName(String s) { userName = s; }
    
    
    /*****
     * <p> Method: void setPassword(String s) </p>
     * 
     * <p> Description: This setter defines the Password attribute. </p>
     * 
     * @param s is the password string to assign to this user.
     * 
     */
    // Sets the Password for this user.
    public void setPassword(String s) { password = s; }
    
    
    /*****
     * <p> Method: void setFirstName(String s) </p>
     * 
     * <p> Description: This setter defines the FirstName attribute. </p>
     * 
     * @param s is the first name string to assign to this user.
     * 
     */
    // Sets the FirstName for this user.
    public void setFirstName(String s) { firstName = s; }
    
    
    /*****
     * <p> Method: void setMiddleName(String s) </p>
     * 
     * <p> Description: This setter defines the MiddleName attribute. </p>
     * 
     * @param s is the middle name string to assign to this user.
     * 
     */
    // Sets the MiddleName for this user.
    public void setMiddleName(String s) { middleName = s; }
    
    
    /*****
     * <p> Method: void setLastName(String s) </p>
     * 
     * <p> Description: This setter defines the LastName attribute. </p>
     * 
     * @param s is the last name string to assign to this user.
     * 
     */
    // Sets the LastName for this user.
    public void setLastName(String s) { lastName = s; }
    
    
    /*****
     * <p> Method: void setPreferredFirstName(String s) </p>
     * 
     * <p> Description: This setter defines the PreferredFirstName attribute. </p>
     * 
     * @param s is the preferred first name string to assign to this user.
     * 
     */
    // Sets the PreferredFirstName for this user.
    public void setPreferredFirstName(String s) { preferredFirstName = s; }
    
    
    /*****
     * <p> Method: void setEmailAddress(String s) </p>
     * 
     * <p> Description: This setter defines the EmailAddress attribute. </p>
     * 
     * @param s is the email address string to assign to this user.
     * 
     */
    // Sets the EmailAddress for this user.
    public void setEmailAddress(String s) { emailAddress = s; }

    
    /*****
     * <p> Method: String getAdminRole() </p>
     * 
     * <p> Description: This getter returns the value of the Admin role attribute. </p>
     * 
     * @return a String of "TRUE" or "FALSE" based on state of the attribute
	 *
     */
    // Gets the current value of the Admin role attribute.
    public boolean getAdminRole() { return adminRole; }

    
    /*****
     * <p> Method: String getStudent() </p>
     * 
     * <p> Description: This getter returns the value of the Student attribute. </p>
     * 
     * @return a String of "TRUE" or "FALSE" based on state of the attribute
	 *
     */
    // Gets the current value of the Student attribute.
	public boolean getNewStudent() { return Student; }

    
    /*****
     * <p> Method: String getStaff() </p>
     * 
     * <p> Description: This getter returns the value of the Staff attribute. </p>
     * 
     * @return a String of "TRUE" or "FALSE" based on state of the attribute
	 *
     */
    // Gets the current value of the Staff attribute.
    public boolean getNewStaff() { return Staff; }

        
    /*****
     * <p> Method: int getNumRoles() </p>
     * 
     * <p> Description: This getter returns the number of roles this user plays (0 - 5). </p>
     * 
     * @return a value 0 - 5 of the number of roles this user plays
	 *
     */
    // Gets the current value of the Staff role attribute.
    public int getNumRoles() {
    	int numRoles = 0;
    	if (adminRole) numRoles++;
    	if (Student) numRoles++;
    	if (Staff) numRoles++;
    	return numRoles;
    }
}
