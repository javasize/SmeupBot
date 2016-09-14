package jtelebotcore.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.Element;

import Smeup.smec_s.utility.StringUtility;
import Smeup.smeui.loa39.utility.A39Client;
import Smeup.smeui.loa39.utility.A39Connection;
import Smeup.smeui.uicommon.uixmlservice.UIXmlProvider;
import Smeup.smeui.uimainmodule.UIFunInputStructure;
import Smeup.smeui.uiutilities.UIXmlUtilities;
import config.BotData;
import config.MessageType;
import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.Contact;
import io.github.nixtabyte.telegram.jtelebot.response.json.CustomReplyKeyboard;
import io.github.nixtabyte.telegram.jtelebot.response.json.Location;
import io.github.nixtabyte.telegram.jtelebot.response.json.Message;
import io.github.nixtabyte.telegram.jtelebot.response.json.PhotoSize;
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