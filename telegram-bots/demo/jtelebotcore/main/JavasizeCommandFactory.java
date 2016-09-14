package jtelebotcore.main;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.Command;
import io.github.nixtabyte.telegram.jtelebot.server.CommandFactory;
 
public class JavasizeCommandFactory implements CommandFactory {
 

    public JavasizeCommandFactory()
    {
    }

    @Override
    public Command createCommand(Message message, RequestHandler requestHandler) {
        System.out.println("MESSAGE: "+message.getText());
        return new JavasizeCommand(message,requestHandler);
    }
}