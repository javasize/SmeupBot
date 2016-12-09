package Smeup.smeui.iotspi.connectors.telegram;

import de.vivistra.telegrambot.model.message.Message;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;

public interface SmeupMessageProcessor
{
    public TelegramRequest getRequest(Message aMessage);

}
