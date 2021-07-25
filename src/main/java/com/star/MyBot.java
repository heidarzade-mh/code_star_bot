package com.star;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
	private final AdminManager ADMIN_MANAGER;
	public String username = "Code_star_bot";
	public ArrayList<Chat> chats = new ArrayList<>();
	
	public MyBot() {
		super();
		
		getDB();
		
		this.ADMIN_MANAGER = new AdminManager(this, this.chats);
	}
	
	@Override
	public String getBotUsername() {
		return this.username;
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		System.out.println("payam jadid");
		String msgTextReceived = update.hasMessage() ? update.getMessage().getText().trim() : "";
		Long chatId = update.hasMessage() ? update.getMessage().getChatId() : update.getCallbackQuery().getMessage().getChatId();
		Chat currentChat = getChat(chatId.toString());
		
		if (isAdmin(chatId.toString())) {
			this.ADMIN_MANAGER.manage(update);
			return;
		}
		
		ArrayList<String> msg = new ArrayList<>();
		
		if (currentChat == null && !msgTextReceived.equals("/start")) {
			msg.addAll(new ArrayList<>(Arrays.asList(this.registerAgain())));
		}
		
		switch (msgTextReceived) {
			case "/start" -> msg.addAll(new ArrayList<>(Arrays.asList(this.start(update, currentChat))));
			case "/private" -> msg.add(changeMode(currentChat, ChatMode.PRIVATE, LanguageDictionary.CHANGED_TO_PRIVATE));
			case "/public" -> msg.add(changeMode(currentChat, ChatMode.PUBLIC, LanguageDictionary.CHANGED_TO_PUBLIC));
			case "/edit_first_name" -> msg.add(changeMode(currentChat, ChatMode.EDIT_FIRST_NAME, LanguageDictionary.GET_FIRST_NAME));
			case "/edit_last_name" -> msg.add(changeMode(currentChat, ChatMode.EDIT_LAST_NAME, LanguageDictionary.GET_LAST_NAME));
			case "/edit_phone_number" -> msg.add(changeMode(currentChat, ChatMode.EDIT_PHONE_NUMBER, LanguageDictionary.GET_PHONE_NUMBER));
			case "/edit_internship_type" -> {
				currentChat.mode = ChatMode.EDIT_INTERNSHIP_TYPE;
				internshipTypeMode(currentChat.id);
			}
			case "/show_my_info" -> msg.add(getInfo(currentChat));
			case "/help" -> msg.add(help());
			default -> modeCommands(currentChat, msg, update);
		}
		
		for (String s : msg) {
			SendMessage sm = new SendMessage();
			sm.setText(s);
			sm.setChatId(chatId.toString());
			
			try {
				execute(sm);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
		
		updateDB();
	}
	
	@Override
	public String getBotToken() {
		return Security.TOKEN;
	}
	
	public boolean isAdmin(String chatId) {
		for (String id : Security.ADMIN_CHAT_IDS) {
			if (chatId.equals(id)) {
				return true;
			}
		}
		
		return false;
	}
	
	public String[] registerAgain() {
		return new String[]{LanguageDictionary.REGISTER_AGAIN};
	}
	
	public void modeCommands(Chat currentChat, ArrayList<String> msg, Update update) {
		if (currentChat == null) {
			return;
		}

		switch (currentChat.mode) {
			case LAST_NAME:
				msg.add(changeMode(update, currentChat, "firstName", ChatMode.PHONE_NUMBER, LanguageDictionary.GET_LAST_NAME));
				break;
			case PHONE_NUMBER:
				msg.add(changeMode(update, currentChat, "lastName", ChatMode.INTERNSHIP_TYPE, LanguageDictionary.GET_PHONE_NUMBER));
				break;
			case INTERNSHIP_TYPE:
				String temp = changeMode(update, currentChat, "phoneNumber", ChatMode.FINISH_GET_INFO, "");
				if (temp.equals("")) {
					internshipTypeMode(currentChat.id);
				} else {
					msg.add(temp);
				}
				break;
			case FINISH_GET_INFO:
				msg.add(finishGetInfoMode(update, currentChat));
				break;
			case PUBLIC:
				msg.add(publicMode(update, currentChat));
				break;
			case PRIVATE:
				msg.add(privateMode(update, currentChat));
				break;
			case EDIT_FIRST_NAME:
				msg.add(changeMode(update, currentChat, "firstName"));
				break;
			case EDIT_LAST_NAME:
				msg.add(changeMode(update, currentChat, "lastName"));
				break;
			case EDIT_PHONE_NUMBER:
				msg.add(changeMode(update, currentChat, "phoneNumber"));
				break;
			case EDIT_INTERNSHIP_TYPE:
				msg.add(editInternshipTypeMode(update, currentChat));
				break;
			default:
				break;
		}
	}
	
	public String[] start(Update update, Chat chat) {
		if (chat != null)
			return new String[]{LanguageDictionary.YOU_REGISTERED};
		
		Chat newChat = new Chat(update.getMessage().getChatId().toString());
		this.chats.add(newChat);
		
		newChat.mode = ChatMode.LAST_NAME;
		
		return new String[]{LanguageDictionary.START, LanguageDictionary.GET_FIRST_NAME};
	}
	
	public String changeMode(Update update, Chat chat, String internField, ChatMode mode, String message) {
		try {
			Field field = chat.intern.getClass().getDeclaredField(internField);
			field.setAccessible(true);
			
			String text = update.getMessage().getText().trim();
			
			if (!Utils.isValidFormat(internField, text))
				return LanguageDictionary.INVALID_FORMAT;
			
			field.set(chat.intern, text);
			return changeMode(chat, mode, message);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	public String changeMode(Chat chat, ChatMode mode, String message) {
		chat.mode = mode;
		return message;
	}
	
	public String changeMode(Update update, Chat chat, String internField) {
		return changeMode(update, chat, internField, ChatMode.PRIVATE, LanguageDictionary.SUCCESS_REQUEST);
	}

	public void internshipTypeMode(String chatId) {
		var markup = new InlineKeyboardMarkup();
		var rows = new ArrayList<List<InlineKeyboardButton>>();
		
		for (var x : InternshipType.values()) {
			var row = new ArrayList<InlineKeyboardButton>();
			InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
			inlineKeyboardButton.setText(x.TITLE);
			inlineKeyboardButton.setCallbackData(x.TITLE);
			row.add(inlineKeyboardButton);
			rows.add(row);
		}
		
		markup.setKeyboard(rows);
		
		SendMessage sm = new SendMessage();
		sm.setChatId(chatId);
		sm.setText(LanguageDictionary.SELECT_INTERNSHIP_TYPE);
		sm.setReplyMarkup(markup);
		
		try {
			execute(sm);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
	
	public String finishGetInfoMode(Update update, Chat chat) {
		if (!update.hasCallbackQuery())
			return LanguageDictionary.INVALID_FORMAT;
		
		updateInternshipType(update, chat);
		chat.mode = ChatMode.PRIVATE;
		return LanguageDictionary.FINISH_GET_INFORMATION;
	}
	
	public String publicMode(Update update, Chat chat) {
		sendResponseToAdmin(update, chat);
		return LanguageDictionary.MESSAGE_SENT_PUBLIC;
	}
	
	public String privateMode(Update update, Chat chat) {
		sendResponseToAdmin(update, chat);
		return LanguageDictionary.MESSAGE_SENT_PRIVATE;
	}
	
	public String editInternshipTypeMode(Update update, Chat chat) {
		updateInternshipType(update, chat);
		chat.mode = ChatMode.PRIVATE;
		return LanguageDictionary.SUCCESS_REQUEST;
	}
	
	public String getInfo(Chat chat) {
		return Utils.generateInfoMessage(chat, true);
	}
	
	public String help() {
		return LanguageDictionary.HELP;
	}
	
	public Chat getChat(String id) {
		for (Chat chat : this.chats) {
			if (chat.id.equals(id)) {
				return chat;
			}
		}
		
		return null;
	}
	
	public void sendResponseToAdmin(Update update, Chat chat) {
		String firstLine = "";
		if (chat.mode == ChatMode.PRIVATE) {
			firstLine = LanguageDictionary.HAVE_PRIVATE_MESSAGE + "\n";
		} else if (chat.mode == ChatMode.PUBLIC) {
			firstLine = LanguageDictionary.HAVE_PUBLIC_MESSAGE + "(" + chat.intern.firstName + " " + chat.intern.lastName
					+ ")\n";
		}
		
		String message = firstLine + update.getMessage().getText().trim();
		
		for (String adminId : Security.ADMIN_CHAT_IDS) {
			SendMessage sm = new SendMessage();
			sm.setText(message);
			sm.setChatId(adminId);
			
			try {
				execute(sm);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateInternshipType(Update update, Chat chat) {
		String title = update.getCallbackQuery().getData();
		System.out.println(title);
		
		for (var x : InternshipType.values()) {
			if (title.equals(x.TITLE)) {
				chat.intern.internshipType = x;
				return;
			}
		}
	}
	
	public void updateDB() {
		Object serObj = this.chats;
		try {
			FileOutputStream fileOut = new FileOutputStream("./database.db");
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(serObj);
			objectOut.close();
			System.out.println("The Object  was succesfully written to a file");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void getDB() {
		try {
			FileInputStream fileIn = new FileInputStream("./database.db");
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			
			Object obj = objectIn.readObject();
			
			System.out.println("The Object has been read from the file");
			objectIn.close();
			
			this.chats = (ArrayList<Chat>) obj;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
