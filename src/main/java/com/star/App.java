package com.star;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App {
	public static void main(String[] args) throws Exception {
		TelegramBotsApi bot = new TelegramBotsApi(DefaultBotSession.class);
		
		bot.registerBot(new MyBot());
	}
}
