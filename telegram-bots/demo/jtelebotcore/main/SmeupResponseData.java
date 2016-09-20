package jtelebotcore.main;

import io.github.nixtabyte.telegram.jtelebot.response.json.CustomReplyKeyboard;

public class SmeupResponseData
{
    String iText= "";
    CustomReplyKeyboard iKeyboard= null;

    public SmeupResponseData(String aText, CustomReplyKeyboard aKeyBoard)
    {
        iText=aText;
        iKeyboard= aKeyBoard;
    }

    public String getText()
    {
        return iText;
    }

    public void setText(String aText)
    {
        iText = aText;
    }

    public CustomReplyKeyboard getKeyboard()
    {
        return iKeyboard;
    }

    public void setKeyboard(CustomReplyKeyboard aKeyboard)
    {
        iKeyboard = aKeyboard;
    }

}
