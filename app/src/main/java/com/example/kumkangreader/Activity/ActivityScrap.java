package com.example.kumkangreader.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.example.kumkangreader.Adapter.ScrapAdapter;
import com.example.kumkangreader.Object.ScrapData;
import com.example.kumkangreader.R;

import java.util.ArrayList;

public class ActivityScrap extends BaseActivity {

    ArrayList<ScrapData> scrapDataArrayList;
    ListView listViewScrap;
    ScrapAdapter scrapAdapter;
    String itemTag;
    String costCenter;

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
        setContentView(R.layout.activity_scrap);

        this.itemTag=getIntent().getStringExtra("itemTag");
        this.costCenter=getIntent().getStringExtra("costCenter");
        this.scrapDataArrayList = (ArrayList<ScrapData>) getIntent().getSerializableExtra("scrapDataArrayList");
        this.scrapAdapter=new ScrapAdapter(ActivityScrap.this, R.layout.listview_scrap_row, scrapDataArrayList, this.itemTag, this.costCenter, mHandler);
        this.listViewScrap=findViewById(R.id.listviewScrap);
        this.listViewScrap.setAdapter(this.scrapAdapter);
    }

    public Handler mHandler = new Handler() { //ScrapAdapter 와 통신을 위함
        @Override
        public void handleMessage(Message msg) {
            scrapDataArrayList = (ArrayList<ScrapData>)msg.getData().getSerializable("scrapDataArrayList");
            scrapAdapter=new ScrapAdapter(ActivityScrap.this, R.layout.listview_scrap_row, scrapDataArrayList, itemTag, costCenter, mHandler);
            listViewScrap.setAdapter(scrapAdapter);
        }
    };

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