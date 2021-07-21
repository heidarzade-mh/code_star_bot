import java.io.Serializable;

public class Intern implements Serializable {
    public String name;
    public String familyName;
    public String phoneNumber;
    public String address;
    public String postCode;
    public InternType type;
}

enum InternType {
    PA,
    SE,
    FE
}