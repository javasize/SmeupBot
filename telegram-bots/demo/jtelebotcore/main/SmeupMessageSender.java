package jtelebotcore.main;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import Smeup.smec_s.utility.Chronograph;
import config.BotData;
import io.github.nixtabyte.telegram.jtelebot.client.impl.DefaultRequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.TelegramResponse;

public class SmeupMessageSender
{
    public static long TIME_TO_SEND_DELTA= 8*60*60*1000+30*60*1000;
    
    private static long ID_MAGNI= 247836496;
    private static long ID_IO= 199971507;
    private static long ID_DARIO= 219217733;
    private static long ID_MORELLI= 125377266;
    private static long ID_GAGLIARDO= 41720880;    
                
    private final ScheduledExecutorService iScheduler = Executors
                .newScheduledThreadPool(1);
    DefaultRequestHandler iRequester = new DefaultRequestHandler(
                                                                 BotData.BOT_SMEUP_TOKEN);

    public SmeupMessageSender()
    {
    }

    public void init()
    {
        final Runnable vNotifier = new Runnable()
        {
            public void run()
            {
                
                GregorianCalendar vMidnightCal= new GregorianCalendar();
                vMidnightCal.set(vMidnightCal.get(GregorianCalendar.YEAR), vMidnightCal.get(GregorianCalendar.MONTH), vMidnightCal.get(GregorianCalendar.DATE), 0, 0, 0);
//                vMidnightCal.set(GregorianCalendar.HOUR_OF_DAY, 0);
//                vMidnightCal.set(GregorianCalendar.MINUTE, 0);
                long vMidnightTime= vMidnightCal.getTimeInMillis();
//                
                GregorianCalendar vNowCal= new GregorianCalendar();
                long vNowTime= vNowCal.getTimeInMillis();
//                long vNowMillis= System.currentTimeMillis();
                long vNowTimeDelta= vNowTime-vMidnightTime;
//                // Fra le 8:30:00 e le 8:30:10
                long vDelta= vNowTimeDelta-TIME_TO_SEND_DELTA;
                if(vDelta>0 && vDelta<(3600*1000)+1)
                {
                    ArrayList<String[]> vList= Utility.getNotificationList(BotData.BOT_SMEUP_TOKEN);
                    if(vList!=null)
                    {
                        Iterator<String[]> vIter= vList.iterator();
                        while(vIter.hasNext())
                        {
                            String[] vStrings = (String[]) vIter.next();
                            try
                            {
                                String vFirstName= vStrings.length>0? vStrings[0]:"";
                                String vLastName= vStrings.length>1? vStrings[1]:"";
                                long vID= vStrings.length>2? Long.parseLong(vStrings[2]):0;
                                String vText= vStrings.length>3? vStrings[3]:"";
                                if(vText!=null && !"".equalsIgnoreCase(vText)&& vID>0)
                                {
                                    sendText(vText, vFirstName, vLastName, vID);
                                }
                            }
                            catch(NumberFormatException vEx)
                            {
                                vEx.printStackTrace();
                                continue;
                            }
                        }
                    }
                    
                }
            }
        };
        final ScheduledFuture<?> vNotifierHandle = iScheduler
                    .scheduleAtFixedRate(vNotifier, 5000, 3600*1000, TimeUnit.MILLISECONDS);
//        iScheduler.schedule(new Runnable()
//        {
//            public void run()
//            {
//                vNotifierHandle.cancel(true);
//            }
//        }, 60*60*60, TimeUnit.SECONDS);
    }

    public void sendText(String aText, String aFirstName, String aLastName, long aChatID)
    {
        StartReplyKeyboardMarkup keyboard = new StartReplyKeyboardMarkup();
//        keyboard.setHideKeyboard(true);
        keyboard.setSelective(false);
        TelegramResponse<?> jsonResponse = null;
        try
        {
            System.out.println("Invio "+aText+" a "+aFirstName+" "+aLastName);
            SmeupResponseData vData= SmeupCommand.createSmeupResponse(aText, aFirstName, aLastName, aChatID, aChatID, new StartReplyKeyboardMarkup());
            jsonResponse = iRequester.sendRequest(TelegramRequestFactory
                                                  .createSendMessageRequest(aChatID, vData.getText(), true, null,
                                                                            vData.getKeyboard()));
            System.out.println(jsonResponse);
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
//        GregorianCalendar vHeightOClockCal= new GregorianCalendar();
//        vHeightOClockCal.set(GregorianCalendar.HOUR_OF_DAY, 8);
//        vHeightOClockCal.set(GregorianCalendar.MINUTE, 0);
//        long vHeightOClockTime= vHeightOClockCal.getTimeInMillis();
//
//        GregorianCalendar vMidnightCal= new GregorianCalendar();
//        vMidnightCal.set(GregorianCalendar.HOUR_OF_DAY, 0);
//        vMidnightCal.set(GregorianCalendar.MINUTE, 0);
//        long vNowTime= vMidnightCal.getTimeInMillis();
//        
//        System.out.println(vHeightOClockTime);
//        System.out.println(vNowTime);
//        System.err.println(vHeightOClockTime-vNowTime);
        new SmeupMessageSender().init();
    }
}
