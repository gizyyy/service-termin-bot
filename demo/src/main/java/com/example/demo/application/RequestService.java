package com.example.demo.application;

import java.util.Objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.domain.UserInteractionListener;
import com.example.demo.domain.UserProvider;
import com.example.demo.infastucture.TelegramBotListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

	private final WebDriver chromeDriverForDiscovery;

	private final UserInteractionListener bot;

	private final UserProvider userProvider;

	@Scheduled(cron = "*/10 * * * * *")
	public void checkAvailability() {
		log.info("Bot is checking..");
		// anmeldung https://service.berlin.de/dienstleistung/120686/
		// meldebescheinigung https://service.berlin.de/dienstleistung/120702/

		chromeDriverForDiscovery.get("https://service.berlin.de/dienstleistung/120686/");

		WebElement berlinWeitSearch = chromeDriverForDiscovery.findElement(By.cssSelector("div.zmstermin-multi a"));
		berlinWeitSearch.click();
		WebElement termininThisMonth = null;
		try {
			termininThisMonth = chromeDriverForDiscovery
					.findElement(By.cssSelector("div.calendar-month-table .buchbar"));
		} catch (Exception e) {
			log.info("Bot could not find something close");
		}

		if (Objects.isNull(termininThisMonth)) {
			return;
		}

		// termin found and we need to chose closest one
		// notify user
		bot.sendMessage("Looks like we almost get you a termin, are you ready?");
		// reserve the first possibility
		chromeDriverForDiscovery.findElement(By.cssSelector("div.calendar-month-table .buchbar a")).click();
		
		// try to fill the form
		try {
			chromeDriverForDiscovery.findElement(By.cssSelector("td.frei a")).click();
			WebElement name = chromeDriverForDiscovery.findElement(By.id("familyName"));
			name.sendKeys(userProvider.getUser().getName());
			WebElement mail = chromeDriverForDiscovery.findElement(By.id("email"));
			mail.sendKeys(userProvider.getUser().getName());

			Select survey = new Select(chromeDriverForDiscovery.findElement(By.name("surveyAccepted")));
			survey.selectByVisibleText("Nicht zustimmen");

			WebElement checkbox = chromeDriverForDiscovery.findElement(By.id("agbgelesen"));
			if (!checkbox.isSelected())
				checkbox.click();

			WebElement submitTheform = chromeDriverForDiscovery.findElement(By.cssSelector("#register_submit"));
			submitTheform.click();
		} catch (Exception e) {
			log.info("Bot failed because someone else was faster");
			bot.sendMessage("I tried so hard and got so far but in the end it does not even matter");
			return;
		}
		bot.sendMessage("Looks like we got something for you, check your email..");

	}
}
