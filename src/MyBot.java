import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MyBot extends TelegramLongPollingBot {
	public String username = "Code_star_bot";
	public ArrayList<Chat> chats = new ArrayList<>();
	
	private final AdminManager ADMIN_MANAGER;
	
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
		String msgTextReceived = update.getMessage().getText();
		Long chatId = update.getMessage().getChatId();
		Chat currentChat = getChat(chatId);
		
		if (isAdmin(chatId)) {
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
			case "/edit_first_name" -> msg.add(changeMode(currentChat, ChatMode.EDIT_FIRST_NAME, LanguageDictionary.GET_NAME));
			case "/edit_last_name" -> msg.add(changeMode(currentChat, ChatMode.EDIT_LAST_NAME, LanguageDictionary.GET_LAST_NAME));
			case "/edit_phone_number" -> msg.add(changeMode(currentChat, ChatMode.EDIT_PHONE_NUMBER, LanguageDictionary.GET_PHONE_NUMBER));
			case "/edit_github_email" -> msg.add(changeMode(currentChat, ChatMode.EDIT_GITHUB_EMAIL, LanguageDictionary.GET_GITHUB_EMAIL));
			case "/edit_teams_email" -> msg.add(changeMode(currentChat, ChatMode.EDIT_TEAMS_EMAIL, LanguageDictionary.GET_TEAMS_EMAIL));
			case "/edit_postal_code" -> msg.add(changeMode(currentChat, ChatMode.EDIT_POSTAL_CODE, LanguageDictionary.GET_POST_CODE));
			case "/edit_address" -> msg.add(changeMode(currentChat, ChatMode.EDIT_ADDRESS, LanguageDictionary.GET_ADDRESS));
			case "/edit_internship_type" -> msg.add(changeMode(currentChat, ChatMode.EDIT_INTERNSHIP_TYPE, LanguageDictionary.SELECT_INTERNSHIP_TYPE));
			case "/show_my_info" -> msg.addAll(new ArrayList<>(Arrays.asList(this.getInfo(currentChat))));
			case "/help" -> msg.addAll(new ArrayList<>(Arrays.asList(this.help())));
			default -> modeCommands(currentChat, msg, update);
		}
		
		for (String s : msg) {
			SendMessage sm = new SendMessage();
			sm.setText(s);
			sm.setChatId(chatId);
			
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
	
	public boolean isAdmin(Long chatId) {
		for (Long id : Security.ADMIN_CHAT_IDS) {
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
			msg.add("BIJAN");
			return;
		}
		
		switch (currentChat.mode) {
			case LAST_NAME:
				msg.add(changeMode(update, currentChat, "firstName", ChatMode.PHONE_NUMBER, LanguageDictionary.GET_LAST_NAME));
				break;
			case PHONE_NUMBER:
				msg.add(changeMode(update, currentChat, "lastName", ChatMode.GITHUB_EMAIL, LanguageDictionary.GET_PHONE_NUMBER));
				break;
			case GITHUB_EMAIL:
				msg.add(changeMode(update, currentChat, "phoneNumber", ChatMode.TEAMS_EMAIL, LanguageDictionary.GET_GITHUB_EMAIL));
				break;
			case TEAMS_EMAIL:
				msg.add(changeMode(update, currentChat, "githubEmail", ChatMode.POSTAL_CODE, LanguageDictionary.GET_TEAMS_EMAIL));
				break;
			case POSTAL_CODE:
				msg.add(changeMode(update, currentChat, "teamsEmail", ChatMode.ADDRESS, LanguageDictionary.GET_POST_CODE));
				break;
			case ADDRESS:
				msg.add(changeMode(update, currentChat, "postCode", ChatMode.INTERNSHIP_TYPE, LanguageDictionary.GET_ADDRESS));
				break;
			case INTERNSHIP_TYPE:
				msg.add(changeMode(update, currentChat, "address", ChatMode.FINISH_GET_INFO, LanguageDictionary.SELECT_INTERNSHIP_TYPE));
				break;
			case FINISH_GET_INFO:
				msg.addAll(new ArrayList<>(Arrays.asList(this.finishGetInfoMode(update, currentChat))));
				break;
			case PUBLIC:
				msg.addAll(new ArrayList<>(Arrays.asList(this.publicMode(update, currentChat))));
				break;
			case PRIVATE:
				msg.addAll(new ArrayList<>(Arrays.asList(this.privateMode(update, currentChat))));
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
			case EDIT_GITHUB_EMAIL:
				msg.add(changeMode(update, currentChat, "githubEmail"));
				break;
			case EDIT_TEAMS_EMAIL:
				msg.add(changeMode(update, currentChat, "teamsEmail"));
				break;
			case EDIT_ADDRESS:
				msg.add(changeMode(update, currentChat, "address"));
				break;
			case EDIT_POSTAL_CODE:
				msg.add(changeMode(update, currentChat, "postCode"));
				break;
			case EDIT_INTERNSHIP_TYPE:
				msg.addAll(new ArrayList<>(Arrays.asList(this.editInternshipTypeMode(update, currentChat))));
				break;
			default:
				break;
		}
	}
	
	public String[] start(Update update, Chat chat) {
		if (chat != null)
			return new String[]{LanguageDictionary.YOU_REGISTERED};
		
		Chat newChat = new Chat(update.getMessage().getChatId());
		this.chats.add(newChat);
		
		newChat.mode = ChatMode.LAST_NAME;
		
		return new String[]{LanguageDictionary.START, LanguageDictionary.GET_NAME};
	}
	
	public String changeMode(Update update, Chat chat, String internField, ChatMode mode, String message) {
		try {
			Field field = chat.intern.getClass().getDeclaredField(internField);
			field.setAccessible(true);
			field.set(chat.intern, update.getMessage().getText());
			
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
	
	public String[] finishGetInfoMode(Update update, Chat chat) {
		updateInternshipType(update, chat);
		chat.mode = ChatMode.PRIVATE;
		return new String[]{LanguageDictionary.FINISH_GET_INFORMATION};
	}
	
	public String[] publicMode(Update update, Chat chat) {
		sendResponseToAdmin(update, chat);
		return new String[]{LanguageDictionary.MESSAGE_SENT_PUBLIC};
	}
	
	public String[] privateMode(Update update, Chat chat) {
		sendResponseToAdmin(update, chat);
		return new String[]{LanguageDictionary.MESSAGE_SENT_PRIVATE};
	}
	
	public String[] editInternshipTypeMode(Update update, Chat chat) {
		updateInternshipType(update, chat);
		chat.mode = ChatMode.PRIVATE;
		return new String[]{LanguageDictionary.SUCCESS_REQUEST};
	}
	
	public String[] getInfo(Chat chat) {
		return new String[]{Utils.generateInfoMessage(chat, true)};
	}
	
	public String[] help() {
		return new String[]{LanguageDictionary.HELP};
	}
	
	public Chat getChat(Long id) {
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
			firstLine = LanguageDictionary.HAVE_PRIVATE_MESSAGE;
		} else if (chat.mode == ChatMode.PUBLIC) {
			firstLine = LanguageDictionary.HAVE_PUBLIC_MESSAGE + "(" + chat.intern.firstName + " " + chat.intern.lastName
					+ ")\n";
		}
		
		String message = firstLine + update.getMessage().getText();
		
		for (Long adminId : Security.ADMIN_CHAT_IDS) {
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
		String msg = update.getMessage().getText();
		switch (msg) {
			case "/ui" -> chat.intern.internshipType = InternshipType.UI;
			case "/fe" -> chat.intern.internshipType = InternshipType.FE;
			case "/se" -> chat.intern.internshipType = InternshipType.SE;
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
