package com.example.kumkangreader.Activity;

import android.content.Context;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kumkangreader.Application.ApplicationClass;

public class BaseActivity extends AppCompatActivity {

    public int checkTagState(String tag) {
        return ApplicationClass.getInstance().checkTagState(tag);
    }

    public void progressON() {
        ApplicationClass.getInstance().progressON(this, null);
    }

    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(this, message);
    }

    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON(this, message, handler);
    }

    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    public void progressOFF2() {
        ApplicationClass.getInstance().progressOFF2();
    }

    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }

}