import java.io.Serializable;

public class Intern implements Serializable {
	public String firstName;
	public String lastName;
	public String phoneNumber;
	public String address;
	public String postCode;
	public InternType type;
}

enum InternType {
	UI,
	SE,
	FE
}
