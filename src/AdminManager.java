import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class AdminManager {
    private AdminMode adminMode = AdminMode.None;
    private MyBot myBot;
    private ArrayList<Chat> chats;

    private InternType sendMessageType;

    public AdminManager(MyBot mybot, ArrayList<Chat> chats) {
        this.myBot = mybot;
        this.chats = chats;
    }

    public void manage(Update update) {
        String msgTextReceived = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        ArrayList<String> msg = new ArrayList<>();

        switch (msgTextReceived) {
            case "/send_message":
                msg.addAll(new ArrayList<>(Arrays.asList(this.sendMessage())));
                break;
            case "/interns":
                msg.addAll(new ArrayList<>(this.getInternsInfo()));
                break;
            default:
                modeCommands(msg, update);
                break;
        }

        for (int i = 0; i < msg.size(); i++) {
            SendMessage sm = new SendMessage();
            sm.setText(msg.get(i));
            sm.setChatId(chatId);

            try {
                this.myBot.execute(sm);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public String[] sendMessage() {
        this.adminMode = AdminMode.GetGroupType;

        String[] result = {
                "نوع کارآموزی را انتخاب کنید:\nرابط کاربری\n/pa\nفرانت‌اند\n/fe\nمهندسی‌نرم‌افزار\n/se\nهمه\n/all" };
        return result;
    }

    public ArrayList<String> getInternsInfo() {
        ArrayList<String> result = new ArrayList<>();

        for (Chat chat : chats) {
            result.add(this.getInternInfo(chat));
        }

        return result;
    }

    public String getInternInfo(Chat chat) {
        String message = "";

        message += "نام: " + chat.intern.name + "\n";
        message += "نام‌خانوادگی: " + chat.intern.familyName + "\n";
        message += "شماره‌موبایل: " + chat.intern.phoneNumber + "\n";
        message += "آدرس: " + chat.intern.address + "\n";
        message += "کدپستی: " + chat.intern.postCode + "\n";

        String type = "";
        if (chat.intern.type == InternType.PA) {
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
            case SendMessage:
                msg.addAll(new ArrayList<>(Arrays.asList(this.sendToAll(update))));
                break;
            case GetGroupType:
                msg.addAll(new ArrayList<>(Arrays.asList(this.getGroupType(update))));
                break;
            default:
                break;
        }
    }

    public String[] sendToAll(Update update) {
        String msg = update.getMessage().getText();

        for (Chat chat : chats) {
            SendMessage sm = new SendMessage();
            sm.setText(msg);
            sm.setChatId(chat.id);
            if (sendMessageType == null) {
            } else if (chat.intern.type != sendMessageType) {
                continue;
            }

            try {
                this.myBot.execute(sm);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        String[] result = { "پیام با موفقیت ارسال شد." };
        this.adminMode = AdminMode.None;
        return result;
    }

    public String[] getGroupType(Update update) {
        String msg = update.getMessage().getText();
        if (msg.equals("/pa")) {
            this.sendMessageType = InternType.PA;
        } else if (msg.equals("/fe")) {
            this.sendMessageType = InternType.FE;
        } else if (msg.equals("/se")) {
            this.sendMessageType = InternType.SE;
        } else if (msg.equals("/all")) {
            this.sendMessageType = null;
        }

        String[] result = { "پیام را ارسال کنید." };
        this.adminMode = AdminMode.SendMessage;
        return result;
    }
}
