package kshv.org.bot.core;

import kshv.org.bot.core.interfaces.BotService;
import kshv.org.bot.core.services.loader.BotActionManagerService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Component
public final class BotCore extends TelegramLongPollingBot {

    @Value("${bot.access.token}")
    private String token;

    @Value("${bot.name}")
    private String username;

    private final Logger logger;
    private final List<BotService> botServicesList;
    private final BotActionManagerService actionManagerService;

    @Autowired
    public BotCore(Logger logger, List<BotService> botServicesList, BotActionManagerService actionManagerService) {
        this.logger = logger;
        this.botServicesList = botServicesList;
        this.actionManagerService = actionManagerService;
        logger.info("core constructed");
    }

    public static Optional<SendMessage> newResponseTextMessage(Message message, String text) {
        Optional<SendMessage> response = Optional.of(new SendMessage());
        Long chatId = message.getChatId();
        response.get().setChatId(String.valueOf(chatId));
        response.get().setReplyToMessageId(message.getMessageId());
        response.get().setText(text);
        return response;
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage()) {
                try {
                    Message message = update.getMessage();
                    logger.info("Incoming message \"{}\" to {}", message.getText(), message.getChat().getTitle());
                    if (botServicesList != null && !botServicesList.isEmpty()) {
                        for (BotService botService : botServicesList) {
                            if (botService.validateUserCommandString(message))
                                execute(botService.performServiceAndGetResult(message).orElseGet(SendMessage::new));
                        }
                    }
                } catch (TelegramApiException e) {
                    logger.error("Failed to send message due to error: {}", e.getMessage());
                }
            }
        }
        actionManagerService.updateActionList(botServicesList);
    }

    @Override
    public void onRegister() {
        logger.info("action loader init");
        actionManagerService.initAllStoredActions(botServicesList);
        actionManagerService.updateActionList(botServicesList);
        logger.info("action loader init completed, bot registered");
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
