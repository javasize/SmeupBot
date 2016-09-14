package telegrambotjapi.main;

import config.BotData;
import de.vivistra.telegrambot.receiver.Receiver;
import de.vivistra.telegrambot.receiver.UpdateRequest;
import de.vivistra.telegrambot.settings.BotSettings;

public class BotRunner
{
    
    public static void main(String[] args)
    {
        BotSettings.setApiToken(BotData.BOT_JAVASIZE_TOKEN);
        Receiver.subscribe(new SmeupReceiver());
//        MyReceiver vReceiver= new MyReceiver();
    }
}
