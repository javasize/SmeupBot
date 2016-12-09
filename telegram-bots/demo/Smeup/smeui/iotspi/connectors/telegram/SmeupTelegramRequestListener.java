package Smeup.smeui.iotspi.connectors.telegram;

import io.github.nixtabyte.telegram.jtelebot.response.json.Message;

public interface SmeupTelegramRequestListener
{
    public void requestReceived(Message aText);
}
