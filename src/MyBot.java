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

    private AdminManager adminManager;

    public MyBot() {
        super();

        getDB();

        this.adminManager = new AdminManager(this, this.chats);
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

        if (chatId.equals(Security.ADMIN_CHAT_ID)) {
            this.adminManager.manage(update);
            return;
        }

        ArrayList<String> msg = new ArrayList<>();

        if (currentChat == null && !msgTextReceived.equals("/start")) {
            msg.addAll(new ArrayList<>(Arrays.asList(this.registerAgain())));
        }

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
            case "/edit_name":
                msg.addAll(new ArrayList<>(Arrays.asList(this.editName(currentChat))));
                break;
            case "/edit_familyname":
                msg.addAll(new ArrayList<>(Arrays.asList(this.editFamilyname(currentChat))));
                break;
            case "/edit_postcode":
                msg.addAll(new ArrayList<>(Arrays.asList(this.editPostcode(currentChat))));
                break;
            case "/edit_address":
                msg.addAll(new ArrayList<>(Arrays.asList(this.editAddress(currentChat))));
                break;
            case "/edit_phonenumber":
                msg.addAll(new ArrayList<>(Arrays.asList(this.editPhoneNumber(currentChat))));
                break;
            case "/displaymyinfo":
                msg.addAll(new ArrayList<>(Arrays.asList(this.getInfo(currentChat))));
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

        updateDB();
    }

    @Override
    public String getBotToken() {
        return Security.TOKEN;
    }

    public String[] registerAgain() {
        String[] result = { LanguageDictionary.REGISTER_AGAIN };

        return result;
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
        message += "کدپستی: " + chat.intern.postCode;

        String[] result = { message };
        return result;
    }

    public String[] editName(Chat chat) {
        chat.mode = ChatMode.EditName;

        String[] result = { LanguageDictionary.GET_NAME };
        return result;
    }

    public String[] editFamilyname(Chat chat) {
        chat.mode = ChatMode.EditFamilyName;

        String[] result = { LanguageDictionary.GET_FAMILYNAME };
        return result;
    }

    public String[] editAddress(Chat chat) {
        chat.mode = ChatMode.EditAddress;

        String[] result = { LanguageDictionary.GET_ADDRESS };
        return result;
    }

    public String[] editPhoneNumber(Chat chat) {
        chat.mode = ChatMode.EditPhoneNumber;

        String[] result = { LanguageDictionary.GET_PHONE_NUMBER };
        return result;
    }

    public String[] editPostcode(Chat chat) {
        chat.mode = ChatMode.EditPostCode;

        String[] result = { LanguageDictionary.GET_POST_CODE };
        return result;
    }

    public String[] editNameMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.SUCCESS_REQUEST };

        chat.intern.name = update.getMessage().getText();
        chat.mode = ChatMode.Private;
        return result;
    }

    public String[] editFamilynameMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.SUCCESS_REQUEST };

        chat.intern.familyName = update.getMessage().getText();
        chat.mode = ChatMode.Private;
        return result;
    }

    public String[] editPhoneNumberMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.SUCCESS_REQUEST };

        chat.intern.phoneNumber = update.getMessage().getText();
        chat.mode = ChatMode.Private;
        return result;
    }

    public String[] editAddressMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.SUCCESS_REQUEST };

        chat.intern.address = update.getMessage().getText();
        chat.mode = ChatMode.Private;
        return result;
    }

    public String[] editPostodeMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.SUCCESS_REQUEST };

        chat.intern.postCode = update.getMessage().getText();
        chat.mode = ChatMode.Private;
        return result;
    }

    public String[] privateMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.MESSAGE_SENDED_PRIVATE };

        sendResponseToAdmin(update, chat);
        return result;
    }

    public String[] publicMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.MESSAGE_SENDED_PUBLIC };

        sendResponseToAdmin(update, chat);
        return result;
    }

    public String[] finishGetInfoMode(Update update, Chat chat) {
        String[] result = { LanguageDictionary.FINISH_GET_INFORMATION };
        chat.mode = ChatMode.Private;

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

    public void sendResponseToAdmin(Update update, Chat chat) {
        String firstLine = "";
        if (chat.mode == ChatMode.Private) {
            firstLine = LanguageDictionary.HAVE_PRIVATE_MESSAGE;
        } else if (chat.mode == ChatMode.Public) {
            firstLine = LanguageDictionary.HAVE_PUBLIC_MESSAGE + "(" + chat.intern.name + " " + chat.intern.familyName
                    + ")\n";
        }

        String message = firstLine + update.getMessage().getText();

        SendMessage sm = new SendMessage();
        sm.setText(message);
        sm.setChatId(Security.ADMIN_CHAT_ID);

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
