package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kumkangreader.Adapter.InputAdapter;
import com.example.kumkangreader.Adapter.OutputAdapter;
import com.example.kumkangreader.Object.InputData;
import com.example.kumkangreader.Object.OutputData;
import com.example.kumkangreader.Object.ProductionInfo;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityProductionPerformance extends BaseActivity {
    ArrayList<ProductionInfo> productionInfoArrayList;
    ArrayList<InputData> inputDataArrayList;
    ArrayList<OutputData> outputDataArrayList;
    InputAdapter inputAdapter;//투입 어뎁터
    OutputAdapter outputAdapter;//실적 어뎁터
    TextView txtInputTotal;
    TextView txtOutputTotal;
    TextView txtWorksOrderNo;
    String worksOrderNo;
    String costCenter;
    String costCenterName;
    String locationNo;
    ListView listViewInput;
    ListView listViewOutput;
    ImageView imvQR;

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
        setContentView(R.layout.activity_production_performance);
        this.productionInfoArrayList = (ArrayList<ProductionInfo>) getIntent().getSerializableExtra("productionInfoArrayList");
        this.inputDataArrayList = new ArrayList<>();
        this.outputDataArrayList = new ArrayList<>();
        this.listViewInput = findViewById(R.id.listViewInput);
        this.listViewOutput = findViewById(R.id.listViewOutPut);
        this.worksOrderNo = getIntent().getStringExtra("worksOrderNo");
        this.costCenter = getIntent().getStringExtra("costCenter");
        this.costCenterName = getIntent().getStringExtra("costCenterName");
        this.locationNo = getIntent().getStringExtra("locationNo");
        this.txtInputTotal = findViewById(R.id.txtInputTotal);
        this.txtOutputTotal = findViewById(R.id.txtOutputTotal);
        this.txtWorksOrderNo = findViewById(R.id.txtWorksOrderNo);
        this.txtWorksOrderNo.setText(worksOrderNo + " / " + costCenterName);
        this.txtInputTotal.setText("투입: " + String.format("%.0f", Double.parseDouble(this.productionInfoArrayList.get(0).InputQty)));
        this.txtOutputTotal.setText(",   생산: " + String.format("%.0f", Double.parseDouble(this.productionInfoArrayList.get(0).IssueOutputQty)) + " / "
                + String.format("%.0f", Double.parseDouble(this.productionInfoArrayList.get(0).OutputQty)));
        this.imvQR = findViewById(R.id.imvQR);
        getInputData("","");//투입정보 가져오기

        this.imvQR.setOnClickListener(new View.OnClickListener() {//QR클릭 이벤트
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(ActivityProductionPerformance.this);
                intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
                intentIntegrator.setPrompt(getString(R.string.qr_state_normal));
                // intentIntegrator.setCaptureActivity(QRReaderActivityStockOutMaster.class);
                intentIntegrator.initiateScan();
            }
        });


        //출고

        //생산실적 셋팅


        //mContext=this;

     /*   this.btnCamera= findViewById(R.id.btnCamera);
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
        this.mode=1;*/
     /*   Scannerinitialize();
        registerReceiver();*/

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

      /*  if (requestCode == REQUEST_STOCKOUT) {//ActivityStockOut에서 돌아옴
        }*/

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                String scanResult;
                scanResult = result.getContents();
                judgeInputOutput(scanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void getInputData(String itemTag, String itemTag2) {
        String url = getString(R.string.service_address) + "getInputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetInputData gsod = new GetInputData(url, values, itemTag, itemTag2);
        gsod.execute();
    }

    private void getOutputData(String itemTag) {
        String url = getString(R.string.service_address) + "getOutputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetOutputData gsod = new GetOutputData(url, values, itemTag);
        gsod.execute();
    }

    private void judgeInputOutput(String scanResult) {
        String itemTag = scanResult;
        String url = getString(R.string.service_address) + "judgeInputOutput";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
       /* values.put("CostCenter", costCenter);
        values.put("LocationNo", locationNo);*/
        values.put("ItemTag", itemTag);
        JudgeInputOutput gsod = new JudgeInputOutput(url, values);
        gsod.execute();
    }

  /*  private void getOutputData(){
        String url=getString(R.string.service_address) + "getOutputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetOutputData gsod = new GetOutputData(url, values);
        gsod.execute();
    }*/


    public class GetInputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTag;
        String itemTag2;

        GetInputData(String url, ContentValues values, String itemTag, String itemTa2) {
            this.url = url;
            this.values = values;
            this.itemTag=itemTag;
            this.itemTag2=itemTa2;
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
                InputData inputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                inputDataArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    inputData = new InputData(
                            child.getString("ItemTag"),
                            child.getString("CoilNo"),
                            child.getString("PartCode"),
                            child.getString("PartName"),
                            child.getString("PartSpec"),
                            child.getString("PartSpecName"),
                            child.getString("Qty"),
                            child.getString("UseFlag")
                    );
                    inputDataArrayList.add(inputData);
                }

                if(this.itemTag.equals(""))
                    inputAdapter = new InputAdapter(ActivityProductionPerformance.this, R.layout.listview_input_row, inputDataArrayList);
                else{
                    inputAdapter = new InputAdapter(ActivityProductionPerformance.this, R.layout.listview_input_row, inputDataArrayList, this.itemTag);
                }
                getOutputData(itemTag2);//생산정보 가져오기
                listViewInput.setAdapter(inputAdapter);
                listViewInput.setSelection(inputAdapter.lastPosition);


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }


    public class GetOutputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTag;

        GetOutputData(String url, ContentValues values, String itemTag) {
            this.url = url;
            this.values = values;
            this.itemTag=itemTag;
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
                OutputData outputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                outputDataArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    outputData = new OutputData(
                            child.getString("ItemTag"),
                            child.getString("CoilNo"),
                            child.getString("PartCode"),
                            child.getString("PartName"),
                            child.getString("PartSpec"),
                            child.getString("PartSpecName"),
                            child.getString("Qty")
                    );
                    outputDataArrayList.add(outputData);

                }
                if(this.itemTag.equals(""))
                    outputAdapter = new OutputAdapter(ActivityProductionPerformance.this, R.layout.listview_output_row, outputDataArrayList);
                else
                    outputAdapter = new OutputAdapter(ActivityProductionPerformance.this, R.layout.listview_output_row, outputDataArrayList, this.itemTag);
                listViewOutput.setAdapter(outputAdapter);
                listViewOutput.setSelection(outputAdapter.lastPosition);
                getProductionBasicInfo();


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }

    private void getProductionBasicInfo(){
        String url=getString(R.string.service_address) + "getProductionBasicInfo";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetProductionBasicInfo gsod = new GetProductionBasicInfo(url, values);
        gsod.execute();
    }


    public class GetProductionBasicInfo extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        GetProductionBasicInfo(String url, ContentValues values){
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
                String worksOrderNo;
                String costCenterName;

                ProductionInfo productionInfo;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                productionInfoArrayList=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    productionInfo = new ProductionInfo(
                            child.getString("OutputQty"),
                            child.getString("CostCenter"),
                            child.getString("CostCenterName"),
                            child.getString("WorksOrderNo"),
                            child.getString("InputQty"),
                            child.getString("IssueOutputQty"),
                            child.getString("LocationNo")
                    );

                    productionInfoArrayList.add(productionInfo);
                }
                worksOrderNo=productionInfoArrayList.get(0).WorksOrderNo;
                costCenterName=productionInfoArrayList.get(0).CostCenterName;

                txtWorksOrderNo.setText(worksOrderNo + " / " + costCenterName);
                txtInputTotal.setText("투입: " + String.format("%.0f", Double.parseDouble(productionInfoArrayList.get(0).InputQty)));
                txtOutputTotal.setText(",   생산: " + String.format("%.0f", Double.parseDouble(productionInfoArrayList.get(0).IssueOutputQty)) + " / "
                        + String.format("%.0f", Double.parseDouble(productionInfoArrayList.get(0).OutputQty)));

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }


    public class JudgeInputOutput extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        JudgeInputOutput(String url, ContentValues values) {
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
                int type = -1;//1:Input, 2:Output
                //outputDataArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    type = Integer.parseInt(child.getString("Type"));
                }
                //1: Input, 2: Output
                if (type == 1) {//Input: 투입
                    setInputData(values.get("ItemTag").toString());
                } else if (type == 2) {//Output: 생산실적
                    setOutputData(values.get("ItemTag").toString());
                } else {
                    Toast.makeText(getBaseContext(), "투입, 실적 TAG정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }

    private void setInputData(String scanResult) {
        String url = getString(R.string.service_address) + "setInputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        values.put("LocationNo", locationNo);
        values.put("ItemTag", scanResult);
        values.put("WorkClassCode", "A");//작업조: 기본셋팅 A 추후 변경
        values.put("UserCode", Users.PhoneNumber);
        SetInputData gsod = new SetInputData(url, values);
        gsod.execute();
    }


    public class SetInputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        SetInputData(String url, ContentValues values) {
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
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                getInputData(values.get("ItemTag").toString(),"");
                //1: Input, 2: Output
               /* if (type == 1) {//Input: 투입
                    setInputData();
                } else if (type == 2) {//Output: 생산실적
                    setOutputData();
                } else {
                    Toast.makeText(getBaseContext(), "투입, 실적 TAG정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }*/

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }


    private void setOutputData(String scanResult) {

        String url = getString(R.string.service_address) + "setOutputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        values.put("LocationNo", locationNo);
        values.put("ItemTag", scanResult);
        values.put("WorkClassCode", "A");//작업조: 기본셋팅 A 추후 변경
        values.put("UserCode", Users.PhoneNumber);
        SetOutputData gsod = new SetOutputData(url, values);
        gsod.execute();
    }


    public class SetOutputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        SetOutputData(String url, ContentValues values) {
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
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                getInputData("", values.get("ItemTag").toString());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }


    private void Scannerinitialize() {
        /*mSavedStatus = mCurrentStatus = STATUS_CLOSE;
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
        }*/
    }

    private void registerReceiver() {
       /* if(mIsRegisterReceiver) return;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_PARAMETER);
        filter.addAction(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS);

        registerReceiver(mReceiver, filter);
        mIsRegisterReceiver = true;*/
    }

}
