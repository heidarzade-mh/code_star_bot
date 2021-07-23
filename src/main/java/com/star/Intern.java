package com.star;

import java.io.Serializable;

enum InternshipType {
	UI("رابط کاربری"),
	SE("فرانت‌اند"),
	FE("مهندسی نرم‌افزار");
	
	public final String TITLE;
	
	InternshipType(String title) {
		this.TITLE = title;
	}
}

public class Intern implements Serializable {
	public String firstName;
	public String lastName;
	public String phoneNumber;
	public String githubEmail;
	public String teamsEmail;
	public String address;
	public String postalCode;
	public InternshipType internshipType;
}
