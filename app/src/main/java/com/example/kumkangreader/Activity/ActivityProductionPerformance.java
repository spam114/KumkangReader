package com.example.kumkangreader.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.example.kumkangreader.Object.ProductionInfo;
import com.example.kumkangreader.R;

import java.util.ArrayList;

public class ActivityProductionPerformance extends BaseActivity{
    ArrayList<ProductionInfo> productionInfoArrayList;
    TextView txtInputTotal;
    TextView txtOutputTotal;
    TextView txtWorksOrderNo;
    String worksOrderNo;
    String costCenterName;

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
        this.worksOrderNo =  getIntent().getStringExtra("worksOrderNo");
        this.costCenterName =  getIntent().getStringExtra("costCenterName");
        this.txtInputTotal=findViewById(R.id.txtInputTotal);
        this.txtOutputTotal=findViewById(R.id.txtOutputTotal);
        this.txtWorksOrderNo=findViewById(R.id.txtWorksOrderNo);
        this.txtWorksOrderNo.setText(worksOrderNo+" / "+costCenterName);
        this.txtInputTotal.setText("투입: "+String.format("%.0f",Double.parseDouble(this.productionInfoArrayList.get(0).InputQty)));
        this.txtOutputTotal.setText(", 생산: "+String.format("%.0f",Double.parseDouble(this.productionInfoArrayList.get(0).IssueOutPutQty))+" / "
                +String.format("%.0f",Double.parseDouble(this.productionInfoArrayList.get(0).OutPutQty)));

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
