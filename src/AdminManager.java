import java.util.ArrayList;
import java.util.Arrays;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class AdminManager {
	private AdminMode adminMode = AdminMode.NONE;
	private final MyBot MY_BOT;
	private final ArrayList<Chat> CHATS;
	
	private InternType sendMessageType;
	
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
		this.adminMode = AdminMode.GET_GROUP_TYPE;
		
		return new String[]{
				"نوع کارآموزی را انتخاب کنید:\nرابط کاربری\n/pa\nفرانت‌اند\n/fe\nمهندسی‌نرم‌افزار\n/se\nهمه\n/all"};
	}
	
	public ArrayList<String> getInternsInfo() {
		ArrayList<String> result = new ArrayList<>();
		
		for (Chat chat : CHATS) {
			result.add(this.getInternInfo(chat));
		}
		
		return result;
	}
	
	public String getInternInfo(Chat chat) {
		String message = "";
		
		message += "نام: " + chat.intern.firstName + "\n";
		message += "نام‌خانوادگی: " + chat.intern.lastName + "\n";
		message += "شماره‌موبایل: " + chat.intern.phoneNumber + "\n";
		message += "آدرس: " + chat.intern.address + "\n";
		message += "کدپستی: " + chat.intern.postCode + "\n";
		
		String type = "";
		if (chat.intern.type == InternType.UI) {
			type = "رابط کاربری";
		} else if (chat.intern.type == InternType.FE) {
			type = "فرانت‌اند";
		} else if (chat.intern.type == InternType.SE) {
			type = "مهندسی نرم‌افزار";
		}
		message += "نوع کارآموزی: " + type;
		
		return message;
	}
	
	public void modeCommands(ArrayList<String> msg, Update update) {
		switch (adminMode) {
			case SEND_MESSAGE:
				msg.addAll(new ArrayList<>(Arrays.asList(this.sendToAll(update))));
				break;
			case GET_GROUP_TYPE:
				msg.addAll(new ArrayList<>(Arrays.asList(this.getGroupType(update))));
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
			
			if (sendMessageType != null && chat.intern.type != sendMessageType)
				continue;
			
			try {
				this.MY_BOT.execute(sm);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
		
		String[] result = {"پیام با موفقیت ارسال شد."};
		this.adminMode = AdminMode.NONE;
		return result;
	}
	
	public String[] getGroupType(Update update) {
		String msg = update.getMessage().getText();
		switch (msg) {
			case "/pa" -> this.sendMessageType = InternType.UI;
			case "/fe" -> this.sendMessageType = InternType.FE;
			case "/se" -> this.sendMessageType = InternType.SE;
			case "/all" -> this.sendMessageType = null;
		}
		
		String[] result = {"پیام را ارسال کنید."};
		this.adminMode = AdminMode.SEND_MESSAGE;
		return result;
	}
}
