package Smeup.smeui.iotspi.connectors.telegram.utility;

import Smeup.smeui.loa39.utility.A39ConnectionPool;

public class A39ConnectionData
{
    String iA39Endpoint= null;
    int iTCPPort= 80;
    String iASSystem= null;
    String iUser= null;
    String iPassword= null;
    String iEnv= null;
    boolean iIsHTTPS= false;
    int iPoolSize= 1;
    int iTimeout= 30;
    
    public String getA39Endpoint()
    {
        return iA39Endpoint;
    }
    public void setA39Endpoint(String aA39Endpoint)
    {
        iA39Endpoint = aA39Endpoint;
    }
    public int getTCPPort()
    {
        return iTCPPort;
    }
    public void setTCPPort(int aTCPPort)
    {
        iTCPPort = aTCPPort;
    }
    public String getASSystem()
    {
        return iASSystem;
    }
    public void setASSystem(String aASSystem)
    {
        iASSystem = aASSystem;
    }
    public String getUser()
    {
        return iUser;
    }
    public void setUser(String aUser)
    {
        iUser = aUser;
    }
    public String getPassword()
    {
        return iPassword;
    }
    public void setPassword(String aPassword)
    {
        iPassword = aPassword;
    }
    public String getEnv()
    {
        return iEnv;
    }
    public void setEnv(String aEnv)
    {
        iEnv = aEnv;
    }
    public boolean isIsHTTPS()
    {
        return iIsHTTPS;
    }
    public void setIsHTTPS(boolean aIsHTTPS)
    {
        iIsHTTPS = aIsHTTPS;
    }
    public int getPoolSize()
    {
        return iPoolSize;
    }
    public void setPoolSize(int aPoolSize)
    {
        iPoolSize = aPoolSize;
    }
    public int getTimeout()
    {
        return iTimeout;
    }
    public void setTimeout(int aTimeout)
    {
        iTimeout = aTimeout;
    }

}
