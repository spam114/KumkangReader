package com.example.kumkangreader.Dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 투입 삭제 다이얼로그
 */

public class ScrapDeleteDialog extends Dialog implements BaseActivityInterface {

    Context context;
    Button btnDelete;
    Button btnCancel;
    String itemTag;
    String worksOrderNo;
    String costCenter;
    Handler mHandler;


    public ScrapDeleteDialog(@NonNull Context context, String itemTag, String worksOrderNo ,String costCenter, Handler mHandler) {
        super(context);
        this.context=context;
        this.itemTag=itemTag;
        this.worksOrderNo=worksOrderNo;
        this.costCenter=costCenter;
        this.mHandler=mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_delete_dialog);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE));

        this.btnDelete = findViewById(R.id.btnDelete);
        this.btnCancel = findViewById(R.id.btnCancel);
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.8);

        this.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteInputData();
                dismiss();
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getWindow().setAttributes(lp);
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

    private void deleteInputData() {
        String url = context.getString(R.string.service_address) + "deleteInputData";
        ContentValues values = new ContentValues();
        values.put("ItemTag", itemTag);
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        values.put("UserCode", Users.UserID);
        DeleteInputData gsod = new DeleteInputData(url, values);
        gsod.execute();
    }


    public class DeleteInputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        DeleteInputData(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
            startProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // 결과가 여기에 담깁니다. 아래 onPostExecute()의 파라미터로 전달됩니다.
        }

        @Override
        protected void onPostExecute(String result) {
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다

            try {
                //OutputData outputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck,2);
                        return;
                    }
                }
                //데이터및 리스트뷰 다시불러오기
                //ActivityProductionPerformance.getInputData("","");
                Message msg= mHandler.obtainMessage();
                mHandler.sendMessage(msg);

                //getInputData("", values.get("ItemTag").toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
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
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }
}