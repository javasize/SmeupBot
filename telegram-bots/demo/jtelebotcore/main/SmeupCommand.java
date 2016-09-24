package jtelebotcore.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.NumberFormat;
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
import Smeup.smeui.uiutilities.UIFunctionDecoder;
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

public class SmeupCommand extends AbstractCommand
{

    // Long ENABLED_ID = new Long(199971507);
    // Long ENABLED_ID_2 = new Long(219217733);
    // static String[][] LOADED_ENABLED_USER= new String[][] {};

    String[][] DEFAULT_ENABLED_USER = new String[][] {
                { "Oliviero", "Maestrelli" },
                { "Dario", "Foresti" }, { "Silvano", "Lancini" },
                { "Piero", "Gagliardo" }, { "Roberto", "Magni" },
                { "Stefano", "Lancini" }, { "Costantino", "Sanfilippo" },
                { "Stefano", "Arrighini" }, { "Giovanni", "Del Bono" }
    };

    static final String FUN_SENT = "F(EXB;X1SER_32;ESE.MAT) 1(CM;;SMEHDLAB) 2(;;) INPUT(DT("
                + "{0}" + "01" + ") PER(" + "{1}" + ") CF(C) CodVer(No))";

    static final String FUN_AGE = "F(EXB;LOA10_SE;ELE) 1(LI;CNCOL;*) 2(;;) INPUT(Sch() WHR( E§LIVE <= '8') ORDER(E§CRAG) NCf(1) Context() SchPar() NTit(1) Qry(Yes) RPa(800,00000))";
    static final String FUN_AGE_COL = "F(EXB;X1SER_32;ESE.MAT) 1(CN;COL;"
                + "{0}" + ") INPUT(NO(" + "{1}" + ") TV(1) DT(" + "{2}"
                + ") CF(P) CodVer(No))";
    static final String FUN_CLI = "F(EXB;LOA10_SE;ELE) 1(LI;CNCLI;*) 2(;;) INPUT(Sch() NCf(1) Context() SchPar() NTit(1) Qry(Yes) RPa())";
    static final String FUN_CLI_ADDR = "F(EXB;X1BASE_03;DAT) 1(CN;CLI;" + "{0}"
                + ") 2(;;) P(Hlp(Yes) Com(Yes))";
    static final String FUN_CLI_DAT = "F(EXB;X1BASE_03;DAT) 1(CN;CLI;" + "{0}"
                + ") 2(;;) P(Hlp(Yes) Com(Yes))";
    static final String FUN_CLI_CONT = "F(EXB;BRK9CN;CRU) 1(CN;CLI;" + "{0}"
                + ")";
    static final String FUN_CLI_FISC = "F(EXB;BRK9CN;CRU) 1(CN;CLI;" + "{0}"
                + ")";
    static final String FUN_AGE_COM = "F(EXB;X1SER_32;ESE.MAT) 1(;;) 2(;;) INPUT(NO() DT("
                + "{0}" + ") CF(D) CodVer(No))";

    static final String FUN_AUTH_LIST = "F(EXB;LOA13_SE;ESE.SQL) 1(;;) 2(;;) INPUT(SELECT * FROM X1TLGM0F)";

    static final String FUN_AGE_DAY = "F(EXB;X1SER_32;ESE.MAT) 1(;;) 2(;;) INPUT(NO() DT({0}) CF(D) CodVer(No))";

    static final String FUN_IND_DAY = "F(EXB;X1AGEN_01;ANA.SIT) 1(D8;*YYMD;{0}) P(PER())";
    
    // static final String FUN_AUTH_LIST = "F(EXB;LOA10_SE;ELE) 1(LI;CNCOL;*)
    // 2(;;) INPUT(Sch(Q/RU) WHR(E§LIVE <= '8' AND E§STAT='10') ORDER(E§CRAG)
    // NCf(1) Context() SchPar() NTit(1) Qry(Yes) RPa(800,00000))";

    // static
    // {
    // File vUsersFile= new File(BotData."users.txt");
    // vUsersFile= new File("users.txt");
    // System.out.println("Testo presenza di "+vUsersFile.getAbsolutePath());
    // if(vUsersFile.exists() && vUsersFile.length()>0)
    // {
    // try
    // {
    // BufferedReader vReader= new BufferedReader(new FileReader(vUsersFile));
    // String vLine= vReader.readLine();
    // ArrayList<String[]> vList= new ArrayList<>();
    // while(vLine!=null)
    // {
    // String[] vUserEntry= new String[2];
    // int vCommaIndex= vLine.indexOf(",");
    // if(vCommaIndex>-1)
    // {
    // vUserEntry[0]= vLine.substring(0, vCommaIndex).trim();
    // vUserEntry[1]= vLine.substring(vCommaIndex+1).trim();
    // }
    // else
    // {
    // vUserEntry[0]= vLine;
    // }
    // vList.add(vUserEntry);
    // vLine= vReader.readLine();
    // }
    // vReader.close();
    // ENABLED_USER= vList.toArray(new String[vList.size()][2]);
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
    // }
    // }

    String iBotName = "";
    Long MAX_TEXT_LENGTH = new Long(4096);

    public static File iUsersNotifiedFile = null;

    // String iProviderAddress = null;
    // int iProviderPort = 9090;
    //
    public SmeupCommand(Message message, RequestHandler requestHandler,
                String aBotName)
    {
        super(message, requestHandler);
        iBotName = aBotName;
        iUsersNotifiedFile = new File(
                                           iBotName + "_notify.txt");

        // iProviderAddress = aProviderAddress;
        // iProviderPort = aProviderPort;
        // String[][] vLoadedEnableUser= getUserList();
        //
        // }
        // else
        // {
        // ENABLED_USER = vLoadedEnableUser;
        // }

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

    MessageType getMessageType(Message aMessage)
    {
        MessageType vRet = MessageType.UNHANDLED_MESSAGE;

        if(message.getText() != null)
        {
            vRet = MessageType.TEXT_MESSAGE;
        }
        else if(message.getAudio() != null)
        {
            vRet = MessageType.AUDIO_MESSAGE;
        }
        else if(message.getDocument() != null)
        {
            vRet = MessageType.DOCUMENT_MESSAGE;
        }
        else if(message.getPhoto() != null)
        {
            vRet = MessageType.IMAGE_MESSAGE;
        }
        else if(message.getLocation() != null)
        {
            vRet = MessageType.LOCATION_MESSAGE;
        }
        else if(message.getContact() != null)
        {
            vRet = MessageType.CONTACT_MESSAGE;

        }
        else if(message.getSticker() != null)
        {
            vRet = MessageType.STICKER_MESSAGE;

        }
        else if(message.getVideo() != null)
        {
            vRet = MessageType.VIDEO_MESSAGE;

        }
        return vRet;
    }
    
    
    public static SmeupResponseData createSmeupResponse(String aFun, String aFirstName, String aLastName, long aUserID, long aChatID, boolean aIsNotification, CustomReplyKeyboard aDefaultKeyboardMarkup)
    {
        CustomReplyKeyboard vKeyboardMarkup= aDefaultKeyboardMarkup;
        String vFun= aFun;
        String vFirstName= aFirstName;
        String vLastName= aLastName;
        long vUserID= aUserID;
        long vChatID= aChatID;
        String vTempDir = ".\\temp";
        new File(vTempDir).mkdirs();
        
        String vRespMsg= "";
        
        if(("CIAO").equalsIgnoreCase(vFun)
                    || ("START").equalsIgnoreCase(vFun))
        {
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Sei autorizzato a dialogare con Sme.UP.";
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
            vKeyboardMarkup = new CiaoReplyKeyboardMarkup();

        }
        else if(vFun.toUpperCase().startsWith("SENTINELLE"))
        {
            String vDateNow = new SimpleDateFormat("yyyyMMdd")
                        .format(Calendar.getInstance().getTime());
            String vDateNowYear = new SimpleDateFormat("yyyy")
                        .format(Calendar.getInstance().getTime());
            String vDateNowYearMonth = new SimpleDateFormat("yyyyMM")
                        .format(Calendar.getInstance().getTime());
            String vDateNowMonth = new SimpleDateFormat("MM")
                        .format(Calendar.getInstance().getTime());

            String vResp = "";
            String vCodStart = "";
            if(vFun.length() > "SENTINELLE".length())
            {
                vCodStart = vFun.substring(("SENTINELLE").length(),
                                           ("SENTINELLE").length() + 1);
            }
            String vFunToCall = MessageFormat.format(FUN_SENT,
                                                     vDateNowYearMonth
                                                                 + "01",
                                                     vDateNowYearMonth);
            String vXmlResp;
            A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
            }
            String vDay = "";
            String vDate = "";
            String vCodCol = "";
            String vHour = "";
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext())
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            vDay = (vSplit.length > 6
                                        ? vSplit[6]: "");
                            vDate = (vSplit.length > 8
                                        ? vSplit[8]: "");
                            vCodCol = (vSplit.length > 10
                                        ? vSplit[10]: "");
                            vHour = (vSplit.length > 25
                                        ? vSplit[25]: "");
                            try
                            {
                                Date vDateObject = new SimpleDateFormat(
                                            "yyyyMMdd").parse(vDate);
                                vDate = new SimpleDateFormat(
                                            "dd/MM/yyyy")
                                                        .format(vDateObject);
                            }
                            catch(ParseException ex)
                            {
                                // TODO Auto-generated catch block
                                ex.printStackTrace();
                            }
                            if(vCodCol != null
                                        && !"".equalsIgnoreCase(vCodCol
                                                    .trim()))
                            {
                                String vCodListStart = vCodStart
                                            .length() > 0? vCodStart
                                                        .substring(0, 1)
                                                        .toUpperCase()
                                                        : "";
                                String vCodColStart = vCodCol
                                            .substring(0, 1)
                                            .toUpperCase();
                                if("".equalsIgnoreCase(vCodListStart
                                            .trim())
                                            || vCodColStart
                                                        .compareTo(vCodListStart) >= 0)
                                {
                                    String vA39Row = vDay + ",\t"
                                                + vDate + "\t" + "<b>"
                                                + vCodCol + "</b>"
                                                + "\t" + vHour;
                                    // String vA39Row =
                                    // "/AGENDA_"+vCodCol;
                                    if(vResp.length() + "\r\n"
                                                .concat(vA39Row)
                                                .length() <= 4096)
                                    {
                                        vResp += "\r\n".concat(vA39Row);
                                    }
                                    else
                                    {
                                        break;
                                    }
                                }
                                else
                                {
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + (aIsNotification? ". Ti aggiorno sulle sentinelle del mese: \r\n": ". Ecco l'elenco delle sentinella del mese: \r\n")
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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
        }
        else if(vFun.toUpperCase().startsWith("NOTIFICHE"))
        {
            String vAction = "";
            if(vFun.length() > "NOTIFICHE ".length())
            {
                vAction = vFun.substring(("NOTIFICHE ").length());
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName;

            if(vAction!=null && vAction.toUpperCase().contains("DISABILITA"))
            {
                File vUsersNotifiedFile =iUsersNotifiedFile;
                try
                {
                    if(vUsersNotifiedFile.exists())
                    {
                        BufferedReader vReader = new BufferedReader(
                                    new FileReader(vUsersNotifiedFile));
                        String vLine = vReader.readLine();
                        ArrayList<String[]> vUsersList = new ArrayList<>();

                        boolean vFound = false;
                        while(vLine != null)
                        {
                            String[] vRowSplit = vLine.split(",");
                            vUsersList.add(vRowSplit);
                            vLine = vReader.readLine();
                        }
                        vReader.close();
                        ArrayList<String[]> vCopiedUserList= new ArrayList<>(vUsersList);
                        Iterator<String[]> vIter = vUsersList
                                    .iterator();
                        int vIndex= 0;
                        while(vIter.hasNext() && !vFound)
                        {
                            String[] vEntry = (String[]) vIter.next();
                            if(vEntry.length > 3)
                            {
                                vFound = Long.toString(vUserID)
                                            .trim()
                                            .toUpperCase()
                                            .equalsIgnoreCase(vEntry[2]
                                                        .trim()
                                                        .toUpperCase()) && Long.toString(vChatID)
                                            .trim()
                                            .toUpperCase()
                                            .equalsIgnoreCase(vEntry[3]
                                                        .trim()
                                                        .toUpperCase());
                                if(vFound)
                                {
                                    vCopiedUserList.remove(vIndex);
                                }
                            }
                            vIndex+=1;
                        }

                        if(vFound)
                        {
                            try
                            {
                                Iterator<String[]> vCopiedIter= vCopiedUserList.iterator();
                                FileWriter vWriter= new FileWriter(vUsersNotifiedFile, false);
                                while(vCopiedIter.hasNext())
                                {
                                    String[] vEntry = (String[]) vCopiedIter
                                                .next();
                                    if(vEntry.length>2)
                                    {
                                        String vRow = vEntry[0] + ", "
                                                    + vEntry[1] + ", "
                                                    + vEntry[2] + ", "
                                                    + vEntry[3]  + "\r\n";
                                        vWriter.write(vRow);
                                    }
                                }
                                vWriter.flush();
                                vWriter.close();
                                vRespText += ", hai disabilitato la funzione di notifica di Smeup";
                                ((StartReplyKeyboardMarkup)vKeyboardMarkup).setKeyboard(new String[][] {{"Start"}, {"Notifiche Abilita (SVI)"}});
                            }
                            catch(IOException e)
                            {
                                e.printStackTrace();
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
                vRespText += ", hai disabilitato la funzione di notifica di Smeup";
            }
            else if(vAction!=null && vAction.toUpperCase().contains("ABILITA"))
            {
                try
                {
                    if(iUsersNotifiedFile.exists()
                                || iUsersNotifiedFile.createNewFile())
                    {
                        BufferedReader vReader = new BufferedReader(
                                    new FileReader(iUsersNotifiedFile));
                        String vLine = vReader.readLine();
                        ArrayList<String[]> vUsersList = new ArrayList<>();

                        boolean vFound = false;
                        while(vLine != null)
                        {
                            String[] vRowSplit = vLine.split(",");
                            vUsersList.add(vRowSplit);
                            vLine = vReader.readLine();
                        }
                        vReader.close();

                        Iterator<String[]> vIter = vUsersList
                                    .iterator();

                        while(vIter.hasNext() && !vFound)
                        {
                            String[] vEntry = (String[]) vIter.next();
                            if(vEntry.length > 3)
                            {
                                vFound = Long.toString(vUserID)
                                            .trim()
                                            .toUpperCase()
                                            .equalsIgnoreCase(vEntry[2]
                                                        .trim()
                                                        .toUpperCase()) && Long.toString(vChatID)
                                            .trim()
                                            .toUpperCase()
                                            .equalsIgnoreCase(vEntry[3]
                                                        .trim()
                                                        .toUpperCase());
                            }

                        }

                        if(!vFound)
                        {
                            try
                            {
                                String vRow = vFirstName + ", "
                                            + vLastName + ", "
                                            + vUserID + ", "
                                            + vChatID + "\r\n";
                                Files.write(Paths.get(iUsersNotifiedFile
                                            .toURI()), vRow.getBytes(),
                                            StandardOpenOption.APPEND);
                                vRespText += ", hai abilitato la funzione di notifica di Smeup";
                                ((StartReplyKeyboardMarkup)vKeyboardMarkup).setKeyboard(new String[][] {{"Start"}, {"Notifiche Disabilita (SVI)"}});
                            }
                            catch(IOException e)
                            {
                                e.printStackTrace();
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

            }

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
        }
        else if(vFun.toUpperCase().startsWith("AGENDE"))
        {
            String vResp = "";
            String vCodStart = "";
            if(vFun.length() > "AGENDE ".length())
            {
                vCodStart = vFun.substring(("AGENDE ").length(),
                                           ("AGENDE ").length() + 1);
            }
            String vFunToCall = FUN_AGE;
            String vXmlResp;
            A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
            }
            String vCodCol = "";
            String vDescCol = "";
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext())
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            vCodCol = (vSplit.length > 5
                                        ? vSplit[5]: "");
                            vDescCol = (vSplit.length > 6
                                        ? vSplit[6]: "");
                            if(vCodCol != null
                                        && !"".equalsIgnoreCase(vCodCol
                                                    .trim()))
                            {
                                String vCodListStart = vCodStart
                                            .length() > 0? vCodStart
                                                        .substring(0, 1)
                                                        .toUpperCase()
                                                        : "";
                                String vCodColStart = vCodCol
                                            .substring(0, 1)
                                            .toUpperCase();
                                if("".equalsIgnoreCase(vCodListStart
                                            .trim())
                                            || vCodColStart
                                                        .compareTo(vCodListStart) >= 0)
                                {
                                    String vA39Row = "<b>"
                                                + vDescCol + "</b>"
                                                + "\t" + "/AGENDA_"
                                                + vCodCol;
                                    // String vA39Row =
                                    // "/AGENDA_"+vCodCol;
                                    if(vResp.length() + "\r\n"
                                                .concat(vA39Row)
                                                .length() <= 4096)
                                    {
                                        vResp += "\r\n".concat(vA39Row);
                                    }
                                    else
                                    {
                                        break;
                                    }
                                }
                                else
                                {
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco l'elenco collaboratori: \r\n"
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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

            if(!aIsNotification)
            {
                vKeyboardMarkup = new AgendeReplyKeyboardMarkup();
            }
        }
        else if(vFun.toUpperCase().startsWith("AGENDA ")
                    || vFun.toUpperCase().startsWith("AGENDA_"))
        {
            String vResp = "";
            String vDateNow = new SimpleDateFormat("yyyyMMdd")
                        .format(Calendar.getInstance().getTime());
            String vCodiceAgenda = vFun.substring(("AGENDA ").length());
            if(vCodiceAgenda != null)
            {
                vCodiceAgenda = vCodiceAgenda.toUpperCase();
            }

            if("OGGI".equalsIgnoreCase(vCodiceAgenda))
            {
                String vFunToCall = MessageFormat.format(FUN_AGE_DAY,
                                                         vDateNow);
                String vXmlResp;
                A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM
                            .checkOut();
                vXmlResp = vConn != null
                            ? vConn.executeFun(vFunToCall,
                                               new HashMap<String, String>())
                            : UIXmlProvider.readXml(UIFunInputStructure
                                        .getFunInputStructure(vFunToCall));
                if(vConn != null)
                {
                    SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
                }
                String vName = "";
                String vDescCommessa = "";
                String vOre = "";
                String vFilePath = vTempDir + "\\resp"
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
                    if(vRigheEl != null)
                    {
                        ArrayList<Element> vList = new ArrayList(
                                    vRigheEl.elements("Riga"));
                        Iterator<Element> vElIter = vList.iterator();
                        while(vElIter.hasNext())
                        {
                            Element vElement = (Element) vElIter.next();
                            String vFld = vElement.attributeValue("Fld",
                                                                  "");
                            if(vFld.indexOf("|") > -1)
                            {
                                String[] vSplit = vFld.split("\\|");
                                vName = (vSplit.length > 10
                                            ? vSplit[10]: "");
                                vDescCommessa = (vSplit.length > 16
                                            ? vSplit[16]: "");
                                vOre = (vSplit.length > 25
                                            ? vSplit[25]: "");
                                String vA39Row = "<b>"
                                            + vName + "</b>"
                                            + "\t" + vDescCommessa
                                            + "\t" + vOre;
                                vResp += "\r\n".concat(vA39Row);
                            }
                        }
                    }
                }
            }
            else
            {
                String vFunToCall = MessageFormat.format(FUN_AGE_COL,
                                                         vCodiceAgenda,
                                                         vCodiceAgenda,
                                                         vDateNow);
                String vXmlResp;
                A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM
                            .checkOut();
                vXmlResp = vConn != null
                            ? vConn.executeFun(vFunToCall,
                                               new HashMap<String, String>())
                            : UIXmlProvider.readXml(UIFunInputStructure
                                        .getFunInputStructure(vFunToCall));
                if(vConn != null)
                {
                    SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
                }
                String vDate = "";
                String vDescCommessa = "";
                String vOre = "";
                String vFilePath = vTempDir + "\\resp"
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
                    if(vRigheEl != null)
                    {
                        ArrayList<Element> vList = new ArrayList(
                                    vRigheEl.elements("Riga"));
                        Iterator<Element> vElIter = vList.iterator();
                        while(vElIter.hasNext())
                        {
                            Element vElement = (Element) vElIter.next();
                            String vFld = vElement.attributeValue("Fld",
                                                                  "");
                            if(vFld.indexOf("|") > -1)
                            {
                                String[] vSplit = vFld.split("\\|");
                                vDate = (vSplit.length > 8
                                            ? vSplit[8]: "");
                                vDate = vDate.length() >= 8
                                            ? vDate.substring(6): vDate;
                                vDescCommessa = (vSplit.length > 16
                                            ? vSplit[16]: "");
                                vOre = (vSplit.length > 25
                                            ? vSplit[25]: "");
                                String vA39Row = "<b>"
                                            + vDate + "</b>"
                                            + "\t" + vDescCommessa
                                            + "\t" + vOre;
                                vResp += "\r\n".concat(vA39Row);
                            }
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + (aIsNotification? ". Ti aggiorno sull'agenda di " + vCodiceAgenda: ". Ecco l'agenda di " + vCodiceAgenda)
                        + ": \r\n"
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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

        }
        else if(vFun.toUpperCase().startsWith("COMMESSE"))
        {
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco le interrogazioni per commessa.";
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

            if(!aIsNotification)
            {
                vKeyboardMarkup = new CommesseReplyKeyboardMarkup();
            }
        }
        else if(vFun.toUpperCase().startsWith("COMMESSA ")
                    || vFun.toUpperCase().startsWith("COMMESSA_"))
        {
            String vResp = "";
            String vDateNow = new SimpleDateFormat("yyyyMMdd")
                        .format(Calendar.getInstance().getTime());
            String vDateNowYear = new SimpleDateFormat("yyyy")
                        .format(Calendar.getInstance().getTime());
            String vDateNowMonth = new SimpleDateFormat("MM")
                        .format(Calendar.getInstance().getTime());
            String vDateNowDay = new SimpleDateFormat("dd")
                        .format(Calendar.getInstance().getTime());
            String[] vCodComArr = vFun.substring(("COMMESSA ").length())
                        .trim().split("\\|");

            String vFunToCall = MessageFormat.format(FUN_AGE_COM,
                                                     vDateNow);

            String vXmlResp;
            A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
            }
            String vCodCol = "";
            String vCodCommessa = "";
            String vDescCommessa = "";
            String vOre = "";
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext())
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            vCodCol = (vSplit.length > 10
                                        ? vSplit[10]: "");
                            vCodCommessa = (vSplit.length > 14
                                        ? vSplit[14]: "");
                            vDescCommessa = (vSplit.length > 16
                                        ? vSplit[16]: "");
                            vOre = (vSplit.length > 25
                                        ? vSplit[25]: "");
                            for( int vI = 0; vI < vCodComArr.length; vI++)
                            {
                                String vString = vCodComArr[vI];
                                if(vCodCommessa.trim()
                                            .equalsIgnoreCase(vString))
                                {
                                    String vA39Row = "<b>"
                                                + vCodCol + "</b>"
                                                + "\t" + vDescCommessa
                                                + "\t" + vOre;
                                    vResp += "\r\n".concat(vA39Row);
                                    break;
                                }

                            }
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + (aIsNotification? ". Ti aggiorno sulla commessa" + Arrays.toString(vCodComArr) : ". Commessa " + Arrays.toString(vCodComArr))
                        + ", del giorno " + vDateNowDay + "/"
                        + vDateNowMonth + "/" + vDateNowYear + ": \r\n"
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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

        }
        else if(("INDICI").equalsIgnoreCase(vFun) && Utility.isFunctionEnabled(vFun, vFirstName, vLastName, vUserID))
        {
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Queste sono le funzioni sugli Indici.";
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
            if(!aIsNotification)
            {
                vKeyboardMarkup = new IndiciReplyKeyboardMarkup();
            }
        }

        else if((vFun.toUpperCase().startsWith("INDICI ")
                    || vFun.toUpperCase().startsWith("INDICI_")) && Utility.isFunctionEnabled(vFun, vFirstName, vLastName, vUserID))
        {
            String vResp = "";
            Date vDate= Calendar.getInstance().getTime();
            String vCodDay = vFun.substring(("INDICI ").length())
                        .trim();

            if("IERI".equalsIgnoreCase(vCodDay))
            {
                vDate= Utility.getPreviousWorkingDay(Calendar.getInstance().getTime());
            }
            
            String vDateString = new SimpleDateFormat("yyyyMMdd")
                        .format(vDate);
            String vDateStringYear = new SimpleDateFormat("yyyy")
                        .format(vDate);
            String vDateStringMonth = new SimpleDateFormat("MM")
                        .format(vDate);
            String vDateStringDay = new SimpleDateFormat("dd")
                        .format(vDate);

            String vFunToCall = MessageFormat.format(FUN_IND_DAY,
                                                     vDateString);
            String vXmlResp;
            A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
            }
            String vFilePath = vTempDir + "\\resp"
                        + System.currentTimeMillis() + ".xml";
            Document vDoc = UIXmlUtilities
                        .buildDocumentFromXmlString(vXmlResp);
            if(vDoc != null)
            {
                UIXmlUtilities.buildXmlFileFromDocument(vDoc,
                                                        vFilePath);
                Element vRoot = vDoc.getRootElement();
                Element vGrigliaEl = (vRoot != null
                            ? vRoot.element("Griglia"): null);
                String[] vColArr= new String[] {};
                if(vGrigliaEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vGrigliaEl.elements("Colonna"));
                    if(vList!=null && vList.size()>0)
                    {
                        vColArr= new String[vList.size()];
                    }
                    int vI=0;
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext())
                    {
                        Element vElement = (Element) vElIter.next();
                        String vTxt= vElement.attributeValue("Txt",
                                                              "");
                        vColArr[vI]= vTxt;
                        vI= vI+1;
                    }
                }
                
                
                Element vRigheEl = (vRoot != null
                            ? vRoot.element("Righe"): null);
                if(vRigheEl != null)
                {
                    Element vElement= vRigheEl.element("Riga");
                    if(vElement!=null)
                    {
                        
                        String vFld = vElement.attributeValue("Fld", "");
                        if(vFld.indexOf("|") > -1)
                        {
                            NumberFormat vFormat= NumberFormat.getInstance();
                            String[] vSplit = vFld.split("\\|");
                            for( int vI = 0; vI < vSplit.length; vI++)
                            {
                                String vValue= vSplit[vI];
                                vValue= vValue.replace(".", "");
                                vValue= vValue.replace(",", ".");
                                String vValueString= vValue;
                                if(vValue!=null && !"".equalsIgnoreCase(vValue.trim()))
                                {
                                    try
                                    {
                                        double vIntValue= Double.parseDouble(vValue);
                                        
                                        vValueString= vFormat.format(vIntValue);
                                    }
                                    catch(NumberFormatException vEx)
                                    {
                                        vEx.printStackTrace();
                                    }
                                }
                                if(vColArr.length<vI)
                                {
                                    break;
                                }
                                else if(!"".equalsIgnoreCase(vColArr[vI].trim()))
                                {
                                    String vA39Row = "<b>"
                                                + vColArr[vI].replace("|", " ").concat(": ") + "</b>"
                                                + "\t" + vValueString;
                                    vResp += "\r\n".concat(vA39Row);
                                }
                                
                            }
                        }
                        
                    }
                }
                String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + (aIsNotification? ". Ti aggiorno sul totale indici del giorno " :  ". Totale indici del giorno ") + vDateStringDay + "/"
                        + vDateStringMonth + "/" + vDateStringYear + ": \r\n"
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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
            }
        }
        else if(("CLIENTI").equalsIgnoreCase(vFun))
        {
            String vResp = "";
            String vFunToCall = FUN_CLI;
            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
                        .checkOut();
            String vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
            }
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext()
                                && vResp.length() < 4000)
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            String vCod = vSplit.length > 5
                                        ? vSplit[5]: "";
                            String vDesc = getHtmlEncodedString(vSplit.length > 6
                                        ? vSplit[6]: "");
                            String vA39Row = vCod + "\t" + "<b>"
                                        + vDesc + "</b>"
                                        + "\t" + "/CLIENTE_" + vCod
                                        + " /INDIRIZZO_CLIENTE_"
                                        + vCod;
                            vResp += "\r\n".concat(vA39Row);
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco quello che hai chiesto: "
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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

        }
        else if(((vFun).startsWith("CLIENTE ")
                    || (vFun).startsWith("CLIENTE_")))
        {
            String vResp = "";
            String vCod = vFun.substring(("CLIENTE ").length());
            String vFunToCall = MessageFormat.format(FUN_CLI_DAT, vCod);

            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
                        .checkOut();
            String vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
            }
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext()
                                && vResp.length() < 4000)
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            String vA39Row = "<b>" + vSplit[1]
                                        + "</b>" + "\t"
                                        + "<i>" + vSplit[2]
                                        + "</i>";
                            vResp += "\r\n".concat(vA39Row);
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco quello che hai chiesto: "
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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
            if(!aIsNotification)
            {
                vKeyboardMarkup = new ClienteReplyKeyboardMarkup(vCod);
            }
        }
        else if(((vFun).startsWith("INDIRIZZO CLIENTE ")
                    || (vFun).startsWith("INDIRIZZO_CLIENTE_")))
        {
            String vResp = "";
            String vCod = vFun
                        .substring(("INDIRIZZO CLIENTE ").length());
            String vFunToCall = MessageFormat.format(FUN_CLI_ADDR,
                                                     vCod);
            // String vFunToCall = "F(EXB;BRK9CN;CRU) 1(CN;CLI;"
            // + vCod
            // + ")";
            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
                        .checkOut();
            String vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
            }

            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext()
                                && vResp.length() < 4000)
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            if("C. INDIRIZZO"
                                        .equalsIgnoreCase(vSplit[0]))
                            {
                                String vA39Row = (vSplit.length > 2
                                            ? "<b>" + vSplit[2]
                                                        + "</b>"
                                            : "")
                                            + ":\t"
                                            + (vSplit.length > 5
                                                        ? "<i>" + vSplit[5]
                                                                    + "</i>"
                                                        : "");
                                vResp += "\r\n".concat(vA39Row);
                            }
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco quello che hai chiesto: "
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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
        }
        else if(((vFun).startsWith("FISCALE CLIENTE ") || (vFun)
                    .startsWith("FISCALE_CLIENTE_")))
        {
            String vResp = "";
            String vCod = vFun
                        .substring(("FISCALE CLIENTE ").length());
            String vFunToCall = MessageFormat.format(FUN_CLI_FISC,
                                                     vCod);
            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
                        .checkOut();
            String vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
            }

            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext()
                                && vResp.length() < 4000)
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            if(vSplit[0].toUpperCase()
                                        .startsWith("F. RIFERIMENTI FISCALI"))
                            {
                                String vA39Row = (vSplit.length > 2
                                            ? "<b>" + vSplit[2]
                                                        + "</b>"
                                            : "")
                                            + ":\t"
                                            + (vSplit.length > 5
                                                        ? "<i>" + vSplit[5]
                                                                    + "</i>"
                                                        : "");
                                vResp += "\r\n".concat(vA39Row);
                            }
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco quello che hai chiesto: "
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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

        }
        else if(((vFun)
                    .startsWith("CONTABILE CLIENTE ")
                    || (vFun).startsWith("CONTABILE_CLIENTE_")))
        {
            String vResp = "";
            String vCod = vFun
                        .substring(("CONTABILE CLIENTE ").length());
            String vFunToCall = MessageFormat.format(FUN_CLI_CONT,
                                                     vCod);
            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
                        .checkOut();
            String vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
            }
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext())
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            if(vSplit[0].toUpperCase()
                                        .startsWith("G. RIFERIMENTI CONTABILI"))
                            {
                                String vA39Row = (vSplit.length > 2
                                            ? "<b>" + vSplit[2]
                                                        + "</b>"
                                            : "")
                                            + ":\t"
                                            + (vSplit.length > 5
                                                        ? "<i>" + vSplit[5]
                                                                    + "</i>"
                                                        : "");
                                vResp += "\r\n".concat(vA39Row);
                            }
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco quello che hai chiesto: "
                        + (vResp != null
                                    ? (vResp.length() > 4000
                                                ? vResp.substring(0,
                                                                  3000)
                                                : vResp)
                                    : "Risposta nulla");
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

        }
        else if(vFun.toUpperCase().startsWith("USERLIST")
                    || vFun.toUpperCase().startsWith("USERLIST"))
        {
            String vResp = "";
            String vFunToCall = FUN_AUTH_LIST;
            String vXmlResp;
            A39Connection vConn = SmeupConnectors.CLIENT_SRVAMM
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVAMM.checkIn(vConn);
            }
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext())
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String[] vSplit = vFld.split("\\|");
                            String vName = (vSplit.length > 0
                                        ? vSplit[0]: "");
                            String vSurname = (vSplit.length > 1
                                        ? vSplit[1]: "");
                            String vA39Row = "<b>"
                                        + vName + "</b>"
                                        + "\t" + "<b>" + vSurname
                                        + "</b>";
                            vResp += "\r\n".concat(vA39Row);

                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Elenco utenti: \r\n"
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla");
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

        }
        else if(((vFun).startsWith("IMMAGINE CLIENTE ") || (vFun)
                    .startsWith("IMMAGINE_CLIENTE_")))
        {
            String vResp = "";
            String vCod = vFun
                        .substring(("IMMAGINE CLIENTE ").length());
            String vRespText = "Ciao " + vFirstName + " " + vLastName
                        + ". Lafunzione immagine cliente è al momento disabilitata. ";
//                        + (vResp != null
//                                    ? (vResp.length() > 4096
//                                                ? vResp.substring(0,
//                                                                  4000)
//                                                : vResp)
//                                    : "Risposta nulla");
            try
            {
                vRespMsg = new String(vRespText.getBytes(), "UTF-8");
            }
            catch(UnsupportedEncodingException ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
//            telegramRequest = TelegramRequestFactory
//                        .createSendPhotoRequest(message
//                                    .getChat().getId(),
//                                                new File("c:\\temp\\a.png"),
//                                                vRespMsg,
//                                                message.getId(),
//                                                vKeyboardMarkup);

        }
        else if("FUNLIST".equalsIgnoreCase(vFun))
        {
            String vFunToCall = "FUN_LIST_XML";
            String vResp = "";
            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
                        .checkOut();
            String vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : UIXmlProvider.readXml(UIFunInputStructure
                                    .getFunInputStructure(vFunToCall));
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
            }
            String vFilePath = vTempDir + "\\resp"
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
                if(vRigheEl != null)
                {
                    ArrayList<Element> vList = new ArrayList(
                                vRigheEl.elements("Riga"));
                    Iterator<Element> vElIter = vList.iterator();
                    while(vElIter.hasNext())
                    {
                        Element vElement = (Element) vElIter.next();
                        String vFld = vElement.attributeValue("Fld",
                                                              "");
                        if(vFld.indexOf("|") > -1)
                        {
                            String vA39Command = vFld
                                        .substring(0, vFld
                                                    .indexOf("|"));
                            vResp += "\r\n"
                                        .concat("/" + vA39Command);
                        }
                    }
                }
            }
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco quello che hai chiesto: "
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4000)
                                                : vResp)
                                    : "Risposta nulla");
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

        }
        else
        {
            if(UIFunctionDecoder.isValidSyntaxFormat(vFun))
            {
                UIFunInputStructure vStruct = UIFunctionDecoder
                            .getFunInputStructure(vFun);
                A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
                            .checkOut();
                String vResp = vConn != null
                            ? vConn.executeFun(vFun,
                                               new HashMap<String, String>())
                            : (vStruct != null
                                        ? UIXmlProvider.readXml(vStruct)
                                        : "Funzione non supportata senza supporto A39.");
                if(vConn != null)
                {
                    SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
                }

                String vFilePath = vTempDir + "\\resp"
                            + System.currentTimeMillis() + ".xml";
                UIXmlUtilities.buildXmlFileFromDocument(
                                                        UIXmlUtilities
                                                                    .buildDocumentFromXmlString(vResp),
                                                        vFilePath);
                String vRespText = "Ciao " + vFirstName + " "
                            + vLastName
                            + ". Ecco quello che hai chiesto: "
                            + (vResp != null
                                        ? (vResp.length() > 4096
                                                    ? vResp.substring(0,
                                                                      4000)
                                                    : vResp)
                                        : "Risposta nulla");
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
            }
            else
            {
                try
                {
                    vRespMsg = new String(
                                "Richiesta non supportata".getBytes(),
                                "UTF-8");
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }
        }
        return new SmeupResponseData(vRespMsg, vKeyboardMarkup);
    }

    TelegramRequest createRequest(Message aMessage) throws JsonParsingException
    {
        CustomReplyKeyboard vKeyboardMarkup = new StartReplyKeyboardMarkup();
        long vUserID = aMessage.getFromUser().getId();
        long vChatID = aMessage.getChat().getId();

        String vFirstName = aMessage.getFromUser().getFirstName();
        String vLastName = aMessage.getFromUser().getLastName();
        
        if(false)
        {
            if(Utility.isActiveNotification(vUserID, iBotName))
            {
                ((StartReplyKeyboardMarkup)vKeyboardMarkup).setKeyboard(new String[][] {{"Start"}, {"Notifiche Disabilita (SVI)"}});
            }
            else
            {
                ((StartReplyKeyboardMarkup)vKeyboardMarkup).setKeyboard(new String[][] {{"Start"}, {"Notifiche Abilita (SVI)"}});
            }
        }
        
        MessageType vMessageType = getMessageType(aMessage);
        TelegramRequest telegramRequest = null;
        // A39Client vClient = null;

        String vTempDir = ".\\temp";
        new File(vTempDir).mkdirs();
        String vRespMsg = null;
        if(Utility.isEnablesUserName(vFirstName, vLastName, iBotName))
        // if(isEnablesUserID(vUserID) || isEnablesUserName(vFirstName,
        // vLastName))
        {
            if(MessageType.TEXT_MESSAGE.compareTo(vMessageType) == 0)
            {
                System.out.println("@" + iBotName + ": Messaggio da "
                            + vFirstName + " "
                            + vLastName + ". Id: " + vUserID+". Chat: "+vChatID);
                // if(iProviderAddress != null
                // && !"".equalsIgnoreCase(iProviderAddress.trim()))
                // {
                // vClient = new A39Client();
                // }
                String vFun = "SAMPLE";
                if(aMessage != null)
                {
                    vFun = aMessage.getText();
                }
                if(vFun.startsWith("/"))
                {
                    vFun = vFun.replace("/", "");
                }
                
                SmeupResponseData vRespData= createSmeupResponse(vFun, vFirstName, vLastName, vUserID, vChatID, false, vKeyboardMarkup);
                vRespMsg= vRespData.getText();
                vKeyboardMarkup= vRespData.getKeyboard();
            }
            else if(MessageType.IMAGE_MESSAGE.compareTo(vMessageType) == 0)
            {
                try
                {
                    String vRet = "";
                    PhotoSize[] vDoc = aMessage.getPhoto();
                    for( int vI = 0; vI < vDoc.length; vI++)
                    {
                        PhotoSize vPhotoSize = vDoc[vI];
                        String vFileId = vPhotoSize.getFileId();
                        Integer vFileHeight = vPhotoSize.getHeight();
                        Integer vFileSize = vPhotoSize.getFileSize();
                        Integer vFileWidth = vPhotoSize.getWidth();
                        vRet += "Immagine ricevuta Id: " + vFileId
                                    + ", Height: " + vFileHeight + ", Size: "
                                    + vFileSize + ", Width: " + vFileWidth
                                    + "\r\n";
                    }
                    vRespMsg = new String(vRet.getBytes(), "UTF-8");
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
            else if(MessageType.DOCUMENT_MESSAGE.compareTo(vMessageType) == 0)
            {
                try
                {
                    io.github.nixtabyte.telegram.jtelebot.response.json.Document vDoc = aMessage
                                .getDocument();
                    String vFileId = vDoc.getFileId();
                    String vFileName = vDoc.getFileName();
                    Integer vFileSize = vDoc.getFileSize();
                    String vMimeType = vDoc.getMimeType();
                    vRespMsg = new String(("Documento ricevuto Id: " + vFileId
                                + ", Name: " + vFileName + ", Size: "
                                + vFileSize + ", Mime: " + vMimeType)
                                            .getBytes(),
                                "UTF-8");

                    try
                    {
                        String vGetFile = new FileRequestHandler(
                                    BotData.BOT_SMEUP_TOKEN)
                                                .sendRequest(vFileId);
                        System.out.println(vGetFile);

                        String vFilePath = getFilePath(vGetFile);
                        vFilePath = vFilePath.replace("\\/", "/");
                        String vFileContent = new FileRequestHandler(
                                    BotData.BOT_SMEUP_TOKEN)
                                                .sendRequest(vFileId,
                                                             vFilePath);
                        telegramRequest = TelegramRequestFactory
                                    .createSendDocumentRequest(message.getChat()
                                                .getId(), vFileId, message
                                                            .getId(),
                                                               vKeyboardMarkup);
                    }
                    catch(TelegramServerException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }
            else if(MessageType.LOCATION_MESSAGE.compareTo(vMessageType) == 0)
            {
                try
                {
                    Location vDoc = aMessage.getLocation();
                    vRespMsg = new String(
                                ("Localizzazione ricevuta. Long: "
                                            + vDoc.getLongitude() + ", Lat: "
                                            + vDoc.getLatitude()).getBytes(),
                                "UTF-8");
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }
            else if(MessageType.CONTACT_MESSAGE.compareTo(vMessageType) == 0)
            {
                try
                {
                    Contact vDoc = aMessage.getContact();
                    vRespMsg = new String(
                                ("Contatto ricevuto. FirstName: "
                                            + vDoc.getFirstName() + ", Phone: "
                                            + vDoc.getPhoneNumber()).getBytes(),
                                "UTF-8");
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }
            else
            {
                try
                {
                    vRespMsg = new String("Richiesta non supportata".getBytes(),
                                "UTF-8");
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }

            }
        }
        // else if(!isEnablesUserID(vUserID))
        // {
        // vRespMsg = "Client " + vUserID + " non abilitato";
        // }
        else
        {
            vRespMsg = "Utente " + vFirstName + " " + vLastName
                        + " non autorizzato";
        }

        if(telegramRequest == null)
        {
            if(vRespMsg != null && vRespMsg.indexOf("/A39Service") > -1
                        && vRespMsg.indexOf("Manca la fun da eseguire") > -1)
            {
                vRespMsg = "*Funzione non supportata*";
            }
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

    private String getFilePath(String aGetFile)
    {
        String vRet = null;
        String vGetFileResponse = aGetFile;
        if(vGetFileResponse != null)
        {
            String vMarker = "\"file_path\":\"";
            int vIndex = vGetFileResponse.indexOf(vMarker);
            String vString1 = vGetFileResponse
                        .substring(vIndex + vMarker.length());
            vRet = vString1.substring(0, vString1.indexOf("\""));
            // file_path: "document/file_2.txt"
        }
        // TODO Auto-generated method stub
        return vRet;
    }

    public static String getHtmlEncodedString(String aTxt)
    {
        String vRet = null;
        if(aTxt != null)
        {
            vRet = StringUtility.stringToHTMLString(aTxt, true);
        }
        return vRet;
    }

    public static void main(String[] args)
    {
        System.out.println("C - B: " + "C".compareTo("B"));
        System.out.println("C - C: " + "C".compareTo("C"));
        System.out.println("A - B: " + "A".compareTo("B"));

    }
}