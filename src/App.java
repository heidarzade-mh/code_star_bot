import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

public class App {
    public static void main(String[] args) throws Exception {
        ApiContextInitializer.init();
        TelegramBotsApi bot = new TelegramBotsApi();

        bot.registerBot(new MyBot());
    }
}
