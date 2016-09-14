package jtelebotcore.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Utility
{
    public static boolean isToNotification(File aFile, boolean aCreate, long aUserID,
                long aChatID)
    {
        boolean vFound = false;
        File vUsersNotifiedFile = aFile;
        System.out.println("");
        try
        {
            if(vUsersNotifiedFile.exists()
                        || vUsersNotifiedFile.createNewFile())
            {
                BufferedReader vReader = new BufferedReader(
                            new FileReader(vUsersNotifiedFile));
                String vLine = vReader.readLine();
                ArrayList<String[]> vUsersList = getNotificationList(vUsersNotifiedFile, false);
                Iterator<String[]> vIter = vUsersList
                            .iterator();

                while(vIter.hasNext() && !vFound)
                {
                    String[] vEntry = (String[]) vIter.next();
                    if(vEntry.length > 3)
                    {
                        vFound = Long.toString(aUserID)
                                    .trim()
                                    .toUpperCase()
                                    .equalsIgnoreCase(vEntry[2]
                                                .trim()
                                                .toUpperCase())
                                    && Long.toString(aChatID)
                                                .trim()
                                                .toUpperCase()
                                                .equalsIgnoreCase(vEntry[3]
                                                            .trim()
                                                            .toUpperCase());
                    }

                }
            }
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
        return vFound;
    }

    public static ArrayList<String[]> getNotificationList(File aFile, boolean aCreate)
    {
        ArrayList<String[]> vRet= new ArrayList<>();
        File vUsersNotifiedFile = aFile;
        try
        {
            if(!vUsersNotifiedFile.exists() && aCreate)
            {
                vUsersNotifiedFile.createNewFile();
            }
            
            if((vUsersNotifiedFile.exists()))
            {
                BufferedReader vReader = new BufferedReader(
                            new FileReader(vUsersNotifiedFile));
                String vLine = vReader.readLine();
                ArrayList<String[]> vUsersList = new ArrayList<>();

                while(vLine != null)
                {
                    String[] vRowSplit = vLine.split(",");
                    vUsersList.add(vRowSplit);
                    vLine = vReader.readLine();
                }
                vReader.close();

                Iterator<String[]> vIter = vUsersList
                            .iterator();

                while(vIter.hasNext())
                {
                    String[] vEntry = (String[]) vIter.next();
                    vRet.add(vEntry);

                }
            }
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
        return vRet;
    }
}
