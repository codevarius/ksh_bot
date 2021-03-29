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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
    public SendMessage performServiceAndGetResult(Message message) throws TelegramApiException {
        if (message.getText().contains(triggerPatternStr) && isServiceCooledDown(LocalDateTime.now())) {
            SendMessage response = new SendMessage();
            Long chatId = message.getChatId();
            response.setChatId(String.valueOf(chatId));
            BotQuoteEntity quote = restTemplate
                    .getForEntity(quoteGenUri + "&" + getRandomSixDigitNumber(), BotQuoteEntity.class).getBody();
            if (quote != null) {
                quote.setQuoteGenDate(LocalDateTime.now());
                botQuoteRepository.save(quote);
                String text = "Как сказал " + quote.getQuoteAuthor() + ": \"" + quote.getQuoteText() + "\"";
                response.setText(text);
            }
            return response;
        }
        throw new TelegramApiException("response has not been sent back");
    }

    private boolean isServiceCooledDown(LocalDateTime now) {
        Optional<BotQuoteEntity> quoteEntity = botQuoteRepository.findLatestAddedBotQuoteEntity();
        if (quoteEntity.isPresent()) {
            LocalDateTime lastQuoteSentDate = quoteEntity.get().getQuoteGenDate();
            return LocalDateTime.now().isAfter(lastQuoteSentDate.plusMinutes(coolDownTimeAmount));
        }else{
            return true;
        }
    }

    private String getRandomSixDigitNumber() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
}
