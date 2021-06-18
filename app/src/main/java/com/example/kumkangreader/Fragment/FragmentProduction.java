package com.example.kumkangreader.Fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.MainActivity2;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

public class FragmentProduction extends Fragment implements BaseActivityInterface {
    TextInputEditText edtScan;
    Context context;

    public FragmentProduction(){

    }

    public FragmentProduction(Context context){
        this.context=context;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout2, container, false);
        this.edtScan=rootView.findViewById(R.id.edtScan);
        //this.edtScan.setText("WP0-210520-001/WP-S01");
        this.edtScan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edtScan.setGravity(Gravity.START);
                }
                else{
                    edtScan.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        });

        this.edtScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO

                        judgeItemTagOrWorksOrder(v.getText().toString());
                }
                return false;
            }
        });


        return rootView;
    }

    /**
     * 바로 실적 등록 가능한 태그임을 판별한다. 아닐 시, 작지 태그로 간주 후 처리
     * 바로 실적 등록 가능한 태그 : 1
     * 아닌 경우 : 2
     * @param scanResult
     */
    public void judgeItemTagOrWorksOrder(String scanResult){
        String url=getString(R.string.service_address) + "judgeItemTagOrWorksOrder";
        ContentValues values = new ContentValues();
        values.put("ScanInput", scanResult);
        JudgeItemTagOrWorksOrder gsom = new JudgeItemTagOrWorksOrder(url, values, scanResult);
        gsom.execute();
    }

    public class JudgeItemTagOrWorksOrder extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String scanResult;
        JudgeItemTagOrWorksOrder(String url, ContentValues values, String scanResult){
            this.url = url;
            this.values = values;
            this.scanResult=scanResult;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등의 행위
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
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                String type="";

                JSONObject child = jsonArray.getJSONObject(0);

                if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                    ErrorCheck = child.getString("ErrorCheck");
                    Toast.makeText(getContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                    return;
                }
                type = child.getString("Type");

                if(type.equals("1")){
                    ((MainActivity2)(context)).getProductionBasicInfo(child.getString("WorksOrderNo")+"/"+child.getString("CostCenter"), child.getString("ItemTag"));
                }
                else{
                    ((MainActivity2)(context)).getProductionBasicInfo(scanResult, "");
                }
            }
            catch (ArrayIndexOutOfBoundsException aoe){
                Toast.makeText(context, "올바른 발주서 태그가 아닙니다.", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
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
        ApplicationClass.getInstance().progressON((Activity)getContext(), null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity)getContext(), message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }
}