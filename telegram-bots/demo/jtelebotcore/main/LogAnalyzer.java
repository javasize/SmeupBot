package jtelebotcore.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LogAnalyzer
{

    public LogAnalyzer()
    {
    }

    public static HashMap<String, ArrayList<String>> getLogIn(File aDir)
    {
        HashMap<String, ArrayList<String>> vRet= new HashMap<>();
        File vDir= new File(".");
        if(aDir!=null)
        {
            vDir= aDir;
        }
        ArrayList<String> vFileContent= new ArrayList<>();
        File[] vLogFileArr= vDir.listFiles(new FilenameFilter()
        {
            
            @Override
            public boolean accept(File aDir, String aName)
            {
                
                return aName!=null && aName.toLowerCase().startsWith("wrapper") && aName.toLowerCase().endsWith(".log");
            }
        });
        
        
        for( int vI = 0; vI < vLogFileArr.length; vI++)
        {
            File vFile= vLogFileArr[vI];
            
            BufferedReader vReader;
            try
            {
                vReader = new BufferedReader(new FileReader(vFile));
                
                String vLine= vReader.readLine();
                String vSearchString= "@Smeup_bot: Messaggio da"; 
                String vSubStringMarker= " | 201";
                while(vLine!=null)
                {
                    if(vLine.contains(vSearchString) && vLine.contains(vSubStringMarker))
                    {
                        int vIndex= vLine.indexOf(vSubStringMarker);
                        
                        String vChat_id_line= vReader.readLine();
                        vLine= vLine.substring(vIndex+2);
                        String vChatIdIdentifier= "chat_id: ";
                        if(vChat_id_line!=null && vChat_id_line.contains(vChatIdIdentifier))
                        {
                            vLine= vLine.concat(" ***** ").concat(vChat_id_line.substring(vChat_id_line.indexOf(vChatIdIdentifier)));
                        }
                        vFileContent.add(vLine.concat("\r\n"));
                    }
                    vLine= vReader.readLine();
                }
                vReader.close();
            }
            catch(FileNotFoundException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            catch(IOException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
            vRet.put(vFile.getAbsolutePath(), vFileContent);
        }
        
        return vRet;
    }
    
    public static void geterateLogReport(String aFileName, ArrayList<String> aContent, File aReportFile) throws IOException
    {
        
        FileWriter vWriter= new FileWriter(aReportFile, true);
        aContent.sort(null);
        vWriter.write("\r\n//////////////////////////////********************************************************\r\n"+aFileName+"\r\n");
//        Iterator<String> vRowIter= aContent.iterator();
        for(int i=0; i<aContent.size();i++)
        {
            String vRow= (String) aContent.get(i);
            
            vWriter.write(vRow);
        }
        vWriter.flush();
        vWriter.close();
    }
    
    
    public static File produceReport(File aDir)
    {
        File vOut= new File(aDir, "log_output.txt");
        if(vOut.exists())
        {
            vOut.delete();
            try
            {
                vOut.createNewFile();
            }
            catch(IOException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }

        try
        {
            HashMap<String, ArrayList<String>> vMap= getLogIn(aDir);
            Iterator<String> vFileNameIter= vMap.keySet().iterator();
            
            ArrayList<String> vKeyList= new ArrayList<>(vMap.keySet());
            vKeyList.sort(null);
            for( int vI = 0; vI < vKeyList.size(); vI++)
            {
                String vName= vKeyList.get(vI);
                ArrayList<String> vList= vMap.get(vName);
                geterateLogReport(vName, vList, vOut);
                
            }
        }
        catch(IOException ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        return vOut;
    }
    
    public static void main(String[] args)
    {
        LogAnalyzer.produceReport(new File("C:\\Temp\\Bot_log"));
    }
}
