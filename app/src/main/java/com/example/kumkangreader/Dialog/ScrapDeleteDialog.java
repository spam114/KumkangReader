package com.example.kumkangreader.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.R;

public class ScrapDeleteDialog extends Dialog implements BaseActivityInterface {

    Context context;
    Button btnScrap;
    Button btnDelete;
    Button btnCancel;

    public ScrapDeleteDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_delete_dialog);
        WindowManager.LayoutParams lp = getWindow().getAttributes( ) ;
        WindowManager wm = ((WindowManager)context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE)) ;

        this.btnScrap=findViewById(R.id.btnScrap);
        this.btnDelete=findViewById(R.id.btnDelete);
        this.btnCancel=findViewById(R.id.btnCancel);

        lp.width =  (int)( wm.getDefaultDisplay().getWidth( ) * 0.8 );
        lp.height =  (int)( wm.getDefaultDisplay().getWidth( ) * 0.5 );
        getWindow().setAttributes( lp ) ;
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
}