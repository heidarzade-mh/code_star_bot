public class Utils {
	public static String generateInfoMessage(Chat chat, boolean includeInternshipType) {
		String message = "";
		
		message += "نام: " + chat.intern.firstName + "\n";
		message += "نام‌خانوادگی: " + chat.intern.lastName + "\n";
		message += "شماره‌موبایل: " + chat.intern.phoneNumber + "\n";
		message += "آدرس: " + chat.intern.address + "\n";
		message += "کدپستی: " + chat.intern.postCode + "\n";
		
		if (includeInternshipType)
			message += "نوع کارآموزی: " + chat.intern.internshipType.TITLE;
		
		return message;
	}
}
