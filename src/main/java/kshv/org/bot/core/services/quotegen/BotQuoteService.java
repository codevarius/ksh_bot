package kshv.org.bot.core.services.quotegen;

import kshv.org.bot.core.interfaces.BotService;
import kshv.org.bot.core.services.quotegen.data.BotQuoteEntity;
import kshv.org.bot.core.services.quotegen.data.BotQuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class BotQuoteService implements BotService {

    @Value("${quote.gen.uri}")
    private String quoteGenUri;

    @Value("${quote.gen.params.trigger.pattern}")
    private String triggerPatternStr;

    @Value("${quote.gen.params.cool_down.time.amount}")
    private Short coolDownTimeAmount;

    private final RestTemplate restTemplate;
    private final BotQuoteRepository botQuoteRepository;

    @Autowired
    public BotQuoteService(RestTemplate restTemplate, BotQuoteRepository botQuoteRepository) {
        this.restTemplate = restTemplate;
        this.botQuoteRepository = botQuoteRepository;
    }

    @Override
    public Boolean validateMessage(Message message) {
        return message.getText() != null
                && message.getText().contains(triggerPatternStr)
                && isServiceCooledDown(LocalDateTime.now());
    }

    @Override
    public Optional<SendMessage> performServiceAndGetResult(Message message) {
        Optional<SendMessage> response = Optional.of(new SendMessage());
        Long chatId = message.getChatId();
        response.get().setChatId(String.valueOf(chatId));
        BotQuoteEntity quote = restTemplate
                .getForEntity(quoteGenUri + getRandomSixDigitNumber(), BotQuoteEntity.class).getBody();
        if (quote != null) {
            quote.setQuoteGenDate(LocalDateTime.now());
            botQuoteRepository.save(quote);
            String text = "Как сказал " + quote.getQuoteAuthor() + ": \"" + quote.getQuoteText() + "\"";
            response.get().setText(text);
            return response;
        }
        return Optional.empty();
    }

    private boolean isServiceCooledDown(LocalDateTime now) {
        Optional<BotQuoteEntity> quoteEntity = botQuoteRepository.findLatestAddedBotQuoteEntity();
        if (quoteEntity.isPresent()) {
            LocalDateTime lastQuoteSentDate = quoteEntity.get().getQuoteGenDate();
            return now.isAfter(lastQuoteSentDate.plusMinutes(coolDownTimeAmount));
        } else {
            return true;
        }
    }

    protected String getRandomSixDigitNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
