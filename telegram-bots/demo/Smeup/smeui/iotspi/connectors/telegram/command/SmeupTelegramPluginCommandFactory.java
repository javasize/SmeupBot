package Smeup.smeui.iotspi.connectors.telegram.command;

import java.util.ArrayList;
import java.util.Iterator;

import Smeup.smeui.iotspi.connectors.telegram.SmeupTelegramRequestListener;
import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.Command;
import io.github.nixtabyte.telegram.jtelebot.server.CommandFactory;

public class SmeupTelegramPluginCommandFactory implements CommandFactory
{

    // String iProviderAddress= null;
    // int iProviderPort= 9090;
    String iBotName = "";
    SmeupTelegramRequestListener iListener = null;

    public SmeupTelegramPluginCommandFactory(String aBotName,
                SmeupTelegramRequestListener aListener)
    {
        // iProviderAddress= aProviderAddress;
        // iProviderPort= aProviderPort;
        iBotName = aBotName;
        iListener = aListener;
    }

    @Override
    public Command createCommand(Message message, RequestHandler requestHandler)
    {
        System.out.println("MESSAGE: " + message.getText());
        SmeupTelegramPluginCommand vCommandManager = new SmeupTelegramPluginCommand(
                    message, requestHandler, iBotName);
        if(iListener != null)
        {
            vCommandManager.addListener(iListener);
        }
        return vCommandManager;
    }
}