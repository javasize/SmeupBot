package telegrambotjapi.main;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;

import Smeup.smeui.loa39.utility.A39Client;
import Smeup.smeui.loa39.utility.UIXmlUtilities;

import de.vivistra.telegrambot.model.PhotoSize;
import de.vivistra.telegrambot.model.User;
import de.vivistra.telegrambot.model.message.Message;
import de.vivistra.telegrambot.model.message.MessageType;
import de.vivistra.telegrambot.model.message.TextMessage;
import de.vivistra.telegrambot.receiver.IReceiverService;
import de.vivistra.telegrambot.sender.Sender;

public class SmeupReceiver implements IReceiverService
{
//    public static final ContentType CONTENT_TYPE = ContentType.create(ContentType.TEXT_PLAIN.getMimeType(), MIME.UTF8_CHARSET);
    
    Integer ENABLED_ID= 199971507;
    Integer ENABLED_ID_2 = 219217733;
    long MAX_TEXT_LENGTH= 4096;

    public boolean isEnablesUserID(Integer aId)
    {
        return ENABLED_ID.compareTo(aId) == 0
                    || ENABLED_ID_2.compareTo(aId) == 0;
    }

    
    @Override
    public void received(Message aMessage)
    {
        String vCommand= aMessage.getCommand();
        User vUser= aMessage.getSender();
        String vUserName= vUser.getUserName();
        String vFirstName= vUser.getFirstName();
        String vLastName= vUser.getLastName();
        Integer vUserID= vUser.getId();
        Object vReqMessage= aMessage.getMessage();
        MessageType vMessageType= aMessage.getMessageType();
        Message vRespMsg= null;
        if(isEnablesUserID(vUserID))
        {
            if(MessageType.TEXT_MESSAGE.compareTo(vMessageType)==0)
            {
                System.out.println("Messaggio da "+vFirstName +" "+vLastName+". Id: "+vUserID);
                A39Client vClient= new A39Client();
                
                String vFun= "SAMPLE";
                if(vReqMessage!=null)
                {
                    vFun= vReqMessage.toString();
                }
                if(vFun.startsWith("/"))
                {
                    vFun= vFun.replace("/", "");
                }

                if((isEnablesUserID(vUserID)) && ("CLIENTI").equalsIgnoreCase(vFun))
                {
                    try
                    {
                        String vResp= "";
                        String vXmlResp= vClient.httpCall("srv-smens", 29900, "F(EXB;LOA10_SE;ELE) 1(LI;CNCLI;*) 2(;;) INPUT(Sch() NCf(1) Context() SchPar() NTit(1) Qry(Yes) RPa())", "");
                        String vFilePath= "c:\\temp\\resp"+System.currentTimeMillis()+".xml";
                        Document vDoc= UIXmlUtilities.buildDocumentFromXmlString(vXmlResp);
                        if(vDoc!=null)
                        {
                            UIXmlUtilities.buildXmlFileFromDocument(vDoc, vFilePath);
                            Element vRoot= vDoc.getRootElement();
                            Element vRigheEl= (vRoot!=null?vRoot.element("Righe"):null);
                            if (vRigheEl!=null)
                            {
                                ArrayList<Element> vList= new ArrayList(vRigheEl.elements("Riga"));
                                Iterator<Element> vElIter= vList.iterator();
                                while (vElIter.hasNext())
                                {
                                    Element vElement = (Element) vElIter.next();
                                    String vFld= vElement.attributeValue("Fld", "");
                                    if(vFld.indexOf("|")>-1)
                                    {
                                        String[] vSplit= vFld.split("\\|");
                                        String vCod= vSplit.length>5?vSplit[5]:"";
                                        String vDesc= vSplit.length>6?vSplit[6]:"";
                                        String vA39Row= vCod+"\t"+vDesc+"\t"+"/CLIENTE_"+vCod+" /INDIRIZZO_CLIENTE_"+vCod;
                                        vResp+= "\r\n".concat(vA39Row);
                                    }
                                }
                            }
                        }
                        String vRespText= "Ciao "+vFirstName +" "+vLastName+". Ecco quello che hai chiesto: "+(vResp!=null?(vResp.length()>4096?vResp.substring(0, 4000):vResp):"Risposta nulla") ;
                        try
                        {
                            vRespMsg= new TextMessage(vUserID, new String(vRespText.getBytes(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }                }
                    catch (KeyManagementException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (NoSuchAlgorithmException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (KeyStoreException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    
                }
                else if((isEnablesUserID(vUserID)) && ((vFun).startsWith("CLIENTE ") || (vFun).startsWith("CLIENTE_")))
                {
                    try
                    {
                        String vResp= "";
                        String vCod= vFun.substring(("CLIENTE ").length());
                        String vXmlResp= vClient.httpCall("srv-smens", 29900, "F(EXB;X1BASE_03;DAT) 1(CN;CLI;"+vCod+") 2(;;) P(Hlp(Yes) Com(Yes))", "");
                        String vFilePath= "c:\\temp\resp"+System.currentTimeMillis()+".xml";
                        Document vDoc= UIXmlUtilities.buildDocumentFromXmlString(vXmlResp);
                        if(vDoc!=null)
                        {
                            UIXmlUtilities.buildXmlFileFromDocument(vDoc, vFilePath);
                            Element vRoot= vDoc.getRootElement();
                            Element vRigheEl= (vRoot!=null?vRoot.element("Righe"):null);
                            if (vRigheEl!=null)
                            {
                                ArrayList<Element> vList= new ArrayList(vRigheEl.elements("Riga"));
                                Iterator<Element> vElIter= vList.iterator();
                                while (vElIter.hasNext())
                                {
                                    Element vElement = (Element) vElIter.next();
                                    String vFld= vElement.attributeValue("Fld", "");
                                    if(vFld.indexOf("|")>-1)
                                    {
                                        String[] vSplit= vFld.split("\\|");
                                        String vA39Row= vSplit[1]+"\t"+vSplit[2];
                                        vResp+= "\r\n".concat(vA39Row);
                                    }
                                }
                            }
                        }
                        String vRespText= "Ciao "+vFirstName +" "+vLastName+". Ecco quello che hai chiesto: "+(vResp!=null?(vResp.length()>4096?vResp.substring(0, 4000):vResp):"Risposta nulla") ;
                        try
                        {
                            vRespMsg= new TextMessage(vUserID, new String(vRespText.getBytes(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }                }
                    catch (KeyManagementException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (NoSuchAlgorithmException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (KeyStoreException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    
                }
                else if((isEnablesUserID(vUserID)) && ((vFun).startsWith("INDIRIZZO CLIENTE ") || (vFun).startsWith("INDIRIZZO_CLIENTE_")))
                {
                    try
                    {
                        String vResp= "";
                        String vCod= vFun.substring(("INDIRIZZO CLIENTE ").length());
                        String vXmlResp= vClient.httpCall("srv-smens", 29900, "F(EXB;BRK9CN;CRU) 1(CN;CLI;"+vCod+")", "");
                        String vFilePath= "c:\\temp\resp"+System.currentTimeMillis()+".xml";
                        Document vDoc= UIXmlUtilities.buildDocumentFromXmlString(vXmlResp);
                        if(vDoc!=null)
                        {
                            UIXmlUtilities.buildXmlFileFromDocument(vDoc, vFilePath);
                            Element vRoot= vDoc.getRootElement();
                            Element vRigheEl= (vRoot!=null?vRoot.element("Righe"):null);
                            if (vRigheEl!=null)
                            {
                                ArrayList<Element> vList= new ArrayList(vRigheEl.elements("Riga"));
                                Iterator<Element> vElIter= vList.iterator();
                                while (vElIter.hasNext())
                                {
                                    Element vElement = (Element) vElIter.next();
                                    String vFld= vElement.attributeValue("Fld", "");
                                    if(vFld.indexOf("|")>-1)
                                    {
                                        String[] vSplit= vFld.split("\\|");
                                        if("C. INDIRIZZO".equalsIgnoreCase(vSplit[0]))
                                        {
                                            String vA39Row= (vSplit.length>2?vSplit[2]:"")+":\t"+(vSplit.length>5?vSplit[5]:"");
                                            vResp+= "\r\n".concat(vA39Row);
                                        }
                                    }
                                }
                            }
                        }
                        String vRespText= "Ciao "+vFirstName +" "+vLastName+". Ecco quello che hai chiesto: "+(vResp!=null?(vResp.length()>4096?vResp.substring(0, 4000):vResp):"Risposta nulla") ;
                        try
                        {
                            vRespMsg= new TextMessage(vUserID, new String(vRespText.getBytes(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }                }
                    catch (KeyManagementException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (NoSuchAlgorithmException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (KeyStoreException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    
                }
                else if((isEnablesUserID(vUserID)) && ((vFun).startsWith("FISCALE CLIENTE ") || (vFun).startsWith("FISCALE_CLIENTE_")))
                {
                    try
                    {
                        String vResp= "";
                        String vCod= vFun.substring(("FISCALE CLIENTE ").length());
                        String vXmlResp= vClient.httpCall("srv-smens", 29900, "F(EXB;BRK9CN;CRU) 1(CN;CLI;"+vCod+")", "");
                        String vFilePath= "c:\\temp\resp"+System.currentTimeMillis()+".xml";
                        Document vDoc= UIXmlUtilities.buildDocumentFromXmlString(vXmlResp);
                        if(vDoc!=null)
                        {
                            UIXmlUtilities.buildXmlFileFromDocument(vDoc, vFilePath);
                            Element vRoot= vDoc.getRootElement();
                            Element vRigheEl= (vRoot!=null?vRoot.element("Righe"):null);
                            if (vRigheEl!=null)
                            {
                                ArrayList<Element> vList= new ArrayList(vRigheEl.elements("Riga"));
                                Iterator<Element> vElIter= vList.iterator();
                                while (vElIter.hasNext())
                                {
                                    Element vElement = (Element) vElIter.next();
                                    String vFld= vElement.attributeValue("Fld", "");
                                    if(vFld.indexOf("|")>-1)
                                    {
                                        String[] vSplit= vFld.split("\\|");
                                        if(vSplit[0].toUpperCase().startsWith("F. RIFERIMENTI FISCALI"))
                                        {
                                            String vA39Row= (vSplit.length>2?vSplit[2]:"")+":\t"+(vSplit.length>5?vSplit[5]:"");
                                            vResp+= "\r\n".concat(vA39Row);
                                        }
                                    }
                                }
                            }
                        }
                        String vRespText= "Ciao "+vFirstName +" "+vLastName+". Ecco quello che hai chiesto: "+(vResp!=null?(vResp.length()>4096?vResp.substring(0, 4000):vResp):"Risposta nulla") ;
                        try
                        {
                            vRespMsg= new TextMessage(vUserID, new String(vRespText.getBytes(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }                }
                    catch (KeyManagementException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (NoSuchAlgorithmException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (KeyStoreException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    
                }
                else if((isEnablesUserID(vUserID)) && ((vFun).startsWith("CONTABILE CLIENTE ") || (vFun).startsWith("CONTABILE_CLIENTE_")))
                {
                    try
                    {
                        String vResp= "";
                        String vCod= vFun.substring(("CONTABILE CLIENTE ").length());
                        String vXmlResp= vClient.httpCall("srv-smens", 29900, "F(EXB;BRK9CN;CRU) 1(CN;CLI;"+vCod+")", "");
                        String vFilePath= "c:\\temp\resp"+System.currentTimeMillis()+".xml";
                        Document vDoc= UIXmlUtilities.buildDocumentFromXmlString(vXmlResp);
                        if(vDoc!=null)
                        {
                            UIXmlUtilities.buildXmlFileFromDocument(vDoc, vFilePath);
                            Element vRoot= vDoc.getRootElement();
                            Element vRigheEl= (vRoot!=null?vRoot.element("Righe"):null);
                            if (vRigheEl!=null)
                            {
                                ArrayList<Element> vList= new ArrayList(vRigheEl.elements("Riga"));
                                Iterator<Element> vElIter= vList.iterator();
                                while (vElIter.hasNext())
                                {
                                    Element vElement = (Element) vElIter.next();
                                    String vFld= vElement.attributeValue("Fld", "");
                                    if(vFld.indexOf("|")>-1)
                                    {
                                        String[] vSplit= vFld.split("\\|");
                                        if(vSplit[0].toUpperCase().startsWith("G. RIFERIMENTI CONTABILI"))
                                        {
                                            String vA39Row= (vSplit.length>2?vSplit[2]:"")+":\t"+(vSplit.length>5?vSplit[5]:"");
                                            vResp+= "\r\n".concat(vA39Row);
                                        }
                                    }
                                }
                            }
                        }
                        String vRespText= "Ciao "+vFirstName +" "+vLastName+". Ecco quello che hai chiesto: "+(vResp!=null?(vResp.length()>4096?vResp.substring(0, 4000):vResp):"Risposta nulla") ;
                        try
                        {
                            vRespMsg= new TextMessage(vUserID, new String(vRespText.getBytes(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }                }
                    catch (KeyManagementException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (NoSuchAlgorithmException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (KeyStoreException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    
                }
                else if("START".equalsIgnoreCase(vFun))
                {
                    try
                    {
                        String vResp="";
                        String vXmlResp= vClient.httpCall("srv-smens", 29900, "FUN_LIST_XML", "");
                        String vFilePath= "c:\\temp\resp"+System.currentTimeMillis()+".xml";
                        Document vDoc= UIXmlUtilities.buildDocumentFromXmlString(vXmlResp);
                        if(vDoc!=null)
                        {
                            UIXmlUtilities.buildXmlFileFromDocument(vDoc, vFilePath);
                            Element vRoot= vDoc.getRootElement();
                            Element vRigheEl= (vRoot!=null?vRoot.element("Righe"):null);
                            if (vRigheEl!=null)
                            {
                                ArrayList<Element> vList= new ArrayList(vRigheEl.elements("Riga"));
                                Iterator<Element> vElIter= vList.iterator();
                                while (vElIter.hasNext())
                                {
                                    Element vElement = (Element) vElIter.next();
                                    String vFld= vElement.attributeValue("Fld", "");
                                    if(vFld.indexOf("|")>-1)
                                    {
                                        String vA39Command= vFld.substring(0, vFld.indexOf("|"));
                                        vResp+= "\r\n".concat("/"+vA39Command);
                                    }
                                }
                            }
                        }
                        String vRespText= "Ciao "+vFirstName +" "+vLastName+". Ecco quello che hai chiesto: "+(vResp!=null?(vResp.length()>4096?vResp.substring(0, 4000):vResp):"Risposta nulla") ;
                        try
                        {
                            vRespMsg= new TextMessage(vUserID, new String(vRespText.getBytes(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }                    
                    }
                    catch (KeyManagementException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (NoSuchAlgorithmException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (KeyStoreException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    
                }
                else
                {
                    try
                    {
                        String vResp= vClient.httpCall("srv-smens", 29900, vFun, "");
                        String vFilePath= "c:\\temp\resp"+System.currentTimeMillis()+".xml";
                        UIXmlUtilities.buildXmlFileFromDocument( UIXmlUtilities.buildDocumentFromXmlString(vResp), vFilePath);
                        String vRespText= "Ciao "+vFirstName +" "+vLastName+". Ecco quello che hai chiesto: "+(vResp!=null?(vResp.length()>4096?vResp.substring(0, 4000):vResp):"Risposta nulla") ;
                        try
                        {
                            vRespMsg= new TextMessage(vUserID, new String(vRespText.getBytes(), "UTF-8"));
                        }
                        catch (UnsupportedEncodingException ex)
                        {
                            // TODO Auto-generated catch block
                            ex.printStackTrace();
                        }
                        
                    }
                    catch (KeyManagementException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (NoSuchAlgorithmException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                    catch (KeyStoreException ex)
                    {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                }
            }
            else if(MessageType.IMAGE_MESSAGE.compareTo(vMessageType)==0)
            {
                try
                {
                    String vRet= "";
                    System.out.println(vReqMessage.getClass().getCanonicalName());
                    de.vivistra.telegrambot.model.PhotoSize[] vDoc= (de.vivistra.telegrambot.model.PhotoSize[]) vReqMessage;
                    for( int vI = 0; vI < vDoc.length; vI++)
                    {
                        PhotoSize vPhotoSize = vDoc[vI];
                        String vFileId= vPhotoSize.getFileId();
                        Integer vFileHeight= vPhotoSize.getHeight();
                        Integer vFileSize= vPhotoSize.getFileSize();
                        Integer vFileWidth= vPhotoSize.getWidth();
                        vRet+= "Immagine ricevuta Id: "+vFileId+", Height: "+vFileHeight+", Size: "+vFileSize+", Width: "+vFileWidth+"\r\n";
                    }
                    vRespMsg= new TextMessage(vUserID, new String(vRet.getBytes(), "UTF-8"));
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }
            else if(MessageType.DOCUMENT_MESSAGE.compareTo(vMessageType)==0)
            {
                try
                {
                    System.out.println(vReqMessage.getClass().getCanonicalName());
                    de.vivistra.telegrambot.model.Document vDoc= (de.vivistra.telegrambot.model.Document) vReqMessage;
                    String vFileId= vDoc.getFileId();
                    String vFileName= vDoc.getFileName();
                    Integer vFileSize= vDoc.getFileSize();
                    String vMimeType= vDoc.getMimeType();
                    vRespMsg= new TextMessage(vUserID, new String(("Documento ricevuto Id: "+vFileId+", Name: "+vFileName+", Size: "+vFileSize+", Mime: "+vMimeType).getBytes(), "UTF-8"));
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                
            }
            else if(MessageType.LOCATION_MESSAGE.compareTo(vMessageType)==0)
            {
                try
                {
                    de.vivistra.telegrambot.model.Location vDoc= (de.vivistra.telegrambot.model.Location) vReqMessage;
                    System.out.println(vReqMessage.getClass().getCanonicalName());
                    vRespMsg= new TextMessage(vUserID, new String(("Localizzazione ricevuta. Long: "+vDoc.getLongitude()+", Lat: "+vDoc.getLatitude()).getBytes(), "UTF-8"));
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                
            }
            else if(MessageType.CONTACT_MESSAGE.compareTo(vMessageType)==0)
            {
                try
                {
                    de.vivistra.telegrambot.model.Contact vDoc= (de.vivistra.telegrambot.model.Contact) vReqMessage;
                    System.out.println(vReqMessage.getClass().getCanonicalName());
                    vRespMsg= new TextMessage(vUserID, new String(("Contatto ricevuto. FirstName: "+vDoc.getFirstname()+", Phone: "+vDoc.getPhone()).getBytes(), "UTF-8"));
                }
                catch(UnsupportedEncodingException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
                
            }
            else
            {
                vRespMsg= new TextMessage(vUserID, "Richiesta non supportata");
                
            }
            
        }
        else
        {
            vRespMsg= new TextMessage(vUserID, "Client "+vUserID+" non abilitato");
        }
        Sender.send(vRespMsg);
    }
}
