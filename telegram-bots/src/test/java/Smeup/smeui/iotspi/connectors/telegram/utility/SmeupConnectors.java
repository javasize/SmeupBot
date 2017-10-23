package Smeup.smeui.iotspi.connectors.telegram.utility;

import Smeup.smeui.loa39.utility.A39ConnectionPool;

public class SmeupConnectors
{

    
//    public static A39ConnectionPool CLIENT_SRVAMM= new A39ConnectionPool("srv-smens.smeup.com", 29900, "srvamm.smeup.com", "LO_SRVF", "LOOCSERVER", "GES_10ADV", false, 2, 30);
//    public static A39ConnectionPool CLIENT_SRVLAB01= new A39ConnectionPool("srv-smens.smeup.com", 29900, "srvlab01.smeup.com", "LO_SRV", "cffc0qr6kc", "GES_DEMO", false, 2, 30);
//    public static A39ConnectionPool CLIENT_SRVAMM= new A39ConnectionPool("w2016tst01.lab.smeup.com", 29900, "srvamm.smeup.com", "LO_SRVF", "LOOCSERVER", "GES_10ADV", false, 2, 30);
//    public static A39ConnectionPool CLIENT_SRVLAB01= new A39ConnectionPool("w2016tst01.lab.smeup.com", 29900, "srvlab01.smeup.com", "LO_SRV", "cffc0qr6kc", "GES_DEMO", false, 2, 30);
//    public static A39ConnectionPool CLIENT_SRVAMM= new A39ConnectionPool("provider.smeup.com", 80, "srvamm.smeup.com", "PRVC01", "woda8y4764", "GES_10ADV", false, 2, 30);
//    public static A39ConnectionPool CLIENT_SRVLAB01= new A39ConnectionPool("provider.smeup.com", 80, "srvlab01.smeup.com", "PRVL01", "at51kmj62a", "GES_DEMO", false, 2, 30);
    public static A39ConnectionPool CLIENT_SRVAMM= new A39ConnectionPool("provider.smeup.com", 443, "srvamm.smeup.com", "PRVC01", "woda8y4764", "GES_10ADV", true, 2, 30);
    public static A39ConnectionPool CLIENT_SRVLAB01= new A39ConnectionPool("provider.smeup.com", 443, "srvlab01.smeup.com", "PRVL01", "at51kmj62a", "GES_DEMO", true, 2, 30);
    
    public static void init()
    {
        try
        {
            CLIENT_SRVAMM.preparePool();
        }
        catch(Exception ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        try
        {
            CLIENT_SRVLAB01.preparePool();
        }
        catch(Exception ex)
        {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

}
