package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kumkangreader.Adapter.StockOutDetailAdapter;
import com.example.kumkangreader.Object.StockOut;
import com.example.kumkangreader.Object.StockOutDetail;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 신규 출고시스템 20210517
 */
public class ActivityStockOutNew extends BaseActivity{

    int mode;
    TextView txtMode;
    TextView txtInstructQty;
    TextView txtStockOutQty;
    TextView txtInfo;
    ImageView imvQR;
    TextInputEditText edtScan;
    ListView listViewStockOut;
    StockOut stockOut;


    ArrayList<StockOutDetail> stockOutDetailArrayList;//출고(지시)디테일리스트
    StockOutDetailAdapter instructionAdapter;//지시 어뎁터
    String lastPart;//마지막에 추가된 품목,세부규격

    public void startProgress() {
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
        setContentView(R.layout.activity_stockout_new);
        this.txtMode=findViewById(R.id.txtMode);
        this.txtInstructQty=findViewById(R.id.txtInstructQty);
        this.txtStockOutQty=findViewById(R.id.txtStockOutQty);
        this.txtInfo=findViewById(R.id.txtInfo);
        this.imvQR=findViewById(R.id.imvQR);
        this.edtScan=findViewById(R.id.edtScan);
        this.listViewStockOut=findViewById(R.id.listViewStockOut);
        this.mode=1;
        this.stockOut=(StockOut)getIntent().getSerializableExtra("stockOut");

        this.txtInfo.setText(this.stockOut.CustomerLocation+"/"+this.stockOut.AreaCarNumber);
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
                    ActionScan("EI-"+v.getText().toString());
                }
                return false;
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getStockOut("");
    }

    private void getStockOut(String lastPart) {
        String url = getString(R.string.service_address) + "getStockOut";
        ContentValues values = new ContentValues();
        values.put("StockOutNo", this.stockOut.StockOutNo);
        GetStockOut gsod = new GetStockOut(url, values);
        gsod.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            }
            else {
                String scanResult;
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //Intent i = new Intent(getBaseContext(), ActivityStockOut.class);
                scanResult=result.getContents();
                ActionScan(scanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 버튼 클릭
     */
    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.imvQR:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
                // intentIntegrator.setCaptureActivity(QRReaderActivityStockOutMaster.class);
                intentIntegrator.setPrompt("제품 QR코드를 인식하여 주세요.");
                intentIntegrator.initiateScan();
                break;

         /*   case R.id.btnInput:
                ActionScan("EI-"+edtInput.getText().toString());
                break;*/

        }
    }


    public final Handler dataHandler = new Handler() {

        public void handleMessage(Message msg) {
            int test=4;
            test=3;
            switch (msg.what) {
            }
        }
    };

    public void ActionScan(String itemTag){

        if(itemTag.equals("1")){//입력모드
            this.mode=1;
            this.txtMode.setText("입력모드");
            //this.txtMode.setTextColor(Color.parseColor("#FFEB3B"));
        }
        else if(itemTag.equals("2")){//삭제모드
            this.mode=2;
            this.txtMode.setText("삭제모드");
            //this.txtMode.setTextColor(Color.RED);
        }
        else if(itemTag.equals("3")){//완료
            this.mode=3;
            this.finish();
        }
        else{
            if(this.mode==1) {//입력
                /*String url=getString(R.string.service_address) + "setStockOut";
                ContentValues values = new ContentValues();
                values.put("StockOutNo", stockOut.StockOutNo);
                values.put("ScanInput", itemTag);
                values.put("PhoneNumber", Users.PhoneNumber);
                SetItemTag sit = new SetItemTag(url, values);
                sit.execute();*/
                //checkTagState(itemTag);
                setStockOut(itemTag);
            }
            else if(this.mode==2){//삭제
                /*String url=getString(R.string.service_address) + "deleteItemTag";
                ContentValues values = new ContentValues();
                values.put("StockOutNo", stockOut.StockOutNo);
                values.put("ScanInput", itemTag);
                values.put("PhoneNumber", Users.PhoneNumber);
                ActivityStockOut.DeleteItemTag dit = new ActivityStockOut.DeleteItemTag(url, values);
                dit.execute();*/

                deleteStockOut(itemTag);
            }
            else if(this.mode==3){//완료(닫기)
                this.finish();
            }
        }
    }

    private void setStockOut(String itemTag) {
        String url = getString(R.string.service_address) + "setStockOut";
        ContentValues values = new ContentValues();
        values.put("StockOutNo", this.stockOut.StockOutNo);
        values.put("ItemTag", itemTag);
        values.put("UserCode", Users.PhoneNumber);
        SetStockOut gsod = new SetStockOut(url, values);
        gsod.execute();
    }

    private void deleteStockOut(String itemTag) {
        String url = getString(R.string.service_address) + "deleteStockOut";
        ContentValues values = new ContentValues();
        values.put("StockOutNo", this.stockOut.StockOutNo);
        values.put("ItemTag", itemTag);
        values.put("UserCode", Users.PhoneNumber);
        DeleteStockOut gsod = new DeleteStockOut(url, values);
        gsod.execute();
    }

    public class SetStockOut extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        SetStockOut(String url, ContentValues values){
            this.url = url;
            this.values = values;
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
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    if(!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck=child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    lastPart=child.getString("PartCode")+"-"+child.getString("PartSpec");
                }
                getStockOut(lastPart);
                Toast.makeText(getBaseContext(), "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }


    public class DeleteStockOut extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        DeleteStockOut(String url, ContentValues values){
            this.url = url;
            this.values = values;
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
                StockOutDetail stockOutDetail;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck="";
                stockOutDetailArrayList= new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    if(!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck=child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    stockOutDetail = new StockOutDetail(child.getString("PartCode"),child.getString("PartSpec"),child.getString("PartName"),
                            child.getString("PartSpecName"),child.getString("OutQty"), child.getString("ScanQty"));

                    stockOutDetailArrayList.add(stockOutDetail);
                }

                Toast.makeText(getBaseContext(), "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                getStockOut("");

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }

    /**
     * 제품태그를 스캔하여, 제품을 등록한다.
     * 1. 제품 태그여야만 한다.
     * 2. 지시에서 등록되어 있는 제품이 아닐 시, 등록 불가
     * 3. 이미 찍은 제품 코드면, 등록 불가 (삭제 후, 등록하여야 한다.)
     * 4. 이미 등록되어 있는 수량과 합하여 초과시 메세지 출력, 등록은 가능
     *
     * 등록이 완료되면 다시 스캔 데이터 읽어서 어뎁터 새로고침
     */
    /*public class SetItemTag extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        SetItemTag(String url, ContentValues values){
            this.url = url;
            this.values = values;
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
            try {
                //Log.i("ReadJSONFeedTask", result);
                StockOutDetail stockOutDetail;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck="";

                JSONObject child = jsonArray.getJSONObject(0);

                if(!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                    ErrorCheck=child.getString("ErrorCheck");
                    Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                    return;
                }

                String url=getString(R.string.service_address) + "getStockOutDetailAndScanData";
                ContentValues values = new ContentValues();
                values.put("ScanInput", stockOut.StockOutNo);
                ActivityStockOut.GetStockOutDetailAndScanData gsod = new ActivityStockOut.GetStockOutDetailAndScanData(url, values);
                gsod.execute();

                *//*scanDataArrayList.add(stockOutDetail);
                inputAdapter.notifyDataSetChanged();
                listViewInput.setSelection(inputAdapter.getCount()-1);*//*
                //getViewByPosition(0, listViewInput).setBackgroundColor(Color.YELLOW);

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }*/

    public class GetStockOut extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        GetStockOut(String url, ContentValues values){
            this.url = url;
            this.values = values;
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
                StockOutDetail stockOutDetail;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck="";
                stockOutDetailArrayList= new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    if(!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck=child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    stockOutDetail = new StockOutDetail(child.getString("PartCode"),child.getString("PartSpec"),child.getString("PartName"),
                            child.getString("PartSpecName"),child.getString("OutQty"), child.getString("ScanQty"));

                    stockOutDetailArrayList.add(stockOutDetail);
                }

                instructionAdapter= new StockOutDetailAdapter(ActivityStockOutNew.this, R.layout.listview_stockout_detail_row, stockOutDetailArrayList, lastPart, stockOut.StockOutNo);
                listViewStockOut.setAdapter(instructionAdapter);
                listViewStockOut.setSelection(stockOutDetailArrayList.size()-1);

                //listViewStockOut.setSelection(instructionAdapter.lastPosition);

                /*if(mode==2)
                    Toast.makeText(ActivityStockOutNew.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();*/


                int totalQty=0;
                int totalScanQty=0;

                for(int j=0;j<stockOutDetailArrayList.size();j++){
                    totalQty+=Integer.parseInt(stockOutDetailArrayList.get(j).OutQty);
                    totalScanQty+=Integer.parseInt(stockOutDetailArrayList.get(j).ScanQty);
                }

                txtInstructQty.setText("지시("+totalQty+" EA)");
                txtStockOutQty.setText("출고("+totalScanQty+" EA)");


                edtScan.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }


   /* public class GetStockOutDetailAndScanData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        GetStockOutDetailAndScanData(String url, ContentValues values){
            this.url = url;
            this.values = values;
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
                StockOutDetail stockOutDetail;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck="";
                stockOutDetailArrayList= new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    if(!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck=child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    stockOutDetail = new StockOutDetail(child.getString("PartCode"),child.getString("PartSpec"),child.getString("PartName"),
                            child.getString("PartSpecName"),child.getString("OutQty"), child.getString("ScanQty"));

                    stockOutDetailArrayList.add(stockOutDetail);
                }
                *//*inputAdapter.notifyDataSetChanged();*//*

                instructionAdapter= new StockOutDetailAdapter(ActivityStockOut.this, R.layout.listview_stockout_detail_row, stockOutDetailArrayList, lastPart, stockOut.StockOutNo);
                listViewInstruction.setAdapter(instructionAdapter);
                listViewInstruction.setSelection(instructionAdapter.lastPosition);

                if(mode==2)
                    Toast.makeText(ActivityStockOut.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();


                int totalQty=0;
                int totalScanQty=0;

                for(int j=0;j<stockOutDetailArrayList.size();j++){
                    totalQty+=Integer.parseInt(stockOutDetailArrayList.get(j).OutQty);
                    totalScanQty+=Integer.parseInt(stockOutDetailArrayList.get(j).ScanQty);
                }

                txtTotalQty.setText("총지시량("+totalQty+" EA)");
                txtTotalScanQty.setText("총출고량("+totalScanQty+" EA)");


                edtInput.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }*/



    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}