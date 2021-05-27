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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.OutputData;
import com.example.kumkangreader.Object.ScrapCode;
import com.example.kumkangreader.Object.ScrapGrade;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 불량입력 다이얼로그 : 실적(아래쪽)
 */

public class DialogInsertScrap extends Dialog implements BaseActivityInterface {

    TextView txtPartName;
    TextView txtPartSpec;
    TextView txtCoilNo;
    Spinner spinnerScrapGrade;
    Spinner spinnerScrapCode;
    EditText edtQty;
    Button btnOK;
    Button btnCancel;

    Context context;
    OutputData outputData;
    ArrayList<ScrapCode> scrapCodeArrayList;
    ArrayList<ScrapGrade> scrapGradeArrayList;
    Handler mHandler;

    //ScrapDeleteDialog2.OnDialogResult mDialogResult

    public DialogInsertScrap(@NonNull Context context, OutputData outputData, ArrayList<ScrapCode> scrapCodeArrayList, ArrayList<ScrapGrade> scrapGradeArrayList, Handler mHandler) {
        super(context);
        this.context=context;
        this.outputData=outputData;
        this.scrapCodeArrayList=scrapCodeArrayList;
        this.scrapGradeArrayList=scrapGradeArrayList;
        this.mHandler=mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_insert_scrap);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE));
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.8);
        getWindow().setAttributes(lp);

        this.txtPartName=findViewById(R.id.txtPartName);
        this.txtPartSpec=findViewById(R.id.txtPartSpec);
        this.txtCoilNo=findViewById(R.id.txtCoilNo);
        this.spinnerScrapGrade=findViewById(R.id.spinnerScrapGrade);
        this.spinnerScrapCode=findViewById(R.id.spinnerScrapCode);
        this.edtQty=findViewById(R.id.edtQty);
        this.btnOK=findViewById(R.id.btnOK);
        this.btnCancel=findViewById(R.id.btnCancel);

        this.txtPartName.setText(this.outputData.PartName);

        if(!this.outputData.MSpec.equals("")){
            this.txtPartSpec.setText(this.outputData.PartSpecName+"("+this.outputData.MSpec+")");
        }
        else{
            this.txtPartSpec.setText(this.outputData.PartSpecName);
        }
        this.txtCoilNo.setText(this.outputData.CoilNo);

        MakeScrapGradeSpinner();
        MakeScrapCodeSpinner();

        this.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScrap();
            }
        });

        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    private void MakeScrapGradeSpinner() {
        String[] scrapList=new String[this.scrapGradeArrayList.size()];
        for(int i=0;i<this.scrapGradeArrayList.size();i++){
            scrapList[i]=this.scrapGradeArrayList.get(i).ScrapGrade;
        }
        this.spinnerScrapGrade = (Spinner) findViewById(R.id.spinnerScrapGrade);
        ArrayAdapter<String> scrapAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, scrapList);
        this.spinnerScrapGrade.setAdapter(scrapAdapter);
    }

    private void MakeScrapCodeSpinner() {
        String[] scrapList=new String[this.scrapCodeArrayList.size()];
        for(int i=0;i<this.scrapCodeArrayList.size();i++){
            scrapList[i]=this.scrapCodeArrayList.get(i).ScrapCode;
        }
        this.spinnerScrapCode = (Spinner) findViewById(R.id.spinnerScrapCode);
        ArrayAdapter<String> scrapAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, scrapList);
        this.spinnerScrapCode.setAdapter(scrapAdapter);
    }


    private void setScrap() {
        String url = context.getString(R.string.service_address) + "setScrap";
        ContentValues values = new ContentValues();
        values.put("ItemTag", this.outputData.ItemTag);
        values.put("Qty", this.edtQty.getText().toString());
        values.put("ScrapCode", this.spinnerScrapCode.getSelectedItem().toString());
        values.put("ScrapGrade", this.spinnerScrapGrade.getSelectedItem().toString());
        values.put("UserCode", Users.PhoneNumber);
        SetScrap gsod = new SetScrap(url, values);
        gsod.execute();
    }

    public class SetScrap extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        SetScrap(String url, ContentValues values) {
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
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                //기본 Activity에서 초기화 해야함
                //데이터및 리스트뷰 다시불러오기
                //ActivityProductionPerformance.getInputData("","");
                Message msg= mHandler.obtainMessage();
                mHandler.sendMessage(msg);
                dismiss();
                progressOFF();
            }

        }
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
