package Smeup.smeui.iotspi.connectors.telegram.command;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.Command;
import io.github.nixtabyte.telegram.jtelebot.server.CommandFactory;
 
public class SmeupCommandFactory implements CommandFactory {
 
//    String iProviderAddress= null;
//    int iProviderPort= 9090;
    String iBotName= "";
    
    public SmeupCommandFactory(String aBotName)
    {
//        iProviderAddress= aProviderAddress;
//        iProviderPort= aProviderPort;
        iBotName= aBotName;
    }

    @Override
    public Command createCommand(Message message, RequestHandler requestHandler) {
        System.out.println("MESSAGE: "+message.getText());
        return new SmeupCommand(message,requestHandler, iBotName);
    }
}