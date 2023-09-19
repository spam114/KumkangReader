package com.example.kumkangreader.Activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kumkangreader.Adapter.StockOutDetailAdapter;
import com.example.kumkangreader.Constants;
import com.example.kumkangreader.Object.StockOut;
import com.example.kumkangreader.Object.StockOutDetail;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class ActivityStockOut extends BaseActivity{

    private String mCurrentStatus;
    private String mSavedStatus;
    private boolean mIsRegisterReceiver;

    private static final String STATUS_CLOSE = "STATUS_CLOSE";
    private static final String STATUS_OPEN = "STATUS_OPEN";
    private static final String STATUS_TRIGGER_ON = "STATUS_TRIGGER_ON";

    private static final int SEQ_BARCODE_OPEN = 100;
    private static final int SEQ_BARCODE_CLOSE = 200;
    private static final int SEQ_BARCODE_GET_STATUS = 300;
    private static final int SEQ_BARCODE_SET_TRIGGER_ON = 400;
    private static final int SEQ_BARCODE_SET_TRIGGER_OFF = 500;
    private static final int SEQ_BARCODE_SET_PARAMETER = 600;
    private static final int SEQ_BARCODE_GET_PARAMETER = 700;
    private boolean mIsOpened = false;
    private int mBarcodeHandle = -1;

    public static Context mContext;

    int mode;
    TextView txtInfo;
    TextView txtState;
    TextView txtMode;
    TextView txtTotalQty;
    TextView txtTotalScanQty;
    Button btnCamera;
    EditText edtInput;
    ListView listViewInstruction;//지시 리스트뷰
    //ListView listViewInput;//입력 리스트뷰
    StockOutDetailAdapter instructionAdapter;//지시 어뎁터
    //StockOutDetailAdapter inputAdapter;//입력 어뎁터
    StockOut stockOut;
    ArrayList<StockOutDetail> stockOutDetailArrayList;//출고(지시)디테일리스트
    //ArrayList<StockOutDetail> scanDataArrayList;//스캐너로 입력 받은 데이터
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
        setContentView(R.layout.activity_stockout);
        mContext=this;

        this.btnCamera= findViewById(R.id.btnCamera);
        this.txtInfo=findViewById(R.id.txtInfo);
        this.txtState=findViewById(R.id.txtState);
        this.stockOut=(StockOut)getIntent().getSerializableExtra("stockOut");
        this.txtInfo.setText(stockOut.CustomerLocation+"-"+stockOut.AreaCarNumber);
        this.listViewInstruction=findViewById(R.id.listViewInstruction);
        //this.listViewInput= findViewById(R.id.listViewInput);
        this.stockOutDetailArrayList=(ArrayList<StockOutDetail>) getIntent().getSerializableExtra("stockOutDetailArrayList");
        //this.scanDataArrayList=(ArrayList<StockOutDetail>) getIntent().getSerializableExtra("scanDataArrayList");
        this.instructionAdapter= new StockOutDetailAdapter(this, R.layout.listview_stockout_detail_row, stockOutDetailArrayList, stockOut.StockOutNo);
        //this.inputAdapter= new StockOutDetailAdapter(this, R.layout.listview_stockout_detail_row, scanDataArrayList,1, stockOut.StockOutNo);
        this.listViewInstruction.setAdapter(instructionAdapter);
        //this.listViewInput.setAdapter(inputAdapter);
        this.txtMode=findViewById(R.id.txtMode);
        this.txtTotalQty=findViewById(R.id.txtTotalQty);
        int totalQty=getIntent().getIntExtra("totalQty", -1);
        int totalScanQty=getIntent().getIntExtra("totalScanQty", -1) ;
        this.txtTotalQty.setText("총지시량("+totalQty+" EA)");
        this.txtTotalScanQty=findViewById(R.id.txtTotalScanQty);
        this.txtTotalScanQty.setText("총출고량("+totalScanQty+" EA)");
        this.edtInput=findViewById(R.id.edtInput);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.mode=1;
        Scannerinitialize();
        registerReceiver();

    }

    private void Scannerinitialize(){
        mSavedStatus = mCurrentStatus = STATUS_CLOSE;
        mIsRegisterReceiver = false;

        Intent intent = new Intent();
        String action = Constants.ACTION_BARCODE_CLOSE;
        int seq = SEQ_BARCODE_CLOSE;
        if(mCurrentStatus.equals(STATUS_CLOSE)) action = Constants.ACTION_BARCODE_OPEN;
        intent.setAction(action);
        if(mIsOpened) intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
        if(mCurrentStatus.equals(STATUS_CLOSE)) seq = SEQ_BARCODE_OPEN;
        intent.putExtra(Constants.EXTRA_INT_DATA3, seq);
        sendBroadcast(intent);
        if(mCurrentStatus.equals(STATUS_CLOSE))
        {
            mIsOpened = true;
        }
        else
        {
            mIsOpened = false;
        }
    }

    private void registerReceiver()
    {
        if(mIsRegisterReceiver) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_PARAMETER);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS);

        registerReceiver(mReceiver, filter);
        mIsRegisterReceiver = true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int handle = intent.getIntExtra(Constants.EXTRA_HANDLE, 0);
            int seq = intent.getIntExtra(Constants.EXTRA_INT_DATA3, 0);
            if(action.equals(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA))
            {
                byte[] data = intent.getByteArrayExtra(Constants.EXTRA_BARCODE_DECODING_DATA);
                int symbology = intent.getIntExtra(Constants.EXTRA_INT_DATA2, -1);
                String dataResult = "";
                if(data!=null)
                {
                    dataResult = new String(data);
                    if(dataResult.contains("�")) {
                        try {
                            dataResult = new String(data, "Shift-JIS");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    dataResult=dataResult.replaceAll("\n","");

                    ActionScan(dataResult);

                }
            }

            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS))
            {
                if(seq == SEQ_BARCODE_OPEN)
                {
                    mBarcodeHandle = intent.getIntExtra(Constants.EXTRA_HANDLE, 0);
                    mCurrentStatus = STATUS_OPEN;
                }
                else if(seq == SEQ_BARCODE_CLOSE)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(seq == SEQ_BARCODE_GET_STATUS)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(seq == SEQ_BARCODE_SET_TRIGGER_ON) mCurrentStatus = STATUS_TRIGGER_ON;
                else if(seq == SEQ_BARCODE_SET_TRIGGER_OFF) mCurrentStatus = STATUS_OPEN;
                else if(seq == SEQ_BARCODE_SET_PARAMETER)
                {
                }
                else
                {
                }

            }
            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED))
            {
                int result = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0);
                if(result == Constants.ERROR_BARCODE_DECODING_TIMEOUT)
                {
                }
                else if(result == Constants.ERROR_NOT_SUPPORTED)
                {
                }
                else if(result == Constants.ERROR_BARCODE_ERROR_USE_TIMEOUT)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(result == Constants.ERROR_BARCODE_ERROR_ALREADY_OPENED)
                {
                    mCurrentStatus = STATUS_OPEN;
                }
                else if(result == Constants.ERROR_BATTERY_LOW)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(result == Constants.ERROR_NO_RESPONSE)
                {
                    int notiCode = intent.getIntExtra(Constants.EXTRA_INT_DATA3, 0);
                    mCurrentStatus = STATUS_CLOSE;
                }
                else
                {
                }
                if(seq == SEQ_BARCODE_SET_PARAMETER)
                {
                }
            }
            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_PARAMETER))
            {
                int parameter = intent.getIntExtra(Constants.EXTRA_INT_DATA2, -1);
                String value = intent.getStringExtra(Constants.EXTRA_STR_DATA1);
            }
            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS))
            {
                int status = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0);
            }

        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                //Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                showErrorDialog(this, "취소 되었습니다.",1);
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
            case R.id.btnCamera:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
                // intentIntegrator.setCaptureActivity(QRReaderActivityStockOutMaster.class);
                intentIntegrator.setPrompt("제품 QR코드를 인식하여 주세요.");
                intentIntegrator.initiateScan();
                break;

            case R.id.btnInput:
                ActionScan("EI-"+edtInput.getText().toString());
                break;

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
            this.txtMode.setText("-  입력모드");
            this.txtMode.setTextColor(Color.parseColor("#FFEB3B"));
        }
        else if(itemTag.equals("2")){//삭제모드
            this.mode=2;
            this.txtMode.setText("-  삭제모드");
            this.txtMode.setTextColor(Color.RED);
        }
        else if(itemTag.equals("3")){//완료
            this.mode=3;
            this.finish();
        }
        else{
            if(this.mode==1) {//입력
                String url=getString(R.string.service_address) + "setItemTag";
                ContentValues values = new ContentValues();
                values.put("StockOutNo", stockOut.StockOutNo);
                values.put("ScanInput", itemTag);
                values.put("PhoneNumber", Users.UserID);
                SetItemTag sit = new SetItemTag(url, values);
                sit.execute();
            }
            else if(this.mode==2){//삭제
                String url=getString(R.string.service_address) + "deleteItemTag";
                ContentValues values = new ContentValues();
                values.put("StockOutNo", stockOut.StockOutNo);
                values.put("ScanInput", itemTag);
                values.put("PhoneNumber", Users.UserID);
                DeleteItemTag dit = new DeleteItemTag(url, values);
                dit.execute();
            }
            else if(this.mode==3){//완료(닫기)
                this.finish();
            }
        }
    }

    public class DeleteItemTag extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        DeleteItemTag(String url, ContentValues values){
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
                    //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                    showErrorDialog(ActivityStockOut.this, ErrorCheck,2);
                    return;
                }

                String url=getString(R.string.service_address) + "getStockOutDetailAndScanData";
                ContentValues values = new ContentValues();
                values.put("ScanInput", stockOut.StockOutNo);
                GetStockOutDetailAndScanData gsod = new GetStockOutDetailAndScanData(url, values);
                gsod.execute();

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
    public class SetItemTag extends AsyncTask<Void, Void, String> {
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
                    //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                    showErrorDialog(ActivityStockOut.this, ErrorCheck,2);
                    return;
                }
                lastPart=child.getString("PartCode")+"-"+child.getString("PartSpec");

                String url=getString(R.string.service_address) + "getStockOutDetailAndScanData";
                ContentValues values = new ContentValues();
                values.put("ScanInput", stockOut.StockOutNo);
                GetStockOutDetailAndScanData gsod = new GetStockOutDetailAndScanData(url, values);
                gsod.execute();

                /*scanDataArrayList.add(stockOutDetail);
                inputAdapter.notifyDataSetChanged();
                listViewInput.setSelection(inputAdapter.getCount()-1);*/
                //getViewByPosition(0, listViewInput).setBackgroundColor(Color.YELLOW);

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }



    public class GetStockOutDetailAndScanData extends AsyncTask<Void, Void, String> {
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
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityStockOut.this, ErrorCheck,2);
                        return;
                    }

                    stockOutDetail = new StockOutDetail(child.getString("PartCode"),child.getString("PartSpec"),child.getString("PartName"),
                            child.getString("PartSpecName"),child.getString("OutQty"), child.getString("ScanQty"));

                    stockOutDetailArrayList.add(stockOutDetail);
                }
                /*inputAdapter.notifyDataSetChanged();*/

                instructionAdapter= new StockOutDetailAdapter(ActivityStockOut.this, R.layout.listview_stockout_detail_row, stockOutDetailArrayList, lastPart, stockOut.StockOutNo);
                listViewInstruction.setAdapter(instructionAdapter);
                listViewInstruction.setSelection(instructionAdapter.lastPosition);

                if(mode==2) {
                    //Toast.makeText(ActivityStockOut.this, "삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    showErrorDialog(ActivityStockOut.this, "삭제가 완료되었습니다.",1);
                }

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
    }



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


    private void resetCurrentView()
    {
        if(!mSavedStatus.equals(mCurrentStatus))
        {
            if(!mSavedStatus.equals(STATUS_CLOSE))
            {
                Intent intent = new Intent();
                intent.setAction(Constants.ACTION_BARCODE_OPEN);
                intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
                intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_OPEN);
                sendBroadcast(intent);
                return;
            }
        }
    }
    @Override
    protected void onResume() {
        registerReceiver();
        resetCurrentView();
        super.onResume();

    }
    @Override
    protected void onDestroy() {
        destroyEvent();
        super.onDestroy();
        //sdkHandler = null;
    }

    private void destroyEvent()
    {
        if(!mCurrentStatus.equals(STATUS_CLOSE))
        {
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_BARCODE_CLOSE);
            intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
            intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_CLOSE);
            sendBroadcast(intent);
        }
        unregisterReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                btnCamera.performClick();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mSavedStatus = mCurrentStatus;
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_BARCODE_CLOSE);
        intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
        intent.putExtra(Constants.EXTRA_INT_DATA3, SEQ_BARCODE_CLOSE);
        sendBroadcast(intent);
        unregisterReceiver();
        mCurrentStatus = STATUS_CLOSE;
        super.onPause();
    }

    private void unregisterReceiver()
    {
        if(!mIsRegisterReceiver) return;
        unregisterReceiver(mReceiver);
        mIsRegisterReceiver = false;
    }
}
