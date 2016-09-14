package telegrambotjapi.main;

import de.vivistra.telegrambot.model.message.Message;
import de.vivistra.telegrambot.receiver.IReceiverService;

public class GetMessage implements IReceiverService {

    @Override
    public void received(Message message) {
        switch (message.getMessageType()) {
        case TEXT_MESSAGE:
            String sender = message.getSender().toString();

            String text = message.getMessage().toString();

            System.out.println(sender + " wrote: " + text);

            break;
        default:
            System.out.println("Ignore received message.");
        }
    }
}