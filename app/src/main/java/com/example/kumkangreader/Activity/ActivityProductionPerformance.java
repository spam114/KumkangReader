package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kumkangreader.Adapter.InputAdapter;
import com.example.kumkangreader.Object.InputData;
import com.example.kumkangreader.Object.OutputData;
import com.example.kumkangreader.Object.ProductionInfo;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityProductionPerformance extends BaseActivity{
    ArrayList<ProductionInfo> productionInfoArrayList;
    ArrayList<InputData> inputDataArrayList;
    ArrayList<OutputData> outputDataArrayList;
    InputAdapter inputAdapter;//투입 어뎁터
    TextView txtInputTotal;
    TextView txtOutputTotal;
    TextView txtWorksOrderNo;
    String worksOrderNo;
    String costCenter;
    String costCenterName;
    ListView listViewInput;
    ListView listViewOutput;

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
        this.productionInfoArrayList= (ArrayList<ProductionInfo>) getIntent().getSerializableExtra("productionInfoArrayList");
        this.inputDataArrayList=new ArrayList<>();
        this.outputDataArrayList=new ArrayList<>();
        this.listViewInput= findViewById(R.id.listViewInput);
        this.listViewOutput=findViewById(R.id.listViewOutPut);
        this.worksOrderNo =  getIntent().getStringExtra("worksOrderNo");
        this.costCenter =  getIntent().getStringExtra("costCenter");
        this.costCenterName =  getIntent().getStringExtra("costCenterName");
        this.txtInputTotal=findViewById(R.id.txtInputTotal);
        this.txtOutputTotal=findViewById(R.id.txtOutputTotal);
        this.txtWorksOrderNo=findViewById(R.id.txtWorksOrderNo);
        this.txtWorksOrderNo.setText(worksOrderNo+" / "+costCenterName);
        this.txtInputTotal.setText("투입: "+String.format("%.0f",Double.parseDouble(this.productionInfoArrayList.get(0).InputQty)));
        this.txtOutputTotal.setText(", 생산: "+String.format("%.0f",Double.parseDouble(this.productionInfoArrayList.get(0).IssueOutputQty))+" / "
                +String.format("%.0f",Double.parseDouble(this.productionInfoArrayList.get(0).OutputQty)));


        getInputData();//투입정보 가져오기
        //getOutputData();//생산정보 가져오기



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

    private void getInputData(){
        String url=getString(R.string.service_address) + "getInputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetInputData gsod = new GetInputData(url, values);
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
        GetInputData(String url, ContentValues values){
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
                InputData inputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                inputDataArrayList=new ArrayList<>();
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
                    inputAdapter=new InputAdapter(ActivityProductionPerformance.this, R.layout.listview_input_row, inputDataArrayList);
                    listViewInput.setAdapter(inputAdapter);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }


    private void Scannerinitialize(){
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

    private void registerReceiver()
    {
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
