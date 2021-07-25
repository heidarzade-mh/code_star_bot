package com.star;

import java.io.Serializable;

enum InternshipType {
	UI("رابط کاربری"),
	FE("فرانت‌اند"),
	SE("مهندسی نرم‌افزار");
	
	public final String TITLE;
	
	InternshipType(String title) {
		this.TITLE = title;
	}
}

public class Intern implements Serializable {
	public String firstName;
	public String lastName;
	public String phoneNumber;
	public InternshipType internshipType;
}
