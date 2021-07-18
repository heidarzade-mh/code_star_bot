import java.util.ArrayList;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MyBot extends TelegramLongPollingBot {
    public String token = "";
    public String username = "Code_star_bot";
    public ArrayList<Chat> chats = new ArrayList<>(); 

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String msgTextReceived = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Chat currentChat = getChat(chatId);

        String[] msg = null;

        switch (msgTextReceived) {
            case "/start":
                msg = this.start(update, currentChat);
                break;
            default:
                break;
        }

        for (int i = 0; i < msg.length; i++) {
            SendMessage sm = new SendMessage();
            sm.setText(msg[i]);
            sm.setChatId(chatId);

            try {
                execute(sm);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    public String[] start(Update update, Chat chat) {
        if (chat != null) {
            String[] result = { "شما قبلا در سامانه ثبت‌نام کرده‌اید." };
            return result;
        }

        Chat newChat = new Chat(update.getMessage().getChatId());
        this.chats.add(newChat);
        
        String[] result = { 
            "سلام 🙋‍♂️\nبه کارآموزی تابستانه‌ی کداستار خوش‌آمدید.😊\nبرای اطلاع رسانی سریع‌تر و تعامل با شما عزیزان این ربات تلگرامی کداستار را تهیه کردیم.\nلطفا اطلاعات خواسته شده را با دقت پر کنید:" 
        };

        return result;
    }

    public Chat getChat(Long id) {
        for (int i = 0; i < this.chats.size(); i++) {
            if (chats.get(i).id.equals(id)) {
                return chats.get(i);
            }
        }

        return null;
    }
}
