package telegrambotjapi.main;

import config.BotData;
import de.vivistra.telegrambot.model.message.Message;
import de.vivistra.telegrambot.model.message.TextMessage;
import de.vivistra.telegrambot.sender.Sender;
import de.vivistra.telegrambot.settings.BotSettings;

public class Launcher {

    public static void main(String[] args) throws Exception {
        new Launcher();
    }

    private Launcher() throws Exception {

        // Set API token
        BotSettings.setApiToken(BotData.BOT_JAVASIZE_TOKEN);

        // A Telegram ID. It is a negative Integer for bots and a positive Integer for humans.
        int recipient = 199971507;

        // Create a message
        Message message = new TextMessage(recipient, "Hello =)");

        // Send the message
        Sender.send(message);
    }
}