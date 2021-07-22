import java.util.ArrayList;
import java.util.Arrays;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class AdminManager {
	private AdminMode adminMode = AdminMode.NONE;
	private final MyBot MY_BOT;
	private final ArrayList<Chat> CHATS;
	
	private InternshipType internshipType;
	
	public AdminManager(MyBot mybot, ArrayList<Chat> chats) {
		this.MY_BOT = mybot;
		this.CHATS = chats;
	}
	
	public void manage(Update update) {
		String msgTextReceived = update.getMessage().getText();
		Long chatId = update.getMessage().getChatId();
		
		ArrayList<String> msg = new ArrayList<>();
		
		switch (msgTextReceived) {
			case "/send_message" -> msg.addAll(new ArrayList<>(Arrays.asList(this.sendMessage())));
			case "/interns" -> msg.addAll(new ArrayList<>(this.getInternsInfo()));
			default -> modeCommands(msg, update);
		}
		
		for (String text : msg) {
			SendMessage sm = new SendMessage();
			sm.setText(text);
			sm.setChatId(chatId);
			
			try {
				this.MY_BOT.execute(sm);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String[] sendMessage() {
		this.adminMode = AdminMode.SELECT_AUDIENCE;
		
		return new String[]{LanguageDictionary.SELECT_AUDIENCE};
	}
	
	public ArrayList<String> getInternsInfo() {
		ArrayList<String> result = new ArrayList<>();
		
		for (Chat chat : CHATS) {
			result.add(this.getInternInfo(chat));
		}
		
		return result;
	}
	
	public String getInternInfo(Chat chat) {
		return Utils.generateInfoMessage(chat, true);
	}
	
	public void modeCommands(ArrayList<String> msg, Update update) {
		switch (adminMode) {
			case SEND_MESSAGE:
				msg.addAll(new ArrayList<>(Arrays.asList(this.sendToAll(update))));
				break;
			case SELECT_AUDIENCE:
				msg.addAll(new ArrayList<>(Arrays.asList(this.selectAudience(update))));
				break;
			default:
				break;
		}
	}
	
	public String[] sendToAll(Update update) {
		String msg = update.getMessage().getText();
		
		for (Chat chat : CHATS) {
			SendMessage sm = new SendMessage();
			sm.setText(msg);
			sm.setChatId(chat.id);
			
			if (internshipType != null && chat.intern.internshipType != internshipType)
				continue;
			
			try {
				this.MY_BOT.execute(sm);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
		
		String[] result = {LanguageDictionary.MESSAGE_HAS_BEEN_SENT_SUCCESSFULLY};
		this.adminMode = AdminMode.NONE;
		return result;
	}
	
	public String[] selectAudience(Update update) {
		String msg = update.getMessage().getText();
		switch (msg) {
			case "/ui" -> this.internshipType = InternshipType.UI;
			case "/fe" -> this.internshipType = InternshipType.FE;
			case "/se" -> this.internshipType = InternshipType.SE;
			case "/all" -> this.internshipType = null;
		}
		
		String[] result = {LanguageDictionary.SEND_MESSAGE};
		this.adminMode = AdminMode.SEND_MESSAGE;
		return result;
	}
}
