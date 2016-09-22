package jtelebotcore.main;

import config.BotData;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandDispatcher;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandQueue;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandWatcher;
 
public class JavasizeBotMain {
 
    public static void main(String []args){
        SmeupConnectors.init();
        new SmeupMessageSender().init();
        DefaultCommandDispatcher commandDispatcher = new DefaultCommandDispatcher(10,100, 100, new DefaultCommandQueue());
        commandDispatcher.startUp();
         
        DefaultCommandWatcher commandWatcher = new DefaultCommandWatcher(2000,100,BotData.BOT_JAVASIZE_TOKEN,commandDispatcher,new JavasizeCommandFactory());
//        DefaultCommandWatcher commandWatcher = new DefaultCommandWatcher(2000,100,BotData.BOT_TOKEN,commandDispatcher,new SmeupCommandFactory("srv-smens", 29900, BotData.BOT_USER_NAME));
        commandWatcher.startUp();
        
    }
}