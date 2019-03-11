package Smeup.smeui.iotspi.connectors.telegram.command;

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

import javax.swing.plaf.basic.BasicScrollPaneUI.VSBChangeListener;

import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Smeup.smeui.iotspi.connectors.telegram.FileRequestHandler;
import Smeup.smeui.iotspi.connectors.telegram.SmeupResponseData;
import Smeup.smeui.iotspi.connectors.telegram.keyboard.AgendeReplyKeyboardMarkup;
import Smeup.smeui.iotspi.connectors.telegram.keyboard.CiaoReplyKeyboardMarkup;
import Smeup.smeui.iotspi.connectors.telegram.keyboard.CommesseReplyKeyboardMarkup;
import Smeup.smeui.iotspi.connectors.telegram.keyboard.IndiciReplyKeyboardMarkup;
import Smeup.smeui.iotspi.connectors.telegram.keyboard.KickOffReplyKeyboardMarkup;
import Smeup.smeui.iotspi.connectors.telegram.keyboard.StartReplyKeyboardMarkup;
import Smeup.smeui.iotspi.connectors.telegram.utility.SmeupConnectors;
import Smeup.smeui.iotspi.connectors.telegram.utility.Utility;
import Smeup.smeui.loa39.utility.A39Connection;
import Smeup.smeui.loa39.utility.UIXmlUtilities;
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

    public static final String FUN_AUTH_LIST = "F(EXB;LOA13_SE;ESE.SQL) 1(;;) 2(;;) INPUT(SELECT * FROM X1TLGM0F)";

    static final String FUN_AGE_DAY = "F(EXB;X1SER_32;ESE.MAT) 1(;;) 2(;;) INPUT(NO() DT({0}) CF(D) CodVer(No))";

    static final String FUN_IND_DAY = "F(EXB;X1AGEN_01;ANA.SIT) 1(D8;*YYMD;{0}) P(PER())";
    
    static final String FUN_LIS_PUL = "LISPULL";
    static final String FUN_PUL_PAS = "PULLPASS";
    static final String FUN_LIS_GRU = "LISGRU";
    static final String FUN_GRU_PER = "GROPEO";
    static final String FUN_PER_DET = "PEODET";
    
    static final int NUMERO_MASSIMO_ELEMENTI = 130;
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
            String vRespText = "Bentornato " + vFirstName + " "
                        + vLastName
                        + ". Ecco le opzioni attive.";
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
        else if(vFun.toUpperCase().startsWith("OGGI"))
        {
            String vDateNow = new SimpleDateFormat("yyyyMMdd")
                        .format(Calendar.getInstance().getTime());
            String vDateNowYear = new SimpleDateFormat("yyyy")
                        .format(Calendar.getInstance().getTime());
            String vDateNowYearMonth = new SimpleDateFormat("yyyyMM")
                        .format(Calendar.getInstance().getTime());
            String vDateNowMonth = new SimpleDateFormat("MM")
                        .format(Calendar.getInstance().getTime());
            String vOggi = new SimpleDateFormat("dd/MM/yyyy")
                        .format(Calendar.getInstance().getTime());
            
            String vXmlResp;
            String vFunToCall = MessageFormat.format(FUN_AGE_DAY,
                                                     vDateNow);
            A39Connection vConn = SmeupConnectors.CLIENT_A39
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : null;
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_A39.checkIn(vConn);
            }
            String vName = "";
            String vCodCommessa = "";
            String vDescCommessa = "";
            String vOre = "";
            String vFilePath = vTempDir + "\\resp"
                        + System.currentTimeMillis() + ".xml";
            Document vDoc = UIXmlUtilities
                        .buildDocumentFromXmlString(vXmlResp);
            String vResp="";

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
                            vCodCommessa= (vSplit.length > 14
                                        ? vSplit[14]: "");
                            vDescCommessa = (vSplit.length > 16
                                        ? vSplit[16]: "");
                            vOre = (vSplit.length > 25
                                        ? vSplit[25]: "");
                            if(vName.equalsIgnoreCase(new String(vLastName+" "+vFirstName).toUpperCase()))
                            {
                                String vA39Row = "<b>"
                                            + vCodCommessa + "</b>"
                                            + "\t" + vDescCommessa
                                            + "\t" + vOre;
                                vResp += "\r\n".concat(vA39Row);
                            }
                        }
                    }
                }
            }
            
            String vRespText = new String("Ciao " + vFirstName + " "
                        + vLastName
                        + ". Oggi e' il "+vOggi+", e la tua agenda prevede\r\n"
                        + (vResp != null
                                    ? (vResp.length() > 4096
                                                ? vResp.substring(0,
                                                                  4096)
                                                : vResp)
                                    : "Risposta nulla")
                        + "\r\nPer aggiungere/eliminare ore digita:\r\n"
                        + "+/- [ORE] [CODICE-COMMESSA] [BREVE NOTA]");
            
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
        else if(vFun.toUpperCase().startsWith("+") || vFun.toUpperCase().startsWith("-") && (vFun.length()>1 && (" ".equalsIgnoreCase(vFun.substring(1,2))) || Character.isDigit(vFun.charAt(1))))
        {
            String vDateNow = new SimpleDateFormat("yyyyMMdd")
                        .format(Calendar.getInstance().getTime());
            String vDateNowYear = new SimpleDateFormat("yyyy")
                        .format(Calendar.getInstance().getTime());
            String vDateNowYearMonth = new SimpleDateFormat("yyyyMM")
                        .format(Calendar.getInstance().getTime());
            String vDateNowMonth = new SimpleDateFormat("MM")
                        .format(Calendar.getInstance().getTime());
            String vOggi = new SimpleDateFormat("dd/MM/yyyy")
                        .format(Calendar.getInstance().getTime());
            
            String vXmlResp;

            String vFunToCall = MessageFormat.format(FUN_AGE_DAY,
                                                     vDateNow);
            A39Connection vConn = SmeupConnectors.CLIENT_A39
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : null;
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_A39.checkIn(vConn);
            }
            String vName = "";
            String vCodCommessa = "";
            String vDescCommessa = "";
            String vOre = "";
            String vFilePath = vTempDir + "\\resp"
                        + System.currentTimeMillis() + ".xml";
            Document vDoc = UIXmlUtilities
                        .buildDocumentFromXmlString(vXmlResp);
            String vResp="";

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
                            vCodCommessa = (vSplit.length > 14
                                        ? vSplit[14]: "");
                            vDescCommessa = (vSplit.length > 16
                                        ? vSplit[16]: "");
                            vOre = (vSplit.length > 25
                                        ? vSplit[25]: "");
                            if(vName.equalsIgnoreCase(new String(vLastName+" "+vFirstName).toUpperCase()))
                            {
                                String vA39Row = "<b>"
                                            + vCodCommessa + "</b>"
                                            + "\t" + vDescCommessa
                                            + "\t" + vOre;
                                vResp += "\r\n".concat(vA39Row);
                            }
                        }
                    }
                }
            }
            
            
            String vRespText = "Grazie " + vFirstName + " "
                        + vLastName
                        + ". Hai modificato l'agenda. La nuova situazione e' la seguente:\r\n" 
                        + vResp;
            
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
            A39Connection vConn = SmeupConnectors.CLIENT_A39
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : null;
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_A39.checkIn(vConn);
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
        else if (vFun.toUpperCase().startsWith("AGENDE") && Utility.isFunctionEnabled(vFun, vFirstName, vLastName, vUserID))
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
            A39Connection vConn = SmeupConnectors.CLIENT_A39
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : null;
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_A39.checkIn(vConn);
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
                            vCodCol = (vSplit.length > 3
                                        ? vSplit[3]: "");
                            vDescCol = (vSplit.length > 4
                                        ? vSplit[4]: "");
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
        else if ((vFun.toUpperCase().startsWith("AGENDA ")
                    || vFun.toUpperCase().startsWith("AGENDA_")) && Utility.isFunctionEnabled(vFun, vFirstName, vLastName, vUserID))
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
                A39Connection vConn = SmeupConnectors.CLIENT_A39
                            .checkOut();
                vXmlResp = vConn != null
                            ? vConn.executeFun(vFunToCall,
                                               new HashMap<String, String>())
                            : null;
                if(vConn != null)
                {
                    SmeupConnectors.CLIENT_A39.checkIn(vConn);
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
                A39Connection vConn = SmeupConnectors.CLIENT_A39
                            .checkOut();
                vXmlResp = vConn != null
                            ? vConn.executeFun(vFunToCall,
                                               new HashMap<String, String>())
                            : null;
                if(vConn != null)
                {
                    SmeupConnectors.CLIENT_A39.checkIn(vConn);
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
        else if (vFun.toUpperCase().startsWith("COMMESSE") && Utility.isFunctionEnabled(vFun, vFirstName, vLastName, vUserID))
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
        else if ((vFun.toUpperCase().startsWith("COMMESSA ")
                    || vFun.toUpperCase().startsWith("COMMESSA_")) && Utility.isFunctionEnabled(vFun, vFirstName, vLastName, vUserID))
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
            A39Connection vConn = SmeupConnectors.CLIENT_A39
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : null;
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_A39.checkIn(vConn);
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
            A39Connection vConn = SmeupConnectors.CLIENT_A39
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : null;
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_A39.checkIn(vConn);
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
//        else if(("CLIENTI").equalsIgnoreCase(vFun))
//        {
//            String vResp = "";
//            String vFunToCall = FUN_CLI;
//            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
//                        .checkOut();
//            String vXmlResp = vConn != null
//                        ? vConn.executeFun(vFunToCall,
//                                           new HashMap<String, String>())
//                        : null;
//            if(vConn != null)
//            {
//                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
//            }
//            String vFilePath = vTempDir + "\\resp"
//                        + System.currentTimeMillis() + ".xml";
//            Document vDoc = UIXmlUtilities
//                        .buildDocumentFromXmlString(vXmlResp);
//            if(vDoc != null)
//            {
//                UIXmlUtilities.buildXmlFileFromDocument(vDoc,
//                                                        vFilePath);
//                Element vRoot = vDoc.getRootElement();
//                Element vRigheEl = (vRoot != null
//                            ? vRoot.element("Righe"): null);
//                if(vRigheEl != null)
//                {
//                    ArrayList<Element> vList = new ArrayList(
//                                vRigheEl.elements("Riga"));
//                    Iterator<Element> vElIter = vList.iterator();
//                    while(vElIter.hasNext()
//                                && vResp.length() < 4000)
//                    {
//                        Element vElement = (Element) vElIter.next();
//                        String vFld = vElement.attributeValue("Fld",
//                                                              "");
//                        if(vFld.indexOf("|") > -1)
//                        {
//                            String[] vSplit = vFld.split("\\|");
//                            String vCod = vSplit.length > 5
//                                        ? vSplit[5]: "";
//                            String vDesc = getHtmlEncodedString(vSplit.length > 6
//                                        ? vSplit[6]: "");
//                            String vA39Row = vCod + "\t" + "<b>"
//                                        + vDesc + "</b>"
//                                        + "\t" + "/CLIENTE_" + vCod
//                                        + " /INDIRIZZO_CLIENTE_"
//                                        + vCod;
//                            vResp += "\r\n".concat(vA39Row);
//                        }
//                    }
//                }
//            }
//            String vRespText = "Ciao " + vFirstName + " "
//                        + vLastName
//                        + ". Ecco quello che hai chiesto: "
//                        + (vResp != null
//                                    ? (vResp.length() > 4096
//                                                ? vResp.substring(0,
//                                                                  4096)
//                                                : vResp)
//                                    : "Risposta nulla");
//            try
//            {
//                vRespMsg = new String(vRespText.getBytes(),
//                            "UTF-8");
//            }
//            catch(UnsupportedEncodingException ex)
//            {
//                // TODO Auto-generated catch block
//                ex.printStackTrace();
//            }
//
//        }
//        else if(((vFun).startsWith("CLIENTE ")
//                    || (vFun).startsWith("CLIENTE_")))
//        {
//            String vResp = "";
//            String vCod = vFun.substring(("CLIENTE ").length());
//            String vFunToCall = MessageFormat.format(FUN_CLI_DAT, vCod);
//
//            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
//                        .checkOut();
//            String vXmlResp = vConn != null
//                        ? vConn.executeFun(vFunToCall,
//                                           new HashMap<String, String>())
//                        : null;
//            if(vConn != null)
//            {
//                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
//            }
//            String vFilePath = vTempDir + "\\resp"
//                        + System.currentTimeMillis() + ".xml";
//            Document vDoc = UIXmlUtilities
//                        .buildDocumentFromXmlString(vXmlResp);
//            if(vDoc != null)
//            {
//                UIXmlUtilities.buildXmlFileFromDocument(vDoc,
//                                                        vFilePath);
//                Element vRoot = vDoc.getRootElement();
//                Element vRigheEl = (vRoot != null
//                            ? vRoot.element("Righe"): null);
//                if(vRigheEl != null)
//                {
//                    ArrayList<Element> vList = new ArrayList(
//                                vRigheEl.elements("Riga"));
//                    Iterator<Element> vElIter = vList.iterator();
//                    while(vElIter.hasNext()
//                                && vResp.length() < 4000)
//                    {
//                        Element vElement = (Element) vElIter.next();
//                        String vFld = vElement.attributeValue("Fld",
//                                                              "");
//                        if(vFld.indexOf("|") > -1)
//                        {
//                            String[] vSplit = vFld.split("\\|");
//                            String vA39Row = "<b>" + vSplit[1]
//                                        + "</b>" + "\t"
//                                        + "<i>" + vSplit[2]
//                                        + "</i>";
//                            vResp += "\r\n".concat(vA39Row);
//                        }
//                    }
//                }
//            }
//            String vRespText = "Ciao " + vFirstName + " "
//                        + vLastName
//                        + ". Ecco quello che hai chiesto: "
//                        + (vResp != null
//                                    ? (vResp.length() > 4096
//                                                ? vResp.substring(0,
//                                                                  4096)
//                                                : vResp)
//                                    : "Risposta nulla");
//            try
//            {
//                vRespMsg = new String(vRespText.getBytes(),
//                            "UTF-8");
//            }
//            catch(UnsupportedEncodingException ex)
//            {
//                // TODO Auto-generated catch block
//                ex.printStackTrace();
//            }
//            if(!aIsNotification)
//            {
//                vKeyboardMarkup = new ClienteReplyKeyboardMarkup(vCod);
//            }
//        }
//        else if(((vFun).startsWith("INDIRIZZO CLIENTE ")
//                    || (vFun).startsWith("INDIRIZZO_CLIENTE_")))
//        {
//            String vResp = "";
//            String vCod = vFun
//                        .substring(("INDIRIZZO CLIENTE ").length());
//            String vFunToCall = MessageFormat.format(FUN_CLI_ADDR,
//                                                     vCod);
//            // String vFunToCall = "F(EXB;BRK9CN;CRU) 1(CN;CLI;"
//            // + vCod
//            // + ")";
//            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
//                        .checkOut();
//            String vXmlResp = vConn != null
//                        ? vConn.executeFun(vFunToCall,
//                                           new HashMap<String, String>())
//                        : null;
//            if(vConn != null)
//            {
//                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
//            }
//
//            String vFilePath = vTempDir + "\\resp"
//                        + System.currentTimeMillis() + ".xml";
//            Document vDoc = UIXmlUtilities
//                        .buildDocumentFromXmlString(vXmlResp);
//            if(vDoc != null)
//            {
//                UIXmlUtilities.buildXmlFileFromDocument(vDoc,
//                                                        vFilePath);
//                Element vRoot = vDoc.getRootElement();
//                Element vRigheEl = (vRoot != null
//                            ? vRoot.element("Righe"): null);
//                if(vRigheEl != null)
//                {
//                    ArrayList<Element> vList = new ArrayList(
//                                vRigheEl.elements("Riga"));
//                    Iterator<Element> vElIter = vList.iterator();
//                    while(vElIter.hasNext()
//                                && vResp.length() < 4000)
//                    {
//                        Element vElement = (Element) vElIter.next();
//                        String vFld = vElement.attributeValue("Fld",
//                                                              "");
//                        if(vFld.indexOf("|") > -1)
//                        {
//                            String[] vSplit = vFld.split("\\|");
//                            if("C. INDIRIZZO"
//                                        .equalsIgnoreCase(vSplit[0]))
//                            {
//                                String vA39Row = (vSplit.length > 2
//                                            ? "<b>" + vSplit[2]
//                                                        + "</b>"
//                                            : "")
//                                            + ":\t"
//                                            + (vSplit.length > 5
//                                                        ? "<i>" + vSplit[5]
//                                                                    + "</i>"
//                                                        : "");
//                                vResp += "\r\n".concat(vA39Row);
//                            }
//                        }
//                    }
//                }
//            }
//            String vRespText = "Ciao " + vFirstName + " "
//                        + vLastName
//                        + ". Ecco quello che hai chiesto: "
//                        + (vResp != null
//                                    ? (vResp.length() > 4096
//                                                ? vResp.substring(0,
//                                                                  4096)
//                                                : vResp)
//                                    : "Risposta nulla");
//            try
//            {
//                vRespMsg = new String(vRespText.getBytes(),
//                            "UTF-8");
//            }
//            catch(UnsupportedEncodingException ex)
//            {
//                // TODO Auto-generated catch block
//                ex.printStackTrace();
//            }
//        }
//        else if(((vFun).startsWith("FISCALE CLIENTE ") || (vFun)
//                    .startsWith("FISCALE_CLIENTE_")))
//        {
//            String vResp = "";
//            String vCod = vFun
//                        .substring(("FISCALE CLIENTE ").length());
//            String vFunToCall = MessageFormat.format(FUN_CLI_FISC,
//                                                     vCod);
//            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
//                        .checkOut();
//            String vXmlResp = vConn != null
//                        ? vConn.executeFun(vFunToCall,
//                                           new HashMap<String, String>())
//                        : null;
//            if(vConn != null)
//            {
//                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
//            }
//
//            String vFilePath = vTempDir + "\\resp"
//                        + System.currentTimeMillis() + ".xml";
//            Document vDoc = UIXmlUtilities
//                        .buildDocumentFromXmlString(vXmlResp);
//            if(vDoc != null)
//            {
//                UIXmlUtilities.buildXmlFileFromDocument(vDoc,
//                                                        vFilePath);
//                Element vRoot = vDoc.getRootElement();
//                Element vRigheEl = (vRoot != null
//                            ? vRoot.element("Righe"): null);
//                if(vRigheEl != null)
//                {
//                    ArrayList<Element> vList = new ArrayList(
//                                vRigheEl.elements("Riga"));
//                    Iterator<Element> vElIter = vList.iterator();
//                    while(vElIter.hasNext()
//                                && vResp.length() < 4000)
//                    {
//                        Element vElement = (Element) vElIter.next();
//                        String vFld = vElement.attributeValue("Fld",
//                                                              "");
//                        if(vFld.indexOf("|") > -1)
//                        {
//                            String[] vSplit = vFld.split("\\|");
//                            if(vSplit[0].toUpperCase()
//                                        .startsWith("F. RIFERIMENTI FISCALI"))
//                            {
//                                String vA39Row = (vSplit.length > 2
//                                            ? "<b>" + vSplit[2]
//                                                        + "</b>"
//                                            : "")
//                                            + ":\t"
//                                            + (vSplit.length > 5
//                                                        ? "<i>" + vSplit[5]
//                                                                    + "</i>"
//                                                        : "");
//                                vResp += "\r\n".concat(vA39Row);
//                            }
//                        }
//                    }
//                }
//            }
//            String vRespText = "Ciao " + vFirstName + " "
//                        + vLastName
//                        + ". Ecco quello che hai chiesto: "
//                        + (vResp != null
//                                    ? (vResp.length() > 4096
//                                                ? vResp.substring(0,
//                                                                  4096)
//                                                : vResp)
//                                    : "Risposta nulla");
//            try
//            {
//                vRespMsg = new String(vRespText.getBytes(),
//                            "UTF-8");
//            }
//            catch(UnsupportedEncodingException ex)
//            {
//                // TODO Auto-generated catch block
//                ex.printStackTrace();
//            }
//
//        }
//        else if(((vFun)
//                    .startsWith("CONTABILE CLIENTE ")
//                    || (vFun).startsWith("CONTABILE_CLIENTE_")))
//        {
//            String vResp = "";
//            String vCod = vFun
//                        .substring(("CONTABILE CLIENTE ").length());
//            String vFunToCall = MessageFormat.format(FUN_CLI_CONT,
//                                                     vCod);
//            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
//                        .checkOut();
//            String vXmlResp = vConn != null
//                        ? vConn.executeFun(vFunToCall,
//                                           new HashMap<String, String>())
//                        : null;
//            if(vConn != null)
//            {
//                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
//            }
//            String vFilePath = vTempDir + "\\resp"
//                        + System.currentTimeMillis() + ".xml";
//            Document vDoc = UIXmlUtilities
//                        .buildDocumentFromXmlString(vXmlResp);
//            if(vDoc != null)
//            {
//                UIXmlUtilities.buildXmlFileFromDocument(vDoc,
//                                                        vFilePath);
//                Element vRoot = vDoc.getRootElement();
//                Element vRigheEl = (vRoot != null
//                            ? vRoot.element("Righe"): null);
//                if(vRigheEl != null)
//                {
//                    ArrayList<Element> vList = new ArrayList(
//                                vRigheEl.elements("Riga"));
//                    Iterator<Element> vElIter = vList.iterator();
//                    while(vElIter.hasNext())
//                    {
//                        Element vElement = (Element) vElIter.next();
//                        String vFld = vElement.attributeValue("Fld",
//                                                              "");
//                        if(vFld.indexOf("|") > -1)
//                        {
//                            String[] vSplit = vFld.split("\\|");
//                            if(vSplit[0].toUpperCase()
//                                        .startsWith("G. RIFERIMENTI CONTABILI"))
//                            {
//                                String vA39Row = (vSplit.length > 2
//                                            ? "<b>" + vSplit[2]
//                                                        + "</b>"
//                                            : "")
//                                            + ":\t"
//                                            + (vSplit.length > 5
//                                                        ? "<i>" + vSplit[5]
//                                                                    + "</i>"
//                                                        : "");
//                                vResp += "\r\n".concat(vA39Row);
//                            }
//                        }
//                    }
//                }
//            }
//            String vRespText = "Ciao " + vFirstName + " "
//                        + vLastName
//                        + ". Ecco quello che hai chiesto: "
//                        + (vResp != null
//                                    ? (vResp.length() > 4000
//                                                ? vResp.substring(0,
//                                                                  3000)
//                                                : vResp)
//                                    : "Risposta nulla");
//            try
//            {
//                vRespMsg = new String(vRespText.getBytes(),
//                            "UTF-8");
//            }
//            catch(UnsupportedEncodingException ex)
//            {
//                // TODO Auto-generated catch block
//                ex.printStackTrace();
//            }
//
//        }
        else if(vFun.toUpperCase().startsWith("USERLIST")
                    || vFun.toUpperCase().startsWith("USERLIST"))
        {
            String vResp = "";
            String vFunToCall = FUN_AUTH_LIST;
            String vXmlResp;
            A39Connection vConn = SmeupConnectors.CLIENT_A39
                        .checkOut();
            vXmlResp = vConn != null
                        ? vConn.executeFun(vFunToCall,
                                           new HashMap<String, String>())
                        : null;
            if(vConn != null)
            {
                SmeupConnectors.CLIENT_A39.checkIn(vConn);
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
//        else if("FUNLIST".equalsIgnoreCase(vFun))
//        {
//            String vFunToCall = "FUN_LIST_XML";
//            String vResp = "";
//            A39Connection vConn = SmeupConnectors.CLIENT_SRVLAB01
//                        .checkOut();
//            String vXmlResp = vConn != null
//                        ? vConn.executeFun(vFunToCall,
//                                           new HashMap<String, String>())
//                        : null;
//            if(vConn != null)
//            {
//                SmeupConnectors.CLIENT_SRVLAB01.checkIn(vConn);
//            }
//            String vFilePath = vTempDir + "\\resp"
//                        + System.currentTimeMillis() + ".xml";
//            Document vDoc = UIXmlUtilities
//                        .buildDocumentFromXmlString(vXmlResp);
//            if(vDoc != null)
//            {
//                UIXmlUtilities.buildXmlFileFromDocument(vDoc,
//                                                        vFilePath);
//                Element vRoot = vDoc.getRootElement();
//                Element vRigheEl = (vRoot != null
//                            ? vRoot.element("Righe"): null);
//                if(vRigheEl != null)
//                {
//                    ArrayList<Element> vList = new ArrayList(
//                                vRigheEl.elements("Riga"));
//                    Iterator<Element> vElIter = vList.iterator();
//                    while(vElIter.hasNext())
//                    {
//                        Element vElement = (Element) vElIter.next();
//                        String vFld = vElement.attributeValue("Fld",
//                                                              "");
//                        if(vFld.indexOf("|") > -1)
//                        {
//                            String vA39Command = vFld
//                                        .substring(0, vFld
//                                                    .indexOf("|"));
//                            vResp += "\r\n"
//                                        .concat("/" + vA39Command);
//                        }
//                    }
//                }
//            }
//            String vRespText = "Ciao " + vFirstName + " "
//                        + vLastName
//                        + ". Ecco quello che hai chiesto: "
//                        + (vResp != null
//                                    ? (vResp.length() > 4096
//                                                ? vResp.substring(0,
//                                                                  4000)
//                                                : vResp)
//                                    : "Risposta nulla");
//            try
//            {
//                vRespMsg = new String(vRespText.getBytes(),
//                            "UTF-8");
//            }
//            catch(UnsupportedEncodingException ex)
//            {
//                // TODO Auto-generated catch block
//                ex.printStackTrace();
//            }
//
//        }
        else if(vFun.toUpperCase().startsWith("KICK"))
        {
            String vRespText = "Ciao " + vFirstName + " "
                        + vLastName
                        + ". Ecco l'elenco delle richieste.";
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
                vKeyboardMarkup = new KickOffReplyKeyboardMarkup();
            }
        }
        else if (vFun.toUpperCase().startsWith("PULLMAN_")) {
        	String vCodicePullman = "";
            String vFunToCall = FUN_PUL_PAS;
            String vNumeroIter = "0";
            if (vFun.contains("_CONTINUA_")) {
            	vNumeroIter = vFun.substring(vFun.indexOf("_CONTINUA_")+("_CONTINUA_").length());
            	vCodicePullman = vFun.substring(("PULLMAN_").length(),vFun.indexOf("_CONTINUA_"));
            } else {
            	vCodicePullman = vFun.substring(("PULLMAN_").length());
            }
            int contaPagine=0;
            if (vNumeroIter!=null) {
            	try {
            		contaPagine=Integer.parseInt(vNumeroIter);
            	} catch (Exception e) {
            		
            	}
            }
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("*AUTH", "NULL");
            hm.put("XXCOD",vCodicePullman);
            
			String vJResp;
			A39Connection vConn = SmeupConnectors.CLIENT_A39.checkOut();
			vJResp = vConn != null ? vConn.executeFun(vFunToCall,hm): null;
			if(vConn != null) {
				SmeupConnectors.CLIENT_A39.checkIn(vConn);
			}
			JSONParser jPar = new JSONParser();
			Object oResp = null;
			try {
				oResp = jPar.parse(vJResp);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (oResp != null) {
				JSONObject jResp = (JSONObject) oResp;
				JSONObject jRoot = (JSONObject) jResp.get("root");
				JSONArray passeggeri = (JSONArray)jRoot.get("datarows");
				if (passeggeri != null && !passeggeri.isEmpty()) {
                	String vResp="";
                	int cntTot = 0;
                    Iterator<JSONObject> vElIter = passeggeri.iterator();
	                while(vElIter.hasNext())
	                {

	                	JSONObject vElement = (JSONObject) vElIter.next();
	                	String nome = (String) vElement.get("R1NOME");
	                	String cognome = (String) vElement.get("R1COGN");

                        String vA39Row = nome + " "
	                                     + "<b>" + cognome + "</b>";
                        
                        int quoz = cntTot / NUMERO_MASSIMO_ELEMENTI;
                        if (quoz==contaPagine) {                	
	                        vResp += "\r\n".concat(vA39Row);
                        } else {
                        	if (quoz > contaPagine) {
	                        	vResp += "\r\n".concat("\t" + "/PULLMAN_"+vCodicePullman+"_CONTINUA_"+quoz);
	                        	break;
                        	}
                        }
	                	cntTot++;
//	                    if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
//	                        vResp += "\r\n".concat(vA39Row);
//	                    } else {
//	                        break;
//	                    }
	                }
	                try
	                {
	                    vRespMsg = new String(vResp.getBytes(),
	                                "UTF-8");
	                }
	                catch(UnsupportedEncodingException ex)
	                {
	                    // TODO Auto-generated catch block
	                    ex.printStackTrace();
	                }
				} else {
		            try
		            {
		                vRespMsg = new String(vJResp.getBytes(),
		                            "UTF-8");
		            }
		            catch(UnsupportedEncodingException ex)
		            {
		                // TODO Auto-generated catch block
		                ex.printStackTrace();
		            }
				}
				
			}  else {
				String vResp="";
                String vA39Row = "Errore nella richiesta. Riprovare più tardi.";
       
                if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
                	vResp += "\r\n".concat(vA39Row);
                }
	            try
	            {
	                vRespMsg = new String(vResp.getBytes(),
	                            "UTF-8");
	            }
	            catch(UnsupportedEncodingException ex)
	            {
	                // TODO Auto-generated catch block
	                ex.printStackTrace();
	            }
			}

        }
        else if (vFun.toUpperCase().startsWith("PULLMAN")) {
            String vFunToCall = FUN_LIS_PUL;
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("*AUTH", "NULL");

			String vJResp;
			A39Connection vConn = SmeupConnectors.CLIENT_A39.checkOut();
			vJResp = vConn != null ? vConn.executeFun(vFunToCall,hm): null;
			if(vConn != null) {
				SmeupConnectors.CLIENT_A39.checkIn(vConn);
			}
			
			JSONParser jPar = new JSONParser();
			Object oResp = null;
			try {
				oResp = jPar.parse(vJResp);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (oResp != null) {
				JSONObject jResp = (JSONObject) oResp;
				JSONObject jRoot = (JSONObject) jResp.get("root");
				JSONArray pullmanList = (JSONArray)jRoot.get("datarows");
				if (pullmanList != null && !pullmanList.isEmpty()) {
                	String vResp="";
                    Iterator<JSONObject> vElIter = pullmanList.iterator();
	                while(vElIter.hasNext())
	                {
	                	JSONObject vElement = (JSONObject) vElIter.next();
	                	String vDesPull = (String) vElement.get("XXRES");
	                	String vCodPull = (String) vElement.get("XXCOD");
	                	int vnumPers = ((Long) vElement.get("XXNUM")).intValue();

                        String vA39Row = "<b>"
                                         + vDesPull + "</b>"
                                         + " " + vnumPers + " persone " 
	                                     + "\t" + "/PULLMAN_"
	                                     + vCodPull;
                        
	                    if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
	                        vResp += "\r\n".concat(vA39Row);
	                    } else {
	                        break;
	                    }
	                }
	                try
	                {
	                    vRespMsg = new String(vResp.getBytes(),
	                                "UTF-8");
	                }
	                catch(UnsupportedEncodingException ex)
	                {
	                    // TODO Auto-generated catch block
	                    ex.printStackTrace();
	                }
				} else {
		            try
		            {
		                vRespMsg = new String(vJResp.getBytes(),
		                            "UTF-8");
		            }
		            catch(UnsupportedEncodingException ex)
		            {
		                // TODO Auto-generated catch block
		                ex.printStackTrace();
		            }
				}
				
			} else {
				String vResp="";
                String vA39Row = "Errore nella richiesta. Riprovare più tardi.";
       
                if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
                	vResp += "\r\n".concat(vA39Row);
                }
	            try
	            {
	                vRespMsg = new String(vResp.getBytes(),
	                            "UTF-8");
	            }
	            catch(UnsupportedEncodingException ex)
	            {
	                // TODO Auto-generated catch block
	                ex.printStackTrace();
	            }
			}
			
        }
        else if (vFun.toUpperCase().startsWith("GRUPPO_")) {
        	String vCodiceGruppo = vFun.substring(("GRUPPO_").length());
            String vFunToCall = FUN_GRU_PER;
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("*AUTH", "NULL");
            hm.put("XXCOD",vCodiceGruppo);
            
			String vJResp;
			A39Connection vConn = SmeupConnectors.CLIENT_A39.checkOut();
			vJResp = vConn != null ? vConn.executeFun(vFunToCall,hm): null;
			if(vConn != null) {
				SmeupConnectors.CLIENT_A39.checkIn(vConn);
			}
			JSONParser jPar = new JSONParser();
			Object oResp = null;
			try {
				oResp = jPar.parse(vJResp);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (oResp != null) {
				JSONObject jResp = (JSONObject) oResp;
				JSONObject jRoot = (JSONObject) jResp.get("root");
				JSONArray persone = (JSONArray)jRoot.get("datarows");
				if (persone != null && !persone.isEmpty()) {
                	String vResp="";
                    Iterator<JSONObject> vElIter = persone.iterator();
	                while(vElIter.hasNext())
	                {
	                	JSONObject vElement = (JSONObject) vElIter.next();
	                	String nome = (String) vElement.get("R1NOME");
	                	String cognome = (String) vElement.get("R1COGN");

                        String vA39Row = nome + " "
	                                     + "<b>" + cognome + "</b>";
                        
	                    if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
	                        vResp += "\r\n".concat(vA39Row);
	                    } else {
	                        break;
	                    }
	                }
	                try
	                {
	                    vRespMsg = new String(vResp.getBytes(),
	                                "UTF-8");
	                }
	                catch(UnsupportedEncodingException ex)
	                {
	                    // TODO Auto-generated catch block
	                    ex.printStackTrace();
	                }
				} else {
		            try
		            {
		                vRespMsg = new String(vJResp.getBytes(),
		                            "UTF-8");
		            }
		            catch(UnsupportedEncodingException ex)
		            {
		                // TODO Auto-generated catch block
		                ex.printStackTrace();
		            }
				}
				
			} else {
				String vResp="";
                String vA39Row = "Errore nella richiesta. Riprovare più tardi.";
       
                if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
                	vResp += "\r\n".concat(vA39Row);
                }
	            try
	            {
	                vRespMsg = new String(vResp.getBytes(),
	                            "UTF-8");
	            }
	            catch(UnsupportedEncodingException ex)
	            {
	                // TODO Auto-generated catch block
	                ex.printStackTrace();
	            }
			}

        }

        else if(vFun.toUpperCase().startsWith("GRUPPI"))
        {
            String vFunToCall = FUN_LIS_GRU;
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("*AUTH", "NULL");

			String vJResp;
			A39Connection vConn = SmeupConnectors.CLIENT_A39.checkOut();
			vJResp = vConn != null ? vConn.executeFun(vFunToCall,hm): null;
			if(vConn != null) {
				SmeupConnectors.CLIENT_A39.checkIn(vConn);
			}
			
			JSONParser jPar = new JSONParser();
			Object oResp = null;
			try {
				oResp = jPar.parse(vJResp);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (oResp != null) {
				JSONObject jResp = (JSONObject) oResp;
				JSONObject jRoot = (JSONObject) jResp.get("root");
				JSONArray gruppiList = (JSONArray)jRoot.get("datarows");
				if (gruppiList != null && !gruppiList.isEmpty()) {
                	String vResp="";
                    Iterator<JSONObject> vElIter = gruppiList.iterator();
	                while(vElIter.hasNext())
	                {
	                	JSONObject vElement = (JSONObject) vElIter.next();
	                	String vDesPull = (String) vElement.get("XXRES");
	                	String vCodPull = (String) vElement.get("XXCOD");
	                	int vnumPers = ((Long) vElement.get("XXNUM")).intValue();

                        String vA39Row = "<b>"
                                         + vDesPull + "</b>"
                                         + " " + vnumPers + " persone " 
	                                     + "\t" + "/GRUPPO_"
	                                     + vCodPull;
                        
	                    if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
	                        vResp += "\r\n".concat(vA39Row);
	                    } else {
	                        break;
	                    }
	                }
	                try
	                {
	                    vRespMsg = new String(vResp.getBytes(),
	                                "UTF-8");
	                }
	                catch(UnsupportedEncodingException ex)
	                {
	                    // TODO Auto-generated catch block
	                    ex.printStackTrace();
	                }
				} else {
		            try
		            {
		                vRespMsg = new String(vJResp.getBytes(),
		                            "UTF-8");
		            }
		            catch(UnsupportedEncodingException ex)
		            {
		                // TODO Auto-generated catch block
		                ex.printStackTrace();
		            }
				}
				
			} else {
				String vResp="";
                String vA39Row = "Errore nella richiesta. Riprovare più tardi.";
       
                if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
                	vResp += "\r\n".concat(vA39Row);
                }
	            try
	            {
	                vRespMsg = new String(vResp.getBytes(),
	                            "UTF-8");
	            }
	            catch(UnsupportedEncodingException ex)
	            {
	                // TODO Auto-generated catch block
	                ex.printStackTrace();
	            }
			}
        }
        else if(vFun.toUpperCase().startsWith("MIODETTAGLIO"))
        {
            String vFunToCall = FUN_PER_DET;
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("*AUTH", "NULL");
            hm.put("NAM", vFirstName);
            hm.put("SUR", vLastName);

			String vJResp;
			A39Connection vConn = SmeupConnectors.CLIENT_A39.checkOut();
			vJResp = vConn != null ? vConn.executeFun(vFunToCall,hm): null;
			if(vConn != null) {
				SmeupConnectors.CLIENT_A39.checkIn(vConn);
			}
			
			JSONParser jPar = new JSONParser();
			Object oResp = null;
			try {
				oResp = jPar.parse(vJResp);
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (oResp != null) {
				JSONObject jResp = (JSONObject) oResp;
				JSONObject jRoot = (JSONObject) jResp.get("root");
				JSONArray gruppiList = (JSONArray)jRoot.get("datarows");
				if (gruppiList != null && !gruppiList.isEmpty()) {
                	String vResp="";
                    Iterator<JSONObject> vElIter = gruppiList.iterator();
	                while(vElIter.hasNext())
	                {
	                	JSONObject vElement = (JSONObject) vElIter.next();
	                	String vDesPull = (String) vElement.get("DESPUL");
	                	String vCodPull = (String) vElement.get("CODPUL");
	                	String vDesGrup = (String) vElement.get("DESGRU");
	                	String vCodGrup = (String) vElement.get("CODGRU");

                        String vA39Row = "Ciao " + vFirstName + " " + vLastName + "." +
                        		         "\r\n"+
                                		 "Ecco i tuoi riferimenti: "+
                                		 "\r\n"+
                                		 "Pullman: "+
                        				 "<b>"
                                         + vDesPull + "</b>"
	                                     + "\t" + "/PULLMAN_"
	                                     + vCodPull+
	                                     "\r\n"+
	                                     "Gruppo: "+
                        				 "<b>"
                                         + vDesGrup + "</b>"
	                                     + "\t" + "/GRUPPO_"
	                                     + vCodGrup;
                        
	                    if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
	                        vResp += "\r\n".concat(vA39Row);
	                    } else {
	                        break;
	                    }
	                }
	                try
	                {
	                    vRespMsg = new String(vResp.getBytes(),
	                                "UTF-8");
	                }
	                catch(UnsupportedEncodingException ex)
	                {
	                    // TODO Auto-generated catch block
	                    ex.printStackTrace();
	                }
				} else {
					String vResp="";
                    String vA39Row = "Ciao " + vFirstName + " " + vLastName + "." +
           		         "\r\n"+
                   		 "Siamo spiacenti, i tuoi riferimenti non sono stati trovati."+
                   		 "\r\n"+
                   		 "Manca la corrispondenza tra il nome del tuo account Telegram e i dati inseriti nel form di registrazione del Kick Off.";
           
                    if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
                    	vResp += "\r\n".concat(vA39Row);
                    }
		            try
		            {
		                vRespMsg = new String(vResp.getBytes(),
		                            "UTF-8");
		            }
		            catch(UnsupportedEncodingException ex)
		            {
		                // TODO Auto-generated catch block
		                ex.printStackTrace();
		            }
				}
				
			} else {
				String vResp="";
                String vA39Row = "Errore nella richiesta. Riprovare più tardi.";
       
                if (vResp.length() + "\r\n".concat(vA39Row).length() <= 4096) {
                	vResp += "\r\n".concat(vA39Row);
                }
	            try
	            {
	                vRespMsg = new String(vResp.getBytes(),
	                            "UTF-8");
	            }
	            catch(UnsupportedEncodingException ex)
	            {
	                // TODO Auto-generated catch block
	                ex.printStackTrace();
	            }
			}
        }
        else
        {
//            if(true)
////            if(UIFunctionDecoder.isValidSyntaxFormat(vFun))
//            {
//                UIFunInputStructure vStruct = UIFunctionDecoder
//                            .getFunInputStructure(vFun);
                A39Connection vConn = SmeupConnectors.CLIENT_A39
                            .checkOut();
                String vResp = vConn != null
                            ? vConn.executeFun(vFun,
                                               new HashMap<String, String>())
                            : null;
                if(vConn != null)
                {
                    SmeupConnectors.CLIENT_A39.checkIn(vConn);
                }

                String vFilePath = vTempDir + "\\resp"
                            + System.currentTimeMillis() + ".xml";
                
                Document doc = UIXmlUtilities
                        .buildDocumentFromXmlString(vResp);
                
                if (doc != null) {
	                UIXmlUtilities.buildXmlFileFromDocument(
	                                                        doc,
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
                } else {
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
//            }
//            else
//            {
//                try
//                {
//                    vRespMsg = new String(
//                                "Richiesta non supportata".getBytes(),
//                                "UTF-8");
//                }
//                catch(UnsupportedEncodingException ex)
//                {
//                    // TODO Auto-generated catch block
//                    ex.printStackTrace();
//                }
//
//            }
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
//MPB - 2019-03-01 commentato il controllo sugli utenti autorizzati
//        if(Utility.isEnablesUserName(vFirstName, vLastName, iBotName))
        // if(isEnablesUserID(vUserID) || isEnablesUserName(vFirstName,
        // vLastName))
//        {
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
//MPB - 2019-03-01 commentato il controllo sugli utenti autorizzati
//        }
//        // else if(!isEnablesUserID(vUserID))
//        // {
//        // vRespMsg = "Client " + vUserID + " non abilitato";
//        // }
//        else
//        {
//            vRespMsg = "Utente " + vFirstName + " " + vLastName
//                        + " non autorizzato";
//        }

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
            vRet = Utility.stringToHTMLString(aTxt, true);
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