package kshv.org.bot.core.interfaces;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface BotService {

    /**
     * @param message message (text,doc etc.) from chat caught by bot
     * @return response ready to be sent back to chat
     */
    SendMessage performServiceAndGetResult(Message message) throws TelegramApiException;
}
