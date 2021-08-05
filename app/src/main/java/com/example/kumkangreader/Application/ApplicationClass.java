package com.example.kumkangreader.Application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.example.kumkangreader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * 공통으로 쓰는 메소드들이 담겨져있다.
 */
public class ApplicationClass extends Application {
    private static ApplicationClass baseApplication;
    AppCompatDialog progressDialog;

    public static ApplicationClass getInstance() {
        return baseApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;
    }

    /**
     * 인터넷 연결 상태를 확인한다.
     *
     * @return
     */
    public boolean checkInternetConnect() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo == null) {
            return false;
        }
        return true;
    }

    /**
     * 스캔한 태그의 종류를 알아낸다.
     * -1: 유효하지 않은 태그
     * 0: 제품
     * 1: 출고
     *
     * @return
     */
    public int checkTagState(String tag) {

        if (tag.substring(0, 2).equals("EI")) {//제품
            return 0;
        } else if (tag.substring(0, 2).equals("E3")) {//출고(상차)
            return 1;
        } else {
            return -1;
        }
    }


    public void progressON(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();
        }
        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void progressSET(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void showErrorDialog(Context context, String message, int type){
        MaterialAlertDialogBuilder alertBuilder= new MaterialAlertDialogBuilder(context, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog);
        if(type==1){
            alertBuilder.setTitle("작업 성공");
        }
        else{
            alertBuilder.setTitle("에러 발생");
        }


        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
