package kshv.org.bot.core;

import kshv.org.bot.core.interfaces.BotService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public final class BotCore extends TelegramLongPollingBot {

    @Value("${bot.access.token}")
    private String token;

    @Value("${bot.name}")
    private String username;

    private final Logger logger;
    private final BotService botService;
    private final List<BotService> botServicesList;

    @Autowired
    public BotCore(Logger logger, BotService botService, List<BotService> botServicesList) {
        this.logger = logger;
        this.botService = botService;
        this.botServicesList = botServicesList;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage()) {
                try {
                    Message message = update.getMessage();
                    logger.info("Incoming message \"{}\" to {}", message.getText(), message.getChat().getTitle());
                    for (BotService botService : botServicesList) {
                        execute(botService.performServiceAndGetResult(message));
                    }
                } catch (TelegramApiException e) {
                    logger.warn("Failed to send message due to error: {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onRegister() {
        logger.info("bot registered");

    }

    @Override
    public void onUpdateReceived(Update update) {
        logger.info("received new message");
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }
}
