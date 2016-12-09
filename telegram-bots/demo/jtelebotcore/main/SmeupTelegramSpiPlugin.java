package jtelebotcore.main;

import java.util.Enumeration;
import java.util.Iterator;

import Smeup.smeui.iotspi.datastructure.interfaces.SezInterface;
import Smeup.smeui.iotspi.datastructure.iotconnector.IoTConnectorConf;
import Smeup.smeui.iotspi.datastructure.iotconnector.IoTConnectorInput;
import Smeup.smeui.iotspi.datastructure.iotconnector.IoTConnectorResponse;
import Smeup.smeui.iotspi.interaction.SPIIoTConnectorAdapter;
import config.BotData;
import io.github.nixtabyte.telegram.jtelebot.response.json.TelegramResponse;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandDispatcher;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandQueue;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandWatcher;

public class SmeupTelegramSpiPlugin extends SPIIoTConnectorAdapter
{
//    SmeupMessageSender iTelegram = null;
    private SezInterface iSez = null;

    private IoTConnectorConf iConf = null;

    @Override
    public boolean init(SezInterface aSez, IoTConnectorConf aConfiguration)
    {
        iSez = aSez;
        iConf = aConfiguration;
//        String vBotSmeupToken= BotData.BOT_SMEUP_TOKEN;
//        String vBotUserName= BotData.BOT_SMEUP_USER_NAME;
        String vBotSmeupToken= iConf.getData("BotToken");
        String vBotUserName= iConf.getData("BotUserName");
        
        SmeupConnectors.init();

        DefaultCommandDispatcher vSmeupCommandDispatcher = new DefaultCommandDispatcher(
                    10, 100, 100, new DefaultCommandQueue());
        vSmeupCommandDispatcher.startUp();

        DefaultCommandWatcher vSmeupCommandWatcher = new DefaultCommandWatcher(
                    2000, 100, vBotSmeupToken, vSmeupCommandDispatcher,
                    new SmeupCommandFactory(vBotUserName));
        vSmeupCommandWatcher.startUp();

//        iTelegram = new SmeupMessageSender();
//        iTelegram.startDaemon(BotData.BOT_SMEUP_TOKEN);
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public IoTConnectorResponse invoke(IoTConnectorInput aDataTable)
    {
        String vText = aDataTable.getData("Text");
        String vFirstName = aDataTable.getData("FirstName");
        String vLastName = aDataTable.getData("LastName");
        long vChatID = Long.parseLong(aDataTable.getData("ChatId"));
        TelegramResponse<?> vResp = new SmeupMessageSender()
                    .sendRawText(vText, vFirstName, vLastName, vChatID);
        IoTConnectorResponse vRetResp = new IoTConnectorResponse();
        vRetResp.addData("Esito", vResp.isSuccessful()? "*OK": "*ERROR");
        vRetResp.addData("Description", vResp.getDescription());
        Integer vErroreCode = vResp.getErrorCode();
        vRetResp.addData("ErrorCode", vErroreCode != null
                    ? Integer.toString(vResp.getErrorCode()): "");
        Iterator<String> vKeyIter = aDataTable.getKeys().iterator();

        while(vKeyIter.hasNext())
        {
            String vKey = (String) vKeyIter.next();
            String vValue = aDataTable.getData(vKey);
            vRetResp.addData(vKey, vValue);
        }
        // TODO Auto-generated method stub
        return vRetResp;
    }

    @Override
    public SezInterface getSez()
    {
        // TODO Auto-generated method stub
        return iSez;
    }

    @Override
    public boolean unplug()
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean ping()
    {
        // TODO Auto-generated method stub
        TelegramResponse<?> vResp = new SmeupMessageSender()
                    .sendText("Test", "", "", -1);
        return vResp != null && vResp.isSuccessful();
    }

    public static void main(String[] args)
    {
        IoTConnectorConf vConf = new IoTConnectorConf();
//      String vBotSmeupToken= BotData.BOT_SMEUP_TOKEN;
//      String vBotUserName= BotData.BOT_SMEUP_USER_NAME;
        vConf.addData("BotToken", BotData.BOT_SMEUP_TOKEN);
        vConf.addData("BotUserName", BotData.BOT_SMEUP_USER_NAME);
        
        SmeupTelegramSpiPlugin vPlugin = new SmeupTelegramSpiPlugin();
        
        vPlugin.init(null, vConf);
        
        IoTConnectorInput vInput = new IoTConnectorInput();
        vInput.addData("Text", "Ciao " + System.currentTimeMillis());
        vInput.addData("FirstName", "Oliviero");
        vInput.addData("LastName", "Maestrelli");
        vInput.addData("ChatId", ""+SmeupMessageSender.ID_IO);
        IoTConnectorResponse vResp = vPlugin.invoke(vInput);
        if(vResp != null)
        {
            Enumeration<String> vKeyEnum = vResp.getKeys();
            while(vKeyEnum.hasMoreElements())
            {
                String vKey = (String) vKeyEnum.nextElement();
                String vValue = vResp.getData(vKey);

                System.out.println(vKey + ": " + vValue);
            }
        }
    }
}
