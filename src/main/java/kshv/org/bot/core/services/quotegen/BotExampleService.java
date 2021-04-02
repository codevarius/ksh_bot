package kshv.org.bot.core.services.quotegen;

import kshv.org.bot.core.interfaces.BotService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;

//simply example of bot service implementation
@Service
public class BotExampleService implements BotService {

    @Override
    public Optional<SendMessage> performServiceAndGetResult(Message message) {
        Optional<SendMessage> response = Optional.of(new SendMessage());
        Long chatId = message.getChatId();
        response.get().setChatId(String.valueOf(chatId));
        response.get().setText("example success!");
        return response;
    }

    @Override
    public Boolean validateMessage(Message message) {
        byte[] array = new byte[10];
        new Random().nextBytes(array);
        return message.getText().contains(new String(array, StandardCharsets.UTF_8)); //unreachable condition 🎁
    }
}
