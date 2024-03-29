package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kumkangreader.Adapter.InputAdapter;
import com.example.kumkangreader.Adapter.OutputAdapter;
import com.example.kumkangreader.Object.InputData;
import com.example.kumkangreader.Object.OutputData;
import com.example.kumkangreader.Object.ProductionInfo;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
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
    TextView txtIssueTotal;
    TextView txtInputTotal;
    TextView txtScrappedQty;
    TextView txtOutputTotal;
    TextView txtWorksOrderNo;
    TextView txtRepresent;
    String worksOrderNo;
    String costCenter;
    String costCenterName;
    String locationNo;
    ListView listViewInput;
    ListView listViewOutput;
    ImageView imvQR;
    TextInputEditText edtScan;
    String centerSpec;//1번은 코일
    String itemTagForDirect;//바깥쪽에서 실적을 바로잡기위하여 셋팅한 변수

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
        startProgress();
        this.productionInfoArrayList = (ArrayList<ProductionInfo>) getIntent().getSerializableExtra("productionInfoArrayList");
        this.inputDataArrayList = new ArrayList<>();
        this.outputDataArrayList = new ArrayList<>();
        this.listViewInput = findViewById(R.id.listViewInput);
        this.listViewOutput = findViewById(R.id.listViewOutPut);
        this.worksOrderNo = getIntent().getStringExtra("worksOrderNo");
        this.costCenter = getIntent().getStringExtra("costCenter");
        this.costCenterName = getIntent().getStringExtra("costCenterName");
        this.locationNo = getIntent().getStringExtra("locationNo");
        this.txtIssueTotal=findViewById(R.id.txtIssueTotal);
        this.txtInputTotal = findViewById(R.id.txtInputTotal);
        this.txtOutputTotal = findViewById(R.id.txtOutputTotal);
        this.txtWorksOrderNo = findViewById(R.id.txtWorksOrderNo);
        this.txtRepresent=findViewById(R.id.txtRepresent);
        this.txtWorksOrderNo.setText(worksOrderNo + " / " + costCenterName);

        //this.txtInputTotal.setText("총투입: " + String.format("%,d", Integer.parseInt(this.productionInfoArrayList.get(0).InputQty))+"("+
               // String.format("%,d", Integer.parseInt(this.productionInfoArrayList.get(0).InputQtyBD))+")");
        //this.txtOutputTotal.setText(", 발행/생산: " + String.format("%.0f", Double.parseDouble(this.productionInfoArrayList.get(0).IssueOutputQty)) + " / "
                //+ String.format("%.0f", Double.parseDouble(this.productionInfoArrayList.get(0).OutputQty)));
        this.txtScrappedQty=findViewById(R.id.txtScrappedQty);
        //this.txtScrappedQty.setText("불량: " + String.format("%.0f", Double.parseDouble(this.productionInfoArrayList.get(0).ScrappedQty)));
        this.txtRepresent.setText(this.productionInfoArrayList.get(0).PartName+"("+this.productionInfoArrayList.get(0).PartSpecName+")");
        this.edtScan=findViewById(R.id.edtScan);
        this.centerSpec=this.productionInfoArrayList.get(0).CenterSpec;
        this.itemTagForDirect = getIntent().getStringExtra("itemTag");


        this.imvQR = findViewById(R.id.imvQR);
        getInputData("","", this.itemTagForDirect);//투입정보 가져오기

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

        this.edtScan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edtScan.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);
                }
                else{
                    edtScan.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
                }
            }
        });

        this.edtScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO

                    judgeInputOutput(v.getText().toString().toUpperCase());
                }
                return false;
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                imvQR.performClick();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(requestCode==Users.REQUEST_SCRAP){
            getProductionBasicInfo("");
        }
      /*  if (requestCode == REQUEST_STOCKOUT) {//ActivityStockOut에서 돌아옴
        }*/

        else if (result != null) {
            if (result.getContents() == null) {
                //Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
                showErrorDialog(this, "취소 되었습니다.",1);
            } else {
                String scanResult;
                scanResult = result.getContents();
                judgeInputOutput(scanResult);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void getInputData(String itemTag, String itemTag2, String itemTagForDirect) {
        String url = getString(R.string.service_address) + "getInputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetInputData gsod = new GetInputData(url, values, itemTag, itemTag2, itemTagForDirect);
        gsod.execute();
    }

    private void getOutputData(String itemTag, String itemTagForDirect) {
        String url = getString(R.string.service_address) + "getOutputData";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetOutputData gsod = new GetOutputData(url, values, itemTag, itemTagForDirect);
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
        values.put("CostCenter", costCenter);
        values.put("UserCode", Users.UserID);
        JudgeInputOutput gsod = new JudgeInputOutput(url, values);
        gsod.execute();
    }


    public class GetInputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTag;
        String itemTag2;
        String itemTagForDirect;

        GetInputData(String url, ContentValues values, String itemTag, String itemTa2, String itemTagForDirect) {
            this.url = url;
            this.values = values;
            this.itemTag=itemTag;
            this.itemTag2=itemTa2;
            this.itemTagForDirect=itemTagForDirect;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
            //startProgress();
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
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityProductionPerformance.this, ErrorCheck,2);
                        return;
                    }
                    inputData = new InputData(
                            child.getString("ItemTag"),
                            child.getString("CoilNo"),
                            child.getString("PartCode"),
                            child.getString("PartName"),
                            child.getString("PartSpec"),
                            child.getString("PartSpecName"),
                            child.getString("MSpec"),
                            child.getString("Qty"),
                            child.getString("UseFlag"),
                            child.getString("SeqNo")
                    );
                    inputDataArrayList.add(inputData);
                }

                if(this.itemTag.equals(""))
                    inputAdapter = new InputAdapter(ActivityProductionPerformance.this, R.layout.listview_input_row, inputDataArrayList, worksOrderNo, costCenter, mHandler);
                else{
                    inputAdapter = new InputAdapter(ActivityProductionPerformance.this, R.layout.listview_input_row, inputDataArrayList, this.itemTag, worksOrderNo,costCenter, mHandler);
                }
                getOutputData(itemTag2, itemTagForDirect);//생산정보 가져오기
                listViewInput.setAdapter(inputAdapter);
                //listViewInput.setCacheColorHint(Color.TRANSPARENT);
                listViewInput.setSelection(inputDataArrayList.size()-1);
                //progressOFF();

            } catch (Exception e) {
                e.printStackTrace();
                //progressOFF();
            } finally {

            }

        }
    }


    public class GetOutputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTag;
        String itemTagForDirect;

        GetOutputData(String url, ContentValues values, String itemTag, String itemTagForDirect) {
            this.url = url;
            this.values = values;
            this.itemTag=itemTag;
            this.itemTagForDirect=itemTagForDirect;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
            //startProgress();
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
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityProductionPerformance.this, ErrorCheck,2);
                        return;
                    }
                    outputData = new OutputData(
                            child.getString("ItemTag"),
                            child.getString("CoilNo"),
                            child.getString("PartCode"),
                            child.getString("PartName"),
                            child.getString("PartSpec"),
                            child.getString("PartSpecName"),
                            child.getString("MSpec"),
                            child.getString("Qty"),
                            child.getString("SeqNo")
                    );
                    outputDataArrayList.add(outputData);

                }
                if(this.itemTag.equals(""))
                    outputAdapter = new OutputAdapter(ActivityProductionPerformance.this, R.layout.listview_output_row, outputDataArrayList, worksOrderNo, costCenter, mHandler);
                else
                    outputAdapter = new OutputAdapter(ActivityProductionPerformance.this, R.layout.listview_output_row, outputDataArrayList, this.itemTag, worksOrderNo, costCenter, mHandler);
                listViewOutput.setAdapter(outputAdapter);
                //listViewOutput.setCacheColorHint(Color.TRANSPARENT);
                //listViewOutput.setSelection(outputAdapter.lastPosition);
                listViewOutput.setSelection(outputDataArrayList.size()-1);
                //progressOFF();
                getProductionBasicInfo(itemTagForDirect);


            } catch (Exception e) {
                e.printStackTrace();
                //progressOFF();
            } finally {

            }
        }
    }

    /**
     * 생산실적, 투입 삭제후
     */
    public Handler mHandler = new Handler() { //다이얼로그 종료시 액티비티 데이터 전송을 위함
        @Override
        public void handleMessage(Message msg) {

            showErrorDialog(ActivityProductionPerformance.this, "데이터가 반영 되었습니다.",1);
            getInputData("","", "");

        }
    };


    private void getProductionBasicInfo(String itemTagForDirect){
        String url=getString(R.string.service_address) + "getProductionBasicInfo";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetProductionBasicInfo gsod = new GetProductionBasicInfo(url, values, itemTagForDirect);
        gsod.execute();
    }


    public class GetProductionBasicInfo extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTagForDirect;
        GetProductionBasicInfo(String url, ContentValues values, String itemTagForDirect){
            this.url = url;
            this.values = values;
            this.itemTagForDirect=itemTagForDirect;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
            //startProgress();
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
                int centerType=0;
                productionInfoArrayList=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityProductionPerformance.this, ErrorCheck,2);
                        return;
                    }
                    productionInfo = new ProductionInfo(
                            child.getString("OutputQty"),
                            child.getString("OutputQtyBD"),
                            child.getString("CostCenter"),
                            child.getString("CostCenterName"),
                            child.getString("WorksOrderNo"),
                            child.getString("InputQty"),
                            child.getString("InputQtyBD"),
                            child.getString("IssueOutputQty"),
                            child.getString("IssueOutputQtyBD"),
                            child.getString("LocationNo"),
                            child.getString("ScrappedQty"),
                            child.getString("CenterSpec"),
                            child.getString("PartName"),
                            child.getString("PartSpecName")
                    );
                    centerType=Integer.parseInt(child.getString("CenterSpec"));
                    productionInfoArrayList.add(productionInfo);
                }
                worksOrderNo=productionInfoArrayList.get(0).WorksOrderNo;
                costCenterName=productionInfoArrayList.get(0).CostCenterName;

                txtWorksOrderNo.setText(worksOrderNo + " / " + costCenterName);
                txtIssueTotal.setText("총발행: " + String.format("%,d", Integer.parseInt(productionInfoArrayList.get(0).IssueOutputQty))+
                        "("+String.format("%,d", Integer.parseInt(productionInfoArrayList.get(0).IssueOutputQtyBD))+")");

                txtScrappedQty.setText("불량: " + String.format("%,d", Integer.parseInt(productionInfoArrayList.get(0).ScrappedQty)));

                txtRepresent.setText(productionInfoArrayList.get(0).PartName+"("+productionInfoArrayList.get(0).PartSpecName+")");

                int todayInput=0;
                int todayInputBD=inputDataArrayList.size();
                for(int i=0;i<inputDataArrayList.size();i++){
                    todayInput+=Integer.parseInt(inputDataArrayList.get(i).Qty);
                }

                int todayOutput=0;
                int todayOutputBD=outputDataArrayList.size();

                for(int i=0;i<outputDataArrayList.size();i++){
                    todayOutput+=Integer.parseInt(outputDataArrayList.get(i).Qty);
                }

                txtInputTotal.setText("총투입: " + String.format("%,d", Integer.parseInt(productionInfoArrayList.get(0).InputQty))+
                        "("+String.format("%,d", Integer.parseInt(productionInfoArrayList.get(0).InputQtyBD))+")"
                        +", 당일: "+String.format("%,d", todayInput)+"("+String.format("%,d", todayInputBD)+")");

                txtOutputTotal.setText("총생산: " + String.format("%,d", Integer.parseInt(productionInfoArrayList.get(0).OutputQty))+
                        "("+String.format("%,d", Integer.parseInt(productionInfoArrayList.get(0).OutputQtyBD))+")"
                        +", 당일: "+String.format("%,d", todayOutput)+"("+String.format("%,d", todayOutputBD)+")");

                if(centerType==1){//작업장 코일이면 입력 키패드 문자까지
                    edtScan.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                if(!itemTagForDirect.equals("")){//바로등록
                    judgeInputOutput(itemTagForDirect);
                }
                //progressOFF();

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
                //String[] coilArr=new String[jsonArray.length()];
                ArrayList<String> coilArrayList=new ArrayList<>();
                //outputDataArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityProductionPerformance.this, ErrorCheck,2);
                        return;
                    }
                    else if(!child.getString("CoilNo").equals("null")){//중복된 코일이 존재한다.
                        coilArrayList.add(child.getString("CoilNo"));
                        continue;
                    }
                    type = Integer.parseInt(child.getString("Type"));
                }

                if(coilArrayList.size()>0){
                    //중복코일이 존재할 경우 선택창 출력

                    final String coilArr[]= new String[coilArrayList.size()];

                    for (int i = 0; i < coilArrayList.size(); i++) {
                        coilArr[i]=coilArrayList.get(i);
                    }

                    MaterialAlertDialogBuilder alertBuilder= new MaterialAlertDialogBuilder(ActivityProductionPerformance.this);
                    alertBuilder.setTitle("코일번호 선택");
                    final String[] selectedCoil = {""};
                    alertBuilder.setSingleChoiceItems(coilArr, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedCoil[0] =coilArr[which];
                        }
                    });
                    alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            judgeInputOutput(selectedCoil[0]);
                            dialog.dismiss();
                        }
                    });
                    alertBuilder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
                    alertBuilder.show();
                    //progressOFF();
                    return;
                }

                //1: Input, 2: Output
                if (type == 1) {//Input: 투입
                    setInputData(values.get("ItemTag").toString());
                } else if (type == 2) {//Output: 생산실적
                    setOutputData(values.get("ItemTag").toString());
                } else {
                    //Toast.makeText(getBaseContext(), "투입, 실적 TAG정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    showErrorDialog(ActivityProductionPerformance.this, "투입, 실적 TAG정보가 없습니다.",2);
                    //progressOFF();
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
                //progressOFF();
            } finally {
                itemTagForDirect="";
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
        values.put("WorkClassCode", Users.WorkClassCode);//작업조: 기본셋팅 A 추후 변경
        values.put("UserCode", Users.UserID);
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
            //startProgress();
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
                String itemTag="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityProductionPerformance.this, ErrorCheck,2);
                        return;
                    }
                    itemTag=child.getString("ItemTag");
                }

                getInputData(itemTag,"", "");
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
                //progressOFF();
            } finally {

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
        values.put("WorkClassCode", Users.WorkClassCode);//작업조: 기본셋팅 A 추후 변경
        values.put("UserCode", Users.UserID);
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
            //startProgress();
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
                String itemTag="";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityProductionPerformance.this, ErrorCheck,2);
                        return;
                    }
                    itemTag=child.getString("ItemTag");
                }

                getInputData("", itemTag, "");

            } catch (Exception e) {
                e.printStackTrace();
                //progressOFF();
            } finally {

            }

        }
    }

    /**
     * EI가 붙어있지
     */
    private void PlusStringEI(){
        //asfsdafsadfsdf
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
