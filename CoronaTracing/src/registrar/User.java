package registrar;

public class User {

	private String name;
	private String surname;
	private String phoneNumber;	//unique identifier
	
	public User(String name, String surname, String phonNumber) {
		this.name = name;
		this.surname = surname;
		this.phoneNumber = phonNumber;
	}
}
