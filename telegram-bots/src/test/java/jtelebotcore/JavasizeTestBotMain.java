package jtelebotcore;

import Smeup.smeui.iotspi.connectors.telegram.SmeupMessageSender;
import Smeup.smeui.iotspi.connectors.telegram.command.SmeupCommandFactory;
import Smeup.smeui.iotspi.connectors.telegram.utility.SmeupConnectors;
import config.BotData;
import io.github.nixtabyte.telegram.jtelebot.client.impl.DefaultRequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.factory.TelegramRequestFactory;
import io.github.nixtabyte.telegram.jtelebot.response.json.ReplyKeyboardHide;
import io.github.nixtabyte.telegram.jtelebot.response.json.TelegramResponse;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandDispatcher;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandQueue;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandWatcher;
 
public class JavasizeTestBotMain {
    public static void main(String []args){
        System.out.println("Start");
        SmeupConnectors.init();
        new SmeupMessageSender().startDaemon(BotData.BOT_JAVASIZE_TEST_TOKEN);

        DefaultCommandDispatcher commandDispatcher = new DefaultCommandDispatcher(10,100, 100, new DefaultCommandQueue());
        commandDispatcher.startUp();
         
        DefaultCommandWatcher commandWatcher = new DefaultCommandWatcher(2000,100,BotData.BOT_JAVASIZE_TEST_TOKEN,commandDispatcher,new SmeupCommandFactory(BotData.BOT_JAVASIZE_TEST_USER_NAME));
//        DefaultCommandWatcher commandWatcher = new DefaultCommandWatcher(2000,100,BotData.BOT_TEST_TOKEN,commandDispatcher,new SmeupCommandFactory("srv-smens", 29900, BotData.BOT_TEST_USER_NAME));
        commandWatcher.startUp();
        

    }
}