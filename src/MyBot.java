import java.util.ArrayList;
import java.util.Arrays;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class MyBot extends TelegramLongPollingBot {
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
        return Security.TOKEN;
    }

    public void modeCommands(Chat currentChat, ArrayList<String> msg, Update update) {
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
        String[] result = { LanguageDictionary.MESSAGE_SENDED_PRIVATE };
        return result;
    }

    public String[] publicMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.MESSAGE_SENDED_PUBLIC };
        return result;
    }

    public String[] finishGetInfoMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.FINISH_GET_INFORMATION };
        chat.mode = ChatMode.Public;

        chat.intern.address = update.getMessage().getText();
        return result;
    }

    public String[] publicC(Update update, Chat chat) {
        String[] result = { LanguageDictionary.CHANGED_TO_PUBLIC };
        chat.mode = ChatMode.Public;
        return result;
    }

    public String[] privateC(Update update, Chat chat) {
        String[] result = { LanguageDictionary.CHANGED_TO_PRIVATE };
        chat.mode = ChatMode.Private;
        return result;
    }

    public String[] codePostMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.GET_POST_CODE };
        chat.mode = ChatMode.Address;

        chat.intern.phoneNumber = update.getMessage().getText();
        return result;
    }

    public String[] addressMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.GET_ADDRESS };
        chat.mode = ChatMode.FinishGetInfo;

        chat.intern.postCode = update.getMessage().getText();
        return result;
    }

    public String[] phoneNumberMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.GET_PHONE_NUMBER };
        chat.mode = ChatMode.CodePost;

        chat.intern.familyName = update.getMessage().getText();
        return result;
    }

    public String[] familyNameMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.GET_FAMILYNAME };
        chat.mode = ChatMode.PhoneNumber;

        chat.intern.name = update.getMessage().getText();
        return result;
    }

    public String[] start(Update update, Chat chat) {
        if (chat != null) {
            String[] result = { LanguageDictionary.YOU_REGISTERED };
            return result;
        }

        Chat newChat = new Chat(update.getMessage().getChatId());
        this.chats.add(newChat);

        String[] result = { LanguageDictionary.START, LanguageDictionary.GET_NAME };

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
