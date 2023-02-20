package com.example.kumkangreader.Interface;

import android.content.Context;
import android.os.Handler;

/**
 * 엑티비티가 아닌 Dialog등. 다른곳에서 쓰기 위함
 */
public interface BaseActivityInterface{
    public int checkTagState(String tag);


    void progressON();

    void progressON(String message);

    void progressON(String message, Handler handler);

    void progressOFF();

    void progressOFF2();

    void showErrorDialog(Context context, String message, int type);
}
