package kshv.org.bot.core.interfaces;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

public interface BotService {

    /**
     * @param message message (text,doc etc.) from chat caught by bot
     * @return response ready to be sent back to chat
     */
    Optional<SendMessage> performServiceAndGetResult(Message message);

    Boolean validateUserCommandString(Message message);
}
