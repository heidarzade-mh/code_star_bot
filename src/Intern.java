import java.io.Serializable;

public class Intern implements Serializable {
	public String firstName;
	public String lastName;
	public String phoneNumber;
	public String address;
	public String postCode;
	public InternshipType internshipType;
}

enum InternshipType {
	UI("رابط کاربری"),
	SE("فرانت‌اند"),
	FE("مهندسی نرم‌افزار");
	
	public final String TITLE;
	
	InternshipType(String title) {
		this.TITLE = title;
	}
}
