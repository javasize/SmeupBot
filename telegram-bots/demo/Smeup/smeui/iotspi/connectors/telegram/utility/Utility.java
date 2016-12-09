package Smeup.smeui.iotspi.connectors.telegram.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

import Smeup.smeui.iotspi.connectors.telegram.command.SmeupCommand;
import Smeup.smeui.loa39.utility.A39Connection;
import Smeup.smeui.loa39.utility.UIXmlUtilities;

public class Utility
{

    public static boolean isFunctionEnabled(String aFun, String aFirstName, String aLastName, long aUserID)
    {
        boolean vRet= false;
        if(aFun.toUpperCase().startsWith("INDICI"))
        {
            vRet= (("Roberto".equalsIgnoreCase(aFirstName) && "Magni".equalsIgnoreCase(aLastName)  && 247836496==aUserID) 
                        || ("Silvano".equalsIgnoreCase(aFirstName) && "Lancini".equalsIgnoreCase(aLastName) && 266430152==aUserID) 
                        || ("Piero".equalsIgnoreCase(aFirstName) && "Gagliardo".equalsIgnoreCase(aLastName)  && 41720880==aUserID) 
                        || ("Oliviero".equalsIgnoreCase(aFirstName) && "Maestrelli".equalsIgnoreCase(aLastName) && 199971507==aUserID));
        }
        else
        {
            vRet= true;
        }
        return vRet;
    }

    public static Date getPreviousWorkingDay(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayOfWeek;
        do
        {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }
        while(dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);

        return cal.getTime();
    }

    public static boolean isActiveNotification(long aChatID, String aBotName)
    {
        boolean vFound = false;
        ArrayList<String[]> vList = getNotificationList(aBotName);
        for( Iterator vIterator = vList.iterator(); vIterator.hasNext()
                    && !vFound;)
        {
            String[] vStrings = (String[]) vIterator.next();
            vFound = (Long.parseLong(vStrings[2]) == aChatID);
        }
        return vFound;
    }

    public static ArrayList<String[]> getNotificationList(String aBotName)
    {
        ArrayList<String[]> vRetList = new ArrayList<>();
        String[][] vArr = getUserList(aBotName);
        if(vArr!=null)
        {
            if(vArr != null && vArr.length > 0)
            {
                for( int vI = 0; vI < vArr.length; vI++)
                {
                    String[] vStrings = vArr[vI];
                    if(vStrings.length > 2)
                    {
                        String vFirstName = vStrings[0];
                        String vLastName = vStrings.length > 1? vStrings[1]: "";
                        String vID = vStrings.length > 2? vStrings[2]: "";
                        String vText = vStrings.length > 3? vStrings[3]: "";
                        if(vID != null && !"".equalsIgnoreCase(vID.trim()))
                        {
                            vRetList.add(new String[] { vFirstName, vLastName, vID,
                                        vText });
                        }
                    }
                }
            }
        }
        return vRetList;
    }

    // public static boolean isToNotification(File aFile, boolean aCreate, long
    // aUserID,
    // long aChatID)
    // {
    // boolean vFound = false;
    // File vUsersNotifiedFile = aFile;
    // System.out.println("");
    // try
    // {
    // if(vUsersNotifiedFile.exists()
    // || vUsersNotifiedFile.createNewFile())
    // {
    // BufferedReader vReader = new BufferedReader(
    // new FileReader(vUsersNotifiedFile));
    // String vLine = vReader.readLine();
    // ArrayList<String[]> vUsersList = getNotificationList(vUsersNotifiedFile,
    // false);
    // Iterator<String[]> vIter = vUsersList
    // .iterator();
    //
    // while(vIter.hasNext() && !vFound)
    // {
    // String[] vEntry = (String[]) vIter.next();
    // if(vEntry.length > 3)
    // {
    // vFound = Long.toString(aUserID)
    // .trim()
    // .toUpperCase()
    // .equalsIgnoreCase(vEntry[2]
    // .trim()
    // .toUpperCase())
    // && Long.toString(aChatID)
    // .trim()
    // .toUpperCase()
    // .equalsIgnoreCase(vEntry[3]
    // .trim()
    // .toUpperCase());
    // }
    //
    // }
    // }
    // }
    // catch(FileNotFoundException ex)
    // {
    // // TODO Auto-generated catch block
    // ex.printStackTrace();
    // }
    // catch(IOException ex)
    // {
    // // TODO Auto-generated catch block
    // ex.printStackTrace();
    // }
    // return vFound;
    // }
    //
    // public static ArrayList<String[]> getNotificationList(File aFile, boolean
    // aCreate)
    // {
    // ArrayList<String[]> vRet= new ArrayList<>();
    // File vUsersNotifiedFile = aFile;
    // try
    // {
    // if(!vUsersNotifiedFile.exists() && aCreate)
    // {
    // vUsersNotifiedFile.createNewFile();
    // }
    //
    // if((vUsersNotifiedFile.exists()))
    // {
    // BufferedReader vReader = new BufferedReader(
    // new FileReader(vUsersNotifiedFile));
    // String vLine = vReader.readLine();
    // ArrayList<String[]> vUsersList = new ArrayList<>();
    //
    // while(vLine != null)
    // {
    // String[] vRowSplit = vLine.split(",");
    // vUsersList.add(vRowSplit);
    // vLine = vReader.readLine();
    // }
    // vReader.close();
    //
    // Iterator<String[]> vIter = vUsersList
    // .iterator();
    //
    // while(vIter.hasNext())
    // {
    // String[] vEntry = (String[]) vIter.next();
    // vRet.add(vEntry);
    //
    // }
    // }
    // }
    // catch(FileNotFoundException ex)
    // {
    // // TODO Auto-generated catch block
    // ex.printStackTrace();
    // }
    // catch(IOException ex)
    // {
    // // TODO Auto-generated catch block
    // ex.printStackTrace();
    // }
    // return vRet;
    // }

    static String[][] getUserList(String aBotName)
    {
        String[][] vRet = new String[][] {};
        String vFunToCall = SmeupCommand.FUN_AUTH_LIST;
        String vXmlResp=null;
        A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM.checkOut();
        if(vConn != null)
        {
            vXmlResp = vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>());
            SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
        }
        else
        {
            return null;
        }
        String vMailAddr = "";
        String vName = "";
        String vLastName = "";
        String vID = "";
        String vText = "";
        // String vCodCol = "";
        // String vDescCol= "";
        String vFilePath = "c:\\temp\\resp"
                    + System.currentTimeMillis() + ".xml";
        Document vDoc = UIXmlUtilities
                    .buildDocumentFromXmlString(vXmlResp);
        if(vDoc != null)
        {
            UIXmlUtilities.buildXmlFileFromDocument(vDoc,
                                                    vFilePath);
            Element vRoot = vDoc.getRootElement();
            Element vRigheEl = (vRoot != null
                        ? vRoot.element("Righe"): null);
            ArrayList<String[]> vNameList = new ArrayList<>();
            if(vRigheEl != null)
            {
                System.out.println("Carico utenti da lista collaboratori.");
                ArrayList<Element> vList = new ArrayList(
                            vRigheEl.elements("Riga"));
                Iterator<Element> vElIter = vList.iterator();
                boolean vIsFirst = true;
                while(vElIter.hasNext())
                {
                    if(vIsFirst)
                    {
                        System.out.println("Trovato collaboratori nel sistema.");
                        vIsFirst = false;
                    }
                    Element vElement = (Element) vElIter.next();
                    String vFld = vElement.attributeValue("Fld",
                                                          "");
                    if(vFld.indexOf("|") > -1)
                    {
                        String[] vSplit = vFld.split("\\|");

                        vName = (vSplit.length > 0
                                    ? vSplit[0]: "");
                        vLastName = (vSplit.length > 1
                                    ? vSplit[1]: "");
                        vID = (vSplit.length > 2
                                    ? vSplit[2]: "");
                        vText = (vSplit.length > 3
                                    ? vSplit[3]: "");
                        vNameList.add(new String[] { vName, vLastName, vID,
                                    vText });
                    }
                }
                vRet = vNameList.toArray(new String[vNameList.size()][2]);
                System.out.println(Arrays.toString(vRet));
            }
        }

        if(vRet == null || vRet.length == 0)
        {
            File vUsersFile = new File(aBotName + "_users.txt");
            if(aBotName == null || "".equalsIgnoreCase(aBotName.trim())
                        || !vUsersFile.exists())
            {
                vUsersFile = new File("users.txt");
            }

            System.out.println("Testo presenza di "
                        + vUsersFile.getAbsolutePath());
            if(vUsersFile.exists() && vUsersFile.length() > 0)
            {
                try
                {
                    BufferedReader vReader = new BufferedReader(
                                new FileReader(vUsersFile));
                    String vLine = vReader.readLine();
                    ArrayList<String[]> vList = new ArrayList<>();
                    while(vLine != null)
                    {
                        if(!vLine.startsWith("#"))
                        {
                            String[] vUserEntry = new String[2];
                            int vCommaIndex = vLine.indexOf(",");
                            if(vCommaIndex > -1)
                            {
                                vUserEntry[0] = vLine.substring(0, vCommaIndex)
                                            .trim();
                                vUserEntry[1] = vLine.substring(vCommaIndex + 1)
                                            .trim();
                            }
                            else
                            {
                                vUserEntry[0] = vLine;
                            }
                            vList.add(vUserEntry);
                        }
                        vLine = vReader.readLine();
                    }
                    vReader.close();
                    vRet = vList.toArray(new String[vList.size()][2]);
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
            }
            else
            {
                System.out.println("Nessun file di autorizzazioni, procedo con i default");
            }

        }

        return vRet;
    }

    public static boolean isEnablesUserName(String aFirtsName, String aLastName,
                String aBotName)
    {
        boolean vRetFound = false;
        if(aFirtsName != null && aLastName != null)
        {
            String[][] vUserList = getUserList(aBotName);
            if(vUserList!=null)
            {
                for( int vI = 0; vI < vUserList.length && !vRetFound; vI++)
                {
                    String[] vStrings = vUserList[vI];
                    if(vStrings != null && vStrings.length > 1)
                    {
                        vRetFound = (aFirtsName.equalsIgnoreCase(vStrings[0])
                                    && aLastName.equalsIgnoreCase(vStrings[1]));
                        if(!vRetFound)
                            vRetFound = (aFirtsName.equalsIgnoreCase(vStrings[0])
                                        && aLastName.equalsIgnoreCase(vStrings[1]));
                    }
                }
            }
            else
            {
                vRetFound= false;
            }
        }
        return vRetFound;
    }

    
    public static void main(String[] args)
    {
        System.out.println(Utility.getPreviousWorkingDay(new Date()).toString());
    }
    
    public static String stringToHTMLString(String string, boolean aSubSpaces)
    {
        return stringToHTMLString(string, aSubSpaces, true);

    }

    public static String stringToHTMLString(String string, boolean aSubSpaces,
                boolean aManageNewLine)
    {
        return stringToHTMLString(string, aSubSpaces, aManageNewLine, false);
    }

    public static String stringToHTMLString(String string, boolean aSubSpaces,
                boolean aManageNewLine, boolean aPreserveLtGt)
    {
        StringBuffer sb = new StringBuffer(string.length());

        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for( int i = 0; i < len; i++)
        {
            c = string.charAt(i);

            if(c == ' ')
            {
                if(aSubSpaces)
                {
                    // blank gets extra work,
                    // this solves the problem you get if you replace all
                    // blanks with &nbsp;, if you do that you loss
                    // word breaking
                    if(lastWasBlankChar)
                    {
                        lastWasBlankChar = false;
                        sb.append("&nbsp;");
                    }
                    else
                    {
                        lastWasBlankChar = true;
                        sb.append(' ');
                    }
                }
                else
                {
                    sb.append(' ');
                }
            }
            else
            {
                lastWasBlankChar = false;

                //
                // HTML Special Chars
                if(c == '"')
                {
                    sb.append("&quot;");
                }
                else if(c == '\'')
                {
                    sb.append("&#39;");
                    // sb.append("&apos;");
                }
                else if(c == '&')
                {
                    sb.append("&amp;");
                }
                else if(c == '€' || (0xffff & c) == 164)
                {
                    sb.append("&euro;");
                }
                else if(c == '<')
                {
                    if(aPreserveLtGt)
                        sb.append(c);
                    else
                        sb.append("&lt;");
                }
                else if(c == '>')
                {
                    if(aPreserveLtGt)
                        sb.append(c);
                    else
                        sb.append("&gt;");
                }
                else if(c == '\n')
                {
                    // Handle Newline

                    if(aManageNewLine)
                    {
                        if(aPreserveLtGt)
                            sb.append("<br/>");
                        else
                            sb.append("&lt;br/&gt;");
                    }
                    else
                    {
                        sb.append(c);
                    }
                }
                else
                {
                    int ci = 0xffff & c;

                    if(ci < 160)
                    {
                        // nothing special only 7 Bit
                        sb.append(c);
                    }
                    else
                    {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(new Integer(ci).toString());
                        sb.append(';');
                    }
                }
            }
        }

        return sb.toString();
    }
    
    
    
}
