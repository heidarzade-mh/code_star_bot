package com.star;

import java.io.Serializable;

public class Chat implements Serializable {
	public String id;
	public ChatMode mode;
	public Intern intern;
	
	public Chat(String id) {
		this.id = id;
		this.mode = ChatMode.START;
		this.intern = new Intern();
	}
}
