package jtelebotcore.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import config.BotData;
import io.github.nixtabyte.telegram.jtelebot.client.impl.DefaultRequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.ReplyKeyboardHide;
import io.github.nixtabyte.telegram.jtelebot.response.json.TelegramResponse;

public class SmeupRequestSender
{
    private static long ID_MAGNI= 247836496;
    private static long ID_IO= 199971507;
    private static long ID_DARIO= 219217733;
    private static long ID_MORELLI= 125377266;
    private static long ID_GAGLIARDO= 41720880;    
                
    private final ScheduledExecutorService iScheduler = Executors
                .newScheduledThreadPool(1);
    DefaultRequestHandler iRequester = new DefaultRequestHandler(
                                                                 BotData.BOT_SMEUP_TOKEN);

    public SmeupRequestSender()
    {
    }

    public void init()
    {
        final Runnable vNotifier = new Runnable()
        {
            public void run()
            {
                sendText("Sme.UP ERP ti parla... "+System.currentTimeMillis());
            }
        };
        final ScheduledFuture<?> vNotifierHandle = iScheduler
                    .scheduleAtFixedRate(vNotifier, 10, 10, TimeUnit.SECONDS);
        iScheduler.schedule(new Runnable()
        {
            public void run()
            {
                vNotifierHandle.cancel(true);
            }
        }, 60 * 60, TimeUnit.SECONDS);
    }

    public void sendText(String aText)
    {
        StartReplyKeyboardMarkup keyboard = new StartReplyKeyboardMarkup();
//        keyboard.setHideKeyboard(true);
        keyboard.setSelective(false);
        TelegramResponse<?> jsonResponse = null;
        try
        {
            // Dario
            jsonResponse = iRequester.sendRequest(TelegramRequestFactory
                                                  .createSendMessageRequest(ID_DARIO, aText, true, null,
                                                                            keyboard));
            System.out.println(jsonResponse);
            // Io
            jsonResponse = iRequester.sendRequest(TelegramRequestFactory
                                                  .createSendMessageRequest(ID_IO, aText, true, null,
                                                                            keyboard));
            System.out.println(jsonResponse);
            // Morelli
//            jsonResponse = iRequester.sendRequest(TelegramRequestFactory
//                        .createSendMessageRequest(ID_MORELLI, aText, false, null,
//                                                  keyboard));
//            System.out.println(jsonResponse);
        }
        catch(JsonParsingException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        catch(TelegramServerException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        System.out.println(jsonResponse);
    }

    public static void main(String[] args)
    {
        new SmeupRequestSender().init();
    }
}
