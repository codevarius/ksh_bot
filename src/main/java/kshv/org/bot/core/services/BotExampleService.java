package kshv.org.bot.core.services;

import kshv.org.bot.core.BotCore;
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
    public Boolean validateUserCommandString(Message message) {
        byte[] array = new byte[10];
        new Random().nextBytes(array);
        return message.getText() != null && message.getText().contains(new String(array, StandardCharsets.UTF_8)); //unreachable condition 🎁
    }

    @Override
    public Optional<SendMessage> performServiceAndGetResult(Message message) {
        return BotCore.newResponseTextMessage(message, "example success!");
    }

}
