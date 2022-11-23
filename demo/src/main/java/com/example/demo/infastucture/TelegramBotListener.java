package com.example.demo.infastucture;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.demo.domain.User;
import com.example.demo.domain.UserInteractionListener;
import com.example.demo.domain.UserProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramBotListener extends TelegramLongPollingBot implements UserInteractionListener {

	final private UserProvider userProvider;

	@Override
	public void onUpdateReceived(Update update) {

		if (Objects.isNull(update.getMessage())) {
			return;
		}

		final long chatId = update.getMessage().getChatId();
		String userName = update.getMessage().getFrom().getFirstName();
		// TODO: get email from user
		userProvider.setUser(new User(chatId, userName, "max.mustermann@gmail.com"));
		log.info("{} just joined", userName);

	}

	public void addtoUsers(final Long chatId, final String name, final String email) {
		userProvider.setUser(new User(chatId, name, email));
	}

	public void sendMessage(final String message) {
		SendMessage m = new SendMessage();
		m.setChatId(userProvider.getUser().getId().toString());
		m.setText(message);
		try {
			execute(m);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	// TODO: put to constants
	@Override
	public String getBotUsername() {
		return "botname";
	}

	@Override
	public String getBotToken() {
		return "token";
	}

}