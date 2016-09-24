package jtelebotcore.main;

import config.BotData;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandDispatcher;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandQueue;
import io.github.nixtabyte.telegram.jtelebot.server.impl.DefaultCommandWatcher;
 
public class SmeupBotMain {
 
    public static void main(String []args){
        SmeupConnectors.init();
        
//        new SmeupMessageSender().init();

        DefaultCommandDispatcher vJavasizeCommandDispatcher = new DefaultCommandDispatcher(10,100, 100, new DefaultCommandQueue());
        vJavasizeCommandDispatcher.startUp();
         
        DefaultCommandWatcher vJavasizeCommandWatcher = new DefaultCommandWatcher(2000,100,BotData.BOT_JAVASIZE_TOKEN,vJavasizeCommandDispatcher,new JavasizeCommandFactory());
        vJavasizeCommandWatcher.startUp();

        DefaultCommandDispatcher vSmeupCommandDispatcher = new DefaultCommandDispatcher(10,100, 100, new DefaultCommandQueue());
        vSmeupCommandDispatcher.startUp();
         
        DefaultCommandWatcher vSmeupCommandWatcher = new DefaultCommandWatcher(2000,100,BotData.BOT_SMEUP_TOKEN,vSmeupCommandDispatcher,new SmeupCommandFactory(BotData.BOT_SMEUP_USER_NAME));
        vSmeupCommandWatcher.startUp();
    }
}