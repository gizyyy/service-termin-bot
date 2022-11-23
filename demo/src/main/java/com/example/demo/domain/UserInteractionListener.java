package com.example.demo.domain;

public interface UserInteractionListener {

	public void addtoUsers(final Long chatId, final String name, final String email);

	public void sendMessage(final String message);
}
