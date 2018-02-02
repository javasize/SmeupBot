package Smeup.smeui.iotspi.connectors.telegram.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import Smeup.smeui.loa39.utility.A39ConnectionPool;

public class SmeupConnectors
{
    public static final File CONNECTIONS_FILE = new File("bot_connections.txt");
    // public static A39ConnectionPool CLIENT_SRVAMM= new
    // A39ConnectionPool("srv-smens.smeup.com", 29900, "srvamm.smeup.com",
    // "LO_SRVF", "LOOCSERVER", "GES_10ADV", false, 2, 30);
    // public static A39ConnectionPool CLIENT_SRVLAB01= new
    // A39ConnectionPool("srv-smens.smeup.com", 29900, "srvlab01.smeup.com",
    // "LO_SRV", "cffc0qr6kc", "GES_DEMO", false, 2, 30);
    // public static A39ConnectionPool CLIENT_SRVAMM= new
    // A39ConnectionPool("w2016tst01.lab.smeup.com", 29900, "srvamm.smeup.com",
    // "LO_SRVF", "LOOCSERVER", "GES_10ADV", false, 2, 30);
    // public static A39ConnectionPool CLIENT_SRVLAB01= new
    // A39ConnectionPool("w2016tst01.lab.smeup.com", 29900,
    // "srvlab01.smeup.com", "LO_SRV", "cffc0qr6kc", "GES_DEMO", false, 2, 30);
    // public static A39ConnectionPool CLIENT_SRVAMM= new
    // A39ConnectionPool("provider.smeup.com", 80, "srvamm.smeup.com", "PRVC01",
    // "woda8y4764", "GES_10ADV", false, 2, 30);
    // public static A39ConnectionPool CLIENT_SRVLAB01= new
    // A39ConnectionPool("provider.smeup.com", 80, "srvlab01.smeup.com",
    // "PRVL01", "at51kmj62a", "GES_DEMO", false, 2, 30);
    // public static A39ConnectionPool CLIENT_SRVAMM = new A39ConnectionPool(
    // "provider.smeup.com", 443, "srvamm.smeup.com", "PRVC01",
    // "woda8y4764", "GES_10ADV", true, 2, 30);
    // public static A39ConnectionPool CLIENT_SRVLAB01 = new A39ConnectionPool(
    // "provider.smeup.com", 443, "srvlab01.smeup.com", "PRVL01",
    // "at51kmj62a", "GES_DEMO", true, 2, 30);

    public static ArrayList<A39ConnectionData> CONNECTOR_DATA_LIST = null;
    public static A39ConnectionPool CLIENT_A39 = null;
    // public static A39ConnectionPool CLIENT_SRVLAB01 = null;

    public static void init()
    {
        CONNECTOR_DATA_LIST = getConnectionsData();

        A39ConnectionData vData = getFirstConnector();
        if(vData != null)
        {
            CLIENT_A39 = new A39ConnectionPool(vData.getA39Endpoint(),
                        vData.getTCPPort(), vData.getASSystem(),
                        vData.getUser(), vData.getPassword(), vData.getEnv(),
                        vData.isIsHTTPS(), vData.getPoolSize(),
                        vData.getTimeout());
            try
            {
                CLIENT_A39.preparePool();
            }
            catch(Exception ex)
            {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
        else
        {
            System.err.println("Manca collegamento al sistema. Bot con operatività limitata");
        }

        // vData= getConnector("srvlab01.smeup.com");
        // CLIENT_SRVLAB01= new A39ConnectionPool(vData.getA39Endpoint(),
        // vData.getTCPPort()
        // , vData.getASSystem(), vData.getUser()
        // , vData.getPassword(), vData.getEnv()
        // , vData.isIsHTTPS(), vData.getPoolSize(), vData.getTimeout());
        // try
        // {
        // CLIENT_SRVLAB01.preparePool();
        // }
        // catch(Exception ex)
        // {
        // // TODO Auto-generated catch block
        // ex.printStackTrace();
        // }
    }

    public static A39ConnectionData getConnector(String aASSystem)
    {
        A39ConnectionData vRet = null;
        if(CONNECTOR_DATA_LIST != null)
        {
            Iterator<A39ConnectionData> vIter = CONNECTOR_DATA_LIST.iterator();
            while(vIter.hasNext())
            {
                A39ConnectionData vA39ConnectionData = (A39ConnectionData) vIter
                            .next();

                if(vA39ConnectionData.getASSystem().equalsIgnoreCase(aASSystem))
                {
                    vRet = vA39ConnectionData;
                    break;
                }
            }
        }
        return vRet;
    }

    public static A39ConnectionData getFirstConnector()
    {
        A39ConnectionData vRet = null;
        if(CONNECTOR_DATA_LIST != null)
        {
            Iterator<A39ConnectionData> vIter = CONNECTOR_DATA_LIST.iterator();
            while(vIter.hasNext())
            {
                A39ConnectionData vA39ConnectionData = (A39ConnectionData) vIter
                            .next();

                if(vA39ConnectionData != null)
                {
                    vRet = vA39ConnectionData;
                    break;
                }
            }
        }
        return vRet;
    }

    public static ArrayList<A39ConnectionData> getConnectionsData()
    {
        ArrayList<A39ConnectionData> vRet = new ArrayList();
        System.out.println("Cerco i dati per le connessioni in "
                    + CONNECTIONS_FILE.getAbsolutePath());
        if(CONNECTIONS_FILE.exists())
        {
            try
            {
                BufferedReader vReader = new BufferedReader(
                            new FileReader(CONNECTIONS_FILE));
                try
                {
                    String vRow = vReader.readLine();
                    while(vRow != null)
                    {
                        String[] vLinePars = vRow.split(";");
                        if(vLinePars != null && vLinePars.length > 8)
                        {
                            A39ConnectionData vData = new A39ConnectionData();
                            vData.setA39Endpoint(vLinePars[0].trim());
                            vData.setTCPPort(Integer
                                        .parseInt(vLinePars[1].trim()));
                            vData.setASSystem(vLinePars[2].trim());
                            vData.setUser(vLinePars[3].trim());
                            vData.setPassword(vLinePars[4].trim());
                            vData.setEnv(vLinePars[5].trim());
                            vData.setIsHTTPS(Boolean
                                        .parseBoolean(vLinePars[6].trim()));
                            vData.setPoolSize(Integer
                                        .parseInt(vLinePars[7].trim()));
                            vData.setTimeout(Integer
                                        .parseInt(vLinePars[8].trim()));
                            vRet.add(vData);
                        }
                        vRow = vReader.readLine();
                    }
                }
                catch(IOException ex)
                {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
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
        }
        return vRet;
    }
}
