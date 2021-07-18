import java.util.ArrayList;
import java.util.Arrays;
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

        ArrayList<String> msg = new ArrayList<>();

        switch (msgTextReceived) {
            case "/start":
                msg.addAll(new ArrayList<>(Arrays.asList(this.start(update, currentChat))));
                break;
            case "/private":
                  msg.addAll(new ArrayList<>(Arrays.asList(this.privateC(update, currentChat))));
                break;
            case "/public":
                msg.addAll(new ArrayList<>(Arrays.asList(this.publicC(update, currentChat))));
                break;
            default:
                modeCommands(currentChat, msg, update);
                break;
        }

        for (int i = 0; i < msg.size(); i++) {
            SendMessage sm = new SendMessage();
            sm.setText(msg.get(i));
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

    public void modeCommands(Chat currentChat, ArrayList<String> msg, Update update) {
        switch (currentChat.mode) {
            case Name:
                msg.addAll(new ArrayList<>(Arrays.asList(this.nameMode(update, currentChat))));
                break;
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
            case FinishGetInfo:
                msg.addAll(new ArrayList<>(Arrays.asList(this.finishGetInfoMode(update, currentChat))));
                break;
            case Public:
                msg.addAll(new ArrayList<>(Arrays.asList(this.publicMode(update, currentChat))));
                break; 
            case Private:
                msg.addAll(new ArrayList<>(Arrays.asList(this.privateMode(update, currentChat))));
                break; 
            default:
                break;
        }
    }

    public String[] privateMode(Update update, Chat chat) {
        String[] result = { 
            "پیامتان به صورت ناشناس به دست ما رسید.😊\nممنون از توجهتون 🌺"
        };
        return result;
    }

    public String[] publicMode(Update update, Chat chat) {
        String[] result = { 
            "پیامتان به صورت شناس به دست ما رسید.😊\nممنون از توجهتون 🌺"
        };
        return result;
    }

    public String[] finishGetInfoMode(Update update, Chat chat) {
        String[] result = { 
            "ممنون که وقت گذاشتید و فرم مارا پر کردید😊🌺\nازین پس هر پیامی اینجا بنویسید به دست ما خواهد رسید.\nدرصورتی که عبارت\n/private\nرا وارد کنید پیام‌هایتان به صورت ناشناس به دست ما خواهد رسید.\n\nدر صورتی که عبارت\n/public\nرا وارد کنید پیامتان به صورت شناس به دست ما خواهد رسید.\n\nبا آرزوی موفقیت برایتان🌺"
        };
        chat.mode = ChatMode.Public;
        return result;
    }

    public String[] publicC(Update update, Chat chat) {
        String[] result = { "ازین پس هر پیامی اینجا بنویسید به صورت شناس به دست ما خواهد رسید.\nدرصورتی که عبارت\n/private\nرا وارد کنید پیام‌هایتان به صورت ناشناس به دست ما خواهد رسید." };
        chat.mode = ChatMode.Public;
        return result;
    }

    public String[] privateC(Update update, Chat chat) {
        String[] result = { "ازین پس هر پیامی اینجا بنویسید به صورت ناشناس به دست ما خواهد رسید.\nدرصورتی که عبارت\n/public\nرا وارد کنید پیام‌هایتان به صورت شناس به دست ما خواهد رسید." };
        chat.mode = ChatMode.Private;
        return result;
    }

    public String[] codePostMode(Update update, Chat chat) {
        String[] result = { "کدپستی منزل خود را وارد کنید:\n\nفرمت درست:\n1234567890" };
        chat.mode = ChatMode.Address;
        return result;
    }

    public String[] addressMode(Update update, Chat chat) {
        String[] result = { "آدرس منزل خود را وارد کنید:" };
        chat.mode = ChatMode.FinishGetInfo;
        return result;
    }

    public String[] phoneNumberMode(Update update, Chat chat) {
        String[] result = { "شماره تلفن خود را وارد کنید:\n\n(ترجیحا ایرانسل)\nفرمت درست:\n09121234567" };
        chat.mode = ChatMode.CodePost;
        return result;
    }

    public String[] familyNameMode(Update update, Chat chat) {
        String[] result = { "نام‌خانوادگی خود را وارد کنید:\n\nفرمت درست:\nمحمدی" };
        chat.mode = ChatMode.PhoneNumber;
        return result;
    }

    public String[] nameMode(Update update, Chat chat) {
        String[] result = { "نام‌ خود را وارد کنید:\n\nفرمت درست:\nمحمد" };
        chat.mode = ChatMode.FamilyName;
        return result;
    }

    public String[] start(Update update, Chat chat) {
        if (chat != null) {
            String[] result = { "شما قبلا در سامانه ثبت‌نام کرده‌اید." };
            return result;
        }

        Chat newChat = new Chat(update.getMessage().getChatId());
        this.chats.add(newChat);
        
        String[] result = { 
            "سلام 🙋‍♂️\nبه کارآموزی تابستانه‌ی کداستار خوش‌آمدید.😊\nبرای اطلاع رسانی سریع‌تر و تعامل با شما عزیزان این ربات تلگرامی کداستار را تهیه کردیم.\nلطفا اطلاعات خواسته شده را با دقت پر کنید:" ,
            "نام خود را وارد کنید:\n\nفرمت درست:\nمحمد"
        };

        newChat.mode = ChatMode.FamilyName;

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
