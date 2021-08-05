package com.example.kumkangreader.Interface;

import android.content.Context;

/**
 * 엑티비티가 아닌 Dialog등. 다른곳에서 쓰기 위함
 */
public interface BaseActivityInterface{
    public int checkTagState(String tag);


    void progressON();

    void progressON(String message);

    void progressOFF();

    void showErrorDialog(Context context, String message, int type);
}
