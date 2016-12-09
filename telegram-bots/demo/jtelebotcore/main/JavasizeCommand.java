package jtelebotcore.main;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.apache.http.message.BasicNameValuePair;

import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.CustomReplyKeyboard;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.server.impl.AbstractCommand;

public class JavasizeCommand extends AbstractCommand
{

    String iBotName= "Javasize_bot";
    
    Long MAX_TEXT_LENGTH = new Long(4096);

//    String iProviderAddress = null;
//    int iProviderPort = 9090;
//
    public JavasizeCommand(Message message, RequestHandler requestHandler)
    {
        super(message, requestHandler);
    }

    @Override
    public void execute()
    {
        try
        {
            TelegramRequest telegramRequest = createRequest(message);
            System.out.println(telegramRequest.toString());
            requestHandler.sendRequest(telegramRequest);
        }
        catch(JsonParsingException | TelegramServerException e)
        {
            e.printStackTrace();
        }
    }

    TelegramRequest createRequest(Message aMessage) throws JsonParsingException
    {
        CustomReplyKeyboard vKeyboardMarkup = new StartReplyKeyboardMarkup();
        long vUserID = aMessage.getFromUser().getId();
        String vFirstName = aMessage.getFromUser().getFirstName();
        String vLastName = aMessage.getFromUser().getLastName();
        TelegramRequest telegramRequest = null;
        String vTempDir= ".\\temp";
        new File(vTempDir).mkdirs();
        String vRespMsg = null;

        System.out.println("@"+iBotName+": "+"Messaggio da " + vFirstName + " "
                    + vLastName + ". Id: " + vUserID);
        
        String vRespText = "Javasize_bot disabilitato, collegarsi a @Smeup_bot";
        try
        {
            vRespMsg = new String(vRespText.getBytes(),
                        "UTF-8");
        }
        catch(UnsupportedEncodingException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }

        if(telegramRequest == null)
        {
            telegramRequest = TelegramRequestFactory
                        .createSendMessageRequest(message.getChat().getId(),
                                                  vRespMsg, true,
                                                  message.getId(),
                                                  vKeyboardMarkup);
            Iterator<BasicNameValuePair> vIter = telegramRequest.getParameters()
                        .iterator();
            while(vIter.hasNext())
            {
                BasicNameValuePair vType = (BasicNameValuePair) vIter.next();
                String vName = vType.getName();
                String vValue = vType.getValue();
                System.out.println(vName + ": " + vValue);

            }
        }
        return telegramRequest;
    }
}