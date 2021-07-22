import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Utils {
	private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u0600-\\u06FF\\s]+$");
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^09\\d{9}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");
	private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{10}$");
	
	public static String generateInfoMessage(Chat chat, boolean includeInternshipType) {
		var lines = new ArrayList<String>();
		
		lines.addAll(Arrays.asList("نام:", chat.intern.firstName, ""));
		lines.addAll(Arrays.asList("نام‌خانوادگی:", chat.intern.lastName, ""));
		lines.addAll(Arrays.asList("شماره‌موبایل:", chat.intern.phoneNumber, ""));
		lines.addAll(Arrays.asList("ایمیل GitHub:", chat.intern.githubEmail, ""));
		lines.addAll(Arrays.asList("ایمیل Teams:", chat.intern.teamsEmail, ""));
		lines.addAll(Arrays.asList("آدرس:", chat.intern.address, ""));
		lines.addAll(Arrays.asList("کدپستی:", chat.intern.postalCode, ""));
		
		if (includeInternshipType)
			lines.addAll(Arrays.asList("نوع کارآموزی:", chat.intern.internshipType.TITLE));
		
		StringBuilder message = new StringBuilder();
		for (var line : lines)
			message.append(line).append("\n");
		
		return message.toString();
	}
	
	public static boolean isValidFormat(String field, String value) {
		return switch (field) {
			case "firstName", "lastName", "address" -> !value.isBlank() && NAME_PATTERN.matcher(value).find();
			case "phoneNumber" -> PHONE_NUMBER_PATTERN.matcher(value).find();
			case "githubEmail", "teamsEmail" -> EMAIL_PATTERN.matcher(value).find();
			case "postalCode" -> POSTAL_CODE_PATTERN.matcher(value).find();
			default -> true;
		};
	}
}
