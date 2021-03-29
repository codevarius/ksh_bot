package kshv.org.bot.core;

import kshv.org.bot.core.data.BotQuoteEntity;
import kshv.org.bot.core.data.BotQuoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Random;

@Component
public class BotCore extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(BotCore.class);

    private final RestTemplate restTemplate;
    private final BotQuoteRepository botQuoteRepository;

    @Autowired
    public BotCore(RestTemplate restTemplate, BotQuoteRepository botQuoteRepository) {
        this.restTemplate = restTemplate;
        this.botQuoteRepository = botQuoteRepository;
    }

    @Value("${bot.access.token}")
    private String token;

    @Value("${bot.name}")
    private String username;

    @Value("${quote.gen.uri}")
    private String quoteGenUri;

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public String getBotUsername() {
        return username;
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
    public void onUpdatesReceived(List<Update> updates) {
        for (Update update : updates) {
            if (update.hasMessage()) {
                try {
                    Message message = update.getMessage();
                    execute(performResponse(message));
                    logger.info("Sent message \"{}\" to {}", message.getText(), message.getChat().getTitle());
                } catch (TelegramApiException e) {
                    logger.error("Failed to send message due to error: {}", e.getMessage());
                }
            }
        }
    }

    private SendMessage performResponse(Message message) {
        SendMessage response = new SendMessage();
        Long chatId = message.getChatId();
        response.setChatId(String.valueOf(chatId));
        BotQuoteEntity quote = restTemplate
                .getForEntity(quoteGenUri + "&" + getRandomSixDigitNumber(), BotQuoteEntity.class).getBody();
        if (quote != null) {
            botQuoteRepository.save(quote);
            String text = "Как сказал " + quote.getQuoteAuthor() + ": \"" + quote.getQuoteText() + "\"";
            response.setText(text);
        }
        return response;
    }

    private String getRandomSixDigitNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

}
