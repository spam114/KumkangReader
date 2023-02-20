package com.example.kumkangreader.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.Object.WorkClass;
import com.example.kumkangreader.R;

import java.util.ArrayList;

public class WorkClassDialog extends Dialog implements BaseActivityInterface {

    Context context;
    Button btnOK;
    RadioGroup radioGroup;
    ArrayList<WorkClass> workClassArrayList;

    public WorkClassDialog(Context context, ArrayList<WorkClass> workClassArrayList) {
        super(context);
        this.context=context;
        this.workClassArrayList=workClassArrayList;
    }

    private void startProgress() {
        progressON("Loading...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_work_class);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE));
        this.btnOK = findViewById(R.id.btnOK);
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.8);
        this.radioGroup= findViewById(R.id.radioGroup);

        SetRadioButton();

        this.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        getWindow().setAttributes(lp);
    }

    private void SetRadioButton() {

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        startProgress();
        for(int i=0;i<this.workClassArrayList.size();i++){

            RadioButton radioButton= new RadioButton(getContext());
            radioButton.setText("  "+this.workClassArrayList.get(i).WorkClassName);
            //radioButton.setTag(workClassArrayList.get(i));
            radioButton.setTextSize(18);
            params.setMargins(0, 0, 0, 40);
            radioButton.setLayoutParams(params);
            final int finalI = i;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Users.WorkClassCode=workClassArrayList.get(finalI).WorkClassCode;
                    Users.WorkClassName=workClassArrayList.get(finalI).WorkClassName;
                }
            });
            if(i==0)
                radioButton.setChecked(true);
            //radioButton.setBackgroundResource(R.drawable.borderline);

            this.radioGroup.addView(radioButton);
        }
        progressOFF();

    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), message, handler);
    }

    @Override
    public void progressOFF2() {
        ApplicationClass.getInstance().progressOFF2();
    }

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }
}
