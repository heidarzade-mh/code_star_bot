import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
			case "/private" -> msg.addAll(new ArrayList<>(Arrays.asList(this.privateC(currentChat))));
			case "/public" -> msg.addAll(new ArrayList<>(Arrays.asList(this.publicC(currentChat))));
			case "/edit_name" -> msg.addAll(new ArrayList<>(Arrays.asList(this.editName(currentChat))));
			case "/edit_familyname" -> msg.addAll(new ArrayList<>(Arrays.asList(this.editFamilyname(currentChat))));
			case "/edit_postcode" -> msg.addAll(new ArrayList<>(Arrays.asList(this.editPostcode(currentChat))));
			case "/edit_address" -> msg.addAll(new ArrayList<>(Arrays.asList(this.editAddress(currentChat))));
			case "/edit_phonenumber" -> msg.addAll(new ArrayList<>(Arrays.asList(this.editPhoneNumber(currentChat))));
			case "/edit_interntype" -> msg.addAll(new ArrayList<>(Arrays.asList(this.editInternType(currentChat))));
			case "/displaymyinfo" -> msg.addAll(new ArrayList<>(Arrays.asList(this.getInfo(currentChat))));
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
	
	public String[] help() {
		return new String[]{LanguageDictionary.HELP};
	}
	
	public String[] registerAgain() {
		return new String[]{LanguageDictionary.REGISTER_AGAIN};
	}
	
	public void modeCommands(Chat currentChat, ArrayList<String> msg, Update update) {
		if (currentChat == null) {
			return;
		}
		
		switch (currentChat.mode) {
			case FamilyName:
				msg.addAll(new ArrayList<>(Arrays.asList(this.familyNameMode(update, currentChat))));
				break;
			case PhoneNumber:
				msg.addAll(new ArrayList<>(Arrays.asList(this.phoneNumberMode(update, currentChat))));
				break;
			case CodePost:
				msg.addAll(new ArrayList<>(Arrays.asList(this.codePostMode(update, currentChat))));
				break;
			case Address:
				msg.addAll(new ArrayList<>(Arrays.asList(this.addressMode(update, currentChat))));
				break;
			case InternType:
				msg.addAll(new ArrayList<>(Arrays.asList(this.internTypeMode(update, currentChat))));
				break;
			case FinishGetInfo:
				msg.addAll(new ArrayList<>(Arrays.asList(this.finishGetInfoMode(update, currentChat))));
				break;
			case Public:
				msg.addAll(new ArrayList<>(Arrays.asList(this.publicMode(update, currentChat))));
				break;
			case Private:
				msg.addAll(new ArrayList<>(Arrays.asList(this.privateMode(update, currentChat))));
				break;
			case EditName:
				msg.addAll(new ArrayList<>(Arrays.asList(this.editNameMode(update, currentChat))));
				break;
			case EditFamilyName:
				msg.addAll(new ArrayList<>(Arrays.asList(this.editFamilynameMode(update, currentChat))));
				break;
			case EditAddress:
				msg.addAll(new ArrayList<>(Arrays.asList(this.editAddressMode(update, currentChat))));
				break;
			case EditPhoneNumber:
				msg.addAll(new ArrayList<>(Arrays.asList(this.editPhoneNumberMode(update, currentChat))));
				break;
			case EditPostCode:
				msg.addAll(new ArrayList<>(Arrays.asList(this.editPostodeMode(update, currentChat))));
				break;
			case EditInternType:
				msg.addAll(new ArrayList<>(Arrays.asList(this.editInternTypeMode(update, currentChat))));
				break;
			default:
				break;
		}
	}
	
	public String[] getInfo(Chat chat) {
		String message = "";
		
		message += "نام: " + chat.intern.name + "\n";
		message += "نام‌خانوادگی: " + chat.intern.familyName + "\n";
		message += "شماره‌موبایل: " + chat.intern.phoneNumber + "\n";
		message += "آدرس: " + chat.intern.address + "\n";
		message += "کدپستی: " + chat.intern.postCode + "\n";
		
		String type = "";
		if (chat.intern.type == InternType.PA) {
			type = "تحلیل عملکرد";
		} else if (chat.intern.type == InternType.FE) {
			type = "فرانت‌اند";
		} else if (chat.intern.type == InternType.SE) {
			type = "مهندسی نرم‌افزار";
		}
		message += "نوع کارآموزی: " + type;
		
		String[] result = {message};
		return result;
	}
	
	public String[] editName(Chat chat) {
		chat.mode = ChatMode.EditName;
		
		String[] result = {LanguageDictionary.GET_NAME};
		return result;
	}
	
	public String[] editFamilyname(Chat chat) {
		chat.mode = ChatMode.EditFamilyName;
		
		return new String[]{LanguageDictionary.GET_FAMILYNAME};
	}
	
	public String[] editAddress(Chat chat) {
		chat.mode = ChatMode.EditAddress;
		
		return new String[]{LanguageDictionary.GET_ADDRESS};
	}
	
	public String[] editPhoneNumber(Chat chat) {
		chat.mode = ChatMode.EditPhoneNumber;
		
		return new String[]{LanguageDictionary.GET_PHONE_NUMBER};
	}
	
	public String[] editInternType(Chat chat) {
		chat.mode = ChatMode.EditInternType;
		
		return new String[]{LanguageDictionary.GET_INTERNTYPE};
	}
	
	public String[] editPostcode(Chat chat) {
		chat.mode = ChatMode.EditPostCode;
		
		return new String[]{LanguageDictionary.GET_POST_CODE};
	}
	
	public String[] editNameMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.SUCCESS_REQUEST};
		
		chat.intern.name = update.getMessage().getText();
		chat.mode = ChatMode.Private;
		return result;
	}
	
	public String[] editFamilynameMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.SUCCESS_REQUEST};
		
		chat.intern.familyName = update.getMessage().getText();
		chat.mode = ChatMode.Private;
		return result;
	}
	
	public String[] editPhoneNumberMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.SUCCESS_REQUEST};
		
		chat.intern.phoneNumber = update.getMessage().getText();
		chat.mode = ChatMode.Private;
		return result;
	}
	
	public String[] editAddressMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.SUCCESS_REQUEST};
		
		chat.intern.address = update.getMessage().getText();
		chat.mode = ChatMode.Private;
		return result;
	}
	
	public String[] editPostodeMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.SUCCESS_REQUEST};
		
		chat.intern.postCode = update.getMessage().getText();
		chat.mode = ChatMode.Private;
		return result;
	}
	
	public String[] editInternTypeMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.SUCCESS_REQUEST};
		
		String msg = update.getMessage().getText();
		switch (msg) {
			case "/pa" -> chat.intern.type = InternType.PA;
			case "/fe" -> chat.intern.type = InternType.FE;
			case "/se" -> chat.intern.type = InternType.SE;
		}
		
		chat.mode = ChatMode.Private;
		return result;
	}
	
	public String[] privateMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.MESSAGE_SENDED_PRIVATE};
		
		sendResponseToAdmin(update, chat);
		return result;
	}
	
	public String[] publicMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.MESSAGE_SENDED_PUBLIC};
		
		sendResponseToAdmin(update, chat);
		return result;
	}
	
	public String[] finishGetInfoMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.FINISH_GET_INFORMATION};
		chat.mode = ChatMode.Private;
		
		String msg = update.getMessage().getText();
		switch (msg) {
			case "/pa" -> chat.intern.type = InternType.PA;
			case "/fe" -> chat.intern.type = InternType.FE;
			case "/se" -> chat.intern.type = InternType.SE;
		}
		
		return result;
	}
	
	public String[] publicC(Chat chat) {
		String[] result = {LanguageDictionary.CHANGED_TO_PUBLIC};
		chat.mode = ChatMode.Public;
		return result;
	}
	
	public String[] privateC(Chat chat) {
		String[] result = {LanguageDictionary.CHANGED_TO_PRIVATE};
		chat.mode = ChatMode.Private;
		return result;
	}
	
	public String[] codePostMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.GET_POST_CODE};
		chat.mode = ChatMode.Address;
		
		chat.intern.phoneNumber = update.getMessage().getText();
		return result;
	}
	
	public String[] addressMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.GET_ADDRESS};
		chat.mode = ChatMode.InternType;
		
		chat.intern.postCode = update.getMessage().getText();
		return result;
	}
	
	public String[] internTypeMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.GET_INTERNTYPE};
		chat.mode = ChatMode.FinishGetInfo;
		
		chat.intern.address = update.getMessage().getText();
		return result;
	}
	
	public String[] phoneNumberMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.GET_PHONE_NUMBER};
		chat.mode = ChatMode.CodePost;
		
		chat.intern.familyName = update.getMessage().getText();
		return result;
	}
	
	public String[] familyNameMode(Update update, Chat chat) {
		String[] result = {LanguageDictionary.GET_FAMILYNAME};
		chat.mode = ChatMode.PhoneNumber;
		
		chat.intern.name = update.getMessage().getText();
		return result;
	}
	
	public String[] start(Update update, Chat chat) {
		if (chat != null) {
			String[] result = {LanguageDictionary.YOU_REGISTERED};
			return result;
		}
		
		Chat newChat = new Chat(update.getMessage().getChatId());
		this.chats.add(newChat);
		
		String[] result = {LanguageDictionary.START, LanguageDictionary.GET_NAME};
		
		newChat.mode = ChatMode.FamilyName;
		
		return result;
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
		if (chat.mode == ChatMode.Private) {
			firstLine = LanguageDictionary.HAVE_PRIVATE_MESSAGE;
		} else if (chat.mode == ChatMode.Public) {
			firstLine = LanguageDictionary.HAVE_PUBLIC_MESSAGE + "(" + chat.intern.name + " " + chat.intern.familyName
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
