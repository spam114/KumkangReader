package com.example.kumkangreader;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kumkangreader.Activity.ActivityStockOut;
import com.example.kumkangreader.Activity.BaseActivity;
import com.example.kumkangreader.Object.StockOut;
import com.example.kumkangreader.Object.StockOutDetail;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 시작과 동시에
 * 1. 휴대폰의 권한을 확인한다.
 * 2. 사용자정보를 확인한다.(생략할수도있음)
 */

public class MainActivity extends BaseActivity {
    private final int REQUEST_STOCKOUT = 1;
    static int connectedScannerID;
    public StockOut stockOut;
    Button btnCamera;
    TextView txtState;
    ArrayList<StockOutDetail> stockOutDetailArrayList;
    ArrayList<StockOutDetail> scanDataArrayList;

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
        setContentView(R.layout.layout1);
        this.btnCamera = findViewById(R.id.btnCamera);
        this.txtState = findViewById(R.id.txtState);
        /*this.txtVersion=findViewById(R.id.txtVersion);*/
        this.stockOutDetailArrayList = new ArrayList<>();
        this.scanDataArrayList = new ArrayList<>();
        /*try {
            txtVersion.setText("version "+getBaseContext().getPackageManager().getPackageInfo(getBaseContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/
    }





    /*private BroadcastReceiver mReceiver = new BroadcastReceiver()
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
                    if(dataResult.contains("�"))
                    {
                        try {
                            dataResult = new String(data, "Shift-JIS");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    String scanResult;
                    scanResult = dataResult.replaceAll("\n","");

                    //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    //Intent i = new Intent(getBaseContext(), ActivityStockOut.class);

                    *//*mHandler = new Handler();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(MainActivity.this, "",
                                    "잠시만 기다려 주세요.", true);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 4000);
                        }
                    });*//*
                    new GetStockOutMaster(scanResult).execute(getString(R.string.service_address) + "getStockOutMaster");// 마스터 가져온 후 -> 디테일 가져온다.

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
    };*/













    /*private void resetCurrentView()
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

    private void unregisterReceiver()
    {
        if(!mIsRegisterReceiver) return;
        unregisterReceiver(mReceiver);
        mIsRegisterReceiver = false;
    }*/

   /* @Override
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
    }*/


   /* private void registerReceiver()
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
    }*/


    /**
     * 버튼 클릭
     */
    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.btnCamera:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
                // intentIntegrator.setCaptureActivity(QRReaderActivityStockOutMaster.class);
                intentIntegrator.setPrompt(getString(R.string.qr_state_stockoutmaster));
                intentIntegrator.initiateScan();
                break;

           /* case R.id.imvPrint:
                String _url=getString(R.string.service_address) + "setPrintOrderData";
                ContentValues _values = new ContentValues();
                _values.put("PCCode", "201");
                _values.put("PrintDivision", "2");
                _values.put("PintNo", "KP-2012260050");
                _values.put("InsertId", Users.PhoneNumber);
                SetPrintOrderData sod = new SetPrintOrderData(_url, _values);
                sod.execute();


                break;

            case R.id.btnTest:
                String url=getString(R.string.service_address) + "getStockOutDetailAndScanData";
                ContentValues values = new ContentValues();
                values.put("ScanInput", "E3-2102230002");
                GetStockOutDetailAndScanData gsod = new GetStockOutDetailAndScanData(url, values);
                gsod.execute();
                break;*/
        }
    }

    public class SetPrintOrderData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        SetPrintOrderData(String url, ContentValues values) {
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
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                JSONObject child = jsonArray.getJSONObject(0);

                if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                    ErrorCheck = child.getString("ErrorCheck");
                    //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                    showErrorDialog(MainActivity.this, ErrorCheck, 2);
                    return;
                }
                //Toast.makeText(getBaseContext(), "출력이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                showErrorDialog(MainActivity.this, "출력이 완료 되었습니다.", 1);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }


        }
    }

    public class GetStockOutDetailAndScanData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetStockOutDetailAndScanData(String url, ContentValues values) {
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
                StockOutDetail stockOutDetail;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(MainActivity.this, ErrorCheck, 2);
                        return;
                    }
                    stockOutDetail = new StockOutDetail(child.getString("PartCode"), child.getString("PartSpec"), child.getString("PartName"),
                            child.getString("PartSpecName"), child.getString("OutQty"), child.getString("ScanQty"));
                    stockOutDetailArrayList.add(stockOutDetail);
                }
                int totalQty = 0;
                int totalScanQty = 0;
                for (int j = 0; j < stockOutDetailArrayList.size(); j++) {
                    totalQty += Integer.parseInt(stockOutDetailArrayList.get(j).OutQty);
                    totalScanQty += Integer.parseInt(stockOutDetailArrayList.get(j).ScanQty);
                }
                Intent i = new Intent(getBaseContext(), ActivityStockOut.class);
                i.putExtra("stockOut", stockOut);
                i.putExtra("stockOutDetailArrayList", stockOutDetailArrayList);
                i.putExtra("totalQty", totalQty);
                i.putExtra("totalScanQty", totalScanQty);
                startActivityForResult(i, REQUEST_STOCKOUT);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_STOCKOUT) {//ActivityStockOut에서 돌아옴
            this.stockOutDetailArrayList = new ArrayList<>();
            this.scanDataArrayList = new ArrayList<>();
        }

        if (result != null) {
            if (result.getContents() == null) {
                //Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                showErrorDialog(this, "취소 되었습니다.", 1);
            } else {
                String scanResult;
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                //Intent i = new Intent(getBaseContext(), ActivityStockOut.class);
                scanResult = result.getContents();

                String url = getString(R.string.service_address) + "getStockOutMaster";
                ContentValues values = new ContentValues();
                values.put("ScanInput", scanResult);
                GetStockOutMaster gsom = new GetStockOutMaster(url, values);
                gsom.execute();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class GetStockOutMaster extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetStockOutMaster(String url, ContentValues values) {
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
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                String StockOutNo = "";
                String CustomerLocation = "";
                String AreaCarNumber = "";

                JSONObject child = jsonArray.getJSONObject(0);

                if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                    ErrorCheck = child.getString("ErrorCheck");
                    //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                    showErrorDialog(MainActivity.this, ErrorCheck, 2);
                    return;
                }
                StockOutNo = child.getString("StockOutNo");
                CustomerLocation = child.getString("CustomerLocation");
                AreaCarNumber = child.getString("AreaCarNumber");

                stockOut = new StockOut();
                stockOut.StockOutNo = StockOutNo;
                stockOut.CustomerLocation = CustomerLocation;
                stockOut.AreaCarNumber = AreaCarNumber;

                String url = getString(R.string.service_address) + "getStockOutDetailAndScanData";
                ContentValues values = new ContentValues();
                values.put("ScanInput", StockOutNo);
                GetStockOutDetailAndScanData gsod = new GetStockOutDetailAndScanData(url, values);
                gsod.execute();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }

    public class GetScanData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetScanData(String url, ContentValues values) {
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
                String ErrorCheck = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(MainActivity.this, ErrorCheck, 2);
                        return;
                    }
                    stockOutDetail = new StockOutDetail(child.getString("PartCode"), child.getString("PartSpec"),
                            child.getString("PartName"), child.getString("PartSpecName"), child.getString("OutQty"), child.getString("ScanQty"));

                    scanDataArrayList.add(stockOutDetail);
                }

                Intent i = new Intent(getBaseContext(), ActivityStockOut.class);

                i.putExtra("stockOut", stockOut);
                i.putExtra("stockOutDetailArrayList", stockOutDetailArrayList);
                i.putExtra("scanDataArrayList", scanDataArrayList);
                startActivityForResult(i, REQUEST_STOCKOUT);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }


    //과거 QR코드 zebra
   /* @Override
    public void dcssdkEventScannerAppeared(DCSScannerInfo dcsScannerInfo) {//스캐너 꽂았을때


        if (sdkHandler == null) {
            SetSDKHandler();
        }

        mScannerInfoList.add(dcsScannerInfo);
        sdkHandler.dcssdkEstablishCommunicationSession(mScannerInfoList.get(0).getScannerID());
        txtState.setText("스캐너 연결(ON)");
    }

    @Override
    public void dcssdkEventScannerDisappeared(int i) {//스캐너 뽑았을때
        txtState.setText("스캐너 연결(OFF)");
        sdkHandler = null;
    }

    @Override
    public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo dcsScannerInfo) {//연결버튼 눌렀을때: sdkHandler.dcssdkEstablishCommunicationSession(mScannerInfoList.get(0).getScannerID()); 실행
        connectedScannerID = dcsScannerInfo.getScannerID();
        //txtResult.append("... Connected");
    }

    @Override
    public void dcssdkEventCommunicationSessionTerminated(int i) {//아마 연결해제?

    }

    *//**
     * 데이터 읽기
     *
     * @param barcodeData
     * @param barcodeType
     * @param fromScannerID
     *//*
    @Override
    public void dcssdkEventBarcode(final byte[] barcodeData, final int barcodeType, final int fromScannerID) {

        //Log.e("stan114", task_info.get(0).topActivity.getClassName());
        String code = new String(barcodeData);
        dataHandler.obtainMessage(Constants.BARCODE_RECEIVED, code).sendToTarget();

    }*/


    /**
     * 읽은 데이터(스캐닝) 처리
     */
    private final Handler dataHandler = new Handler() {


        public void handleMessage(Message msg) {

            ActivityManager activity_manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> task_info = activity_manager.getRunningTasks(9999);
            switch (msg.what) {
                case Constants.BARCODE_RECEIVED:
                    String item = (String) msg.obj;
                    if (task_info.get(0).topActivity.getClassName().equals("com.example.kumkangreader.MainActivity")) {

                        String url = getString(R.string.service_address) + "getStockOutMaster";
                        ContentValues values = new ContentValues();
                        values.put("ScanInput", item);
                        GetStockOutMaster gsom = new GetStockOutMaster(url, values);
                        gsom.execute();
                        break;
                    } else if (task_info.get(0).topActivity.getClassName().equals("com.example.kumkangreader.Activity.ActivityStockOut")) {
                        //((ActivityStockOut) ActivityStockOut.mContext).startProgress();
                        ((ActivityStockOut) ActivityStockOut.mContext).ActionScan(item);
                        break;
                    } else {
                        return;
                    }
            }
        }
    };


    //과거 QR코드 스캐너
/*
    @Override
    public void dcssdkEventImage(byte[] bytes, int i) {
    }

    @Override
    public void dcssdkEventVideo(byte[] bytes, int i) {
    }

    @Override
    public void dcssdkEventBinaryData(byte[] bytes, int i) {
    }

    @Override
    public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent) {
    }

    @Override
    public void dcssdkEventAuxScannerAppeared(DCSScannerInfo dcsScannerInfo, DCSScannerInfo dcsScannerInfo1) {
    }*/


}

