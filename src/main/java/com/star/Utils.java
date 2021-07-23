package com.star;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Utils {
	private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u0600-\\u06FF\\s]+$");
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^09\\d{9}$");
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$");
	private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^\\d{10}$");
	
	public static String generateInfoMessage(Chat chat, boolean includeInternshipType) {
		ArrayList<String> lines = new ArrayList<String>();
		
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
		for (String line : lines)
			message.append(line).append("\n");
		
		return message.toString();
	}
	
	public static boolean isValidFormat(String field, String value) {
		switch (field) {
			case "firstName":
			case "lastName":
			case "address":
				return !value.isBlank() && NAME_PATTERN.matcher(value).find();
			case "phoneNumber":
				return PHONE_NUMBER_PATTERN.matcher(value).find();
			case "githubEmail":
			case "teamsEmail":
				return EMAIL_PATTERN.matcher(value).find();
			case "postalCode":
				return POSTAL_CODE_PATTERN.matcher(value).find();
			default:
				return true;
		}
	}
}
