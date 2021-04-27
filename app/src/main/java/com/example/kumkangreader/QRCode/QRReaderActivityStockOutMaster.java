package com.example.kumkangreader.QRCode;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.kumkangreader.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QRReaderActivityStockOutMaster extends Activity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager manager;
    private boolean isFlashOn = false;// 플래시가 켜져 있는지


    private Button btFlash;
    private DecoratedBarcodeView barcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qractivity_stockoutmaster);

        barcodeView = findViewById(R.id.db_qr);

        manager = new CaptureManager(this,barcodeView);
        manager.initializeFromIntent(getIntent(),savedInstanceState);
        manager.decode();

        btFlash = findViewById(R.id.btnFlash);
        btFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    barcodeView.setTorchOff();
                }else{
                    barcodeView.setTorchOn();
                }
            }
        });
    }

    @Override
    public void onTorchOn() {

        btFlash.setText("플래시끄기");
        isFlashOn = true;
    }

    @Override
    public void onTorchOff() {
        btFlash.setText("플래시켜기");
        isFlashOn = false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        manager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        manager.onSaveInstanceState(outState);
    }

}

