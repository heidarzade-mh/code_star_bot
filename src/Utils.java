import java.util.regex.Pattern;

public class Utils {
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^09\\d{9}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");
	private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{10}$");
	
	public static String generateInfoMessage(Chat chat, boolean includeInternshipType) {
		String message = "";
		
		message += "نام: " + chat.intern.firstName + "\n";
		message += "نام‌خانوادگی: " + chat.intern.lastName + "\n";
		message += "شماره‌موبایل: " + chat.intern.phoneNumber + "\n";
		message += "ایمیل GitHub: " + chat.intern.githubEmail + "\n";
		message += "ایمیل Teams: " + chat.intern.teamsEmail + "\n";
		message += "آدرس: " + chat.intern.address + "\n";
		message += "کدپستی: " + chat.intern.postalCode + "\n";
		
		if (includeInternshipType)
			message += "نوع کارآموزی: " + chat.intern.internshipType.TITLE;
		
		return message;
	}
	
	public static boolean isValidFormat(String field, String value) {
		return switch (field) {
			case "firstName", "lastName", "address" -> !value.isBlank();
			case "phoneNumber" -> PHONE_NUMBER_PATTERN.matcher(value).find();
			case "githubEmail", "teamsEmail" -> EMAIL_PATTERN.matcher(value).find();
			case "postalCode" -> POSTAL_CODE_PATTERN.matcher(value).find();
			default -> true;
		};
	}
}
