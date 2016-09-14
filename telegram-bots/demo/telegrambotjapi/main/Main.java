package telegrambotjapi.main;

import config.BotData;
import de.vivistra.telegrambot.client.Bot;
import de.vivistra.telegrambot.client.BotRequest;
import de.vivistra.telegrambot.client.BotResponse;
import de.vivistra.telegrambot.model.message.Message;
import de.vivistra.telegrambot.model.message.TextMessage;
import de.vivistra.telegrambot.settings.BotSettings;

public class Main {
    public static void main(String[] args) {
        BotSettings.setApiToken(BotData.BOT_JAVASIZE_TOKEN);

        Bot vClient= new Bot();
        
        Message vMessage= new TextMessage(199971507, "Ciao");
        BotRequest vReq= new BotRequest(vMessage);
        
        BotResponse vResp= vClient.post(vReq);
        System.out.println(vResp.getMessages()[0].getMessage().toString());
    }
}