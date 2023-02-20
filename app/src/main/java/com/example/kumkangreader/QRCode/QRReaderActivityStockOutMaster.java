package com.example.kumkangreader.QRCode;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class QRReaderActivityStockOutMaster extends Activity implements DecoratedBarcodeView.TorchListener, BaseActivityInterface {

    private CaptureManager manager;
    private boolean isFlashOn = false;// 플래시가 켜져 있는지

    String Zone;
    String ZoneSeqNo;
    private Button btFlash;
    private DecoratedBarcodeView barcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qractivity_stockoutmaster);
        Zone= getIntent().getStringExtra("Zone");
        ZoneSeqNo= getIntent().getStringExtra("ZoneSeqNo");
        barcodeView = findViewById(R.id.db_qr);
        //ViewfinderView vfv = barcodeView.getViewFinder();
        manager = new CaptureManager(this,barcodeView);
        manager.initializeFromIntent(getIntent(),savedInstanceState);
        manager.decode();
        barcodeView.decodeContinuous(callback);

        btFlash = findViewById(R.id.btnFlash);
        btFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFlashOn){
                    barcodeView.setTorchOff();
                    btFlash.setText("플래시켜기");
                    isFlashOn = false;
                }else{
                    barcodeView.setTorchOn();
                    btFlash.setText("플래시끄기");
                    isFlashOn = true;
                }
            }
        });
    }

    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2();
            }
        }, 3500);
        progressON("Loading...", handler);
    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity)QRReaderActivityStockOutMaster.this, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity)QRReaderActivityStockOutMaster.this, message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity)QRReaderActivityStockOutMaster.this, message, handler);
    }

    @Override
    public void progressOFF2() {
        ApplicationClass.getInstance().progressOFF2();
    }

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }

    public class SetInventorySurvey extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTag;

        SetInventorySurvey(String url, ContentValues values, String itemTag) {
            this.url = url;
            this.values = values;
            this.itemTag = itemTag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등등의 행위
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        showErrorDialogQR(QRReaderActivityStockOutMaster.this, ErrorCheck);
                        //Toast.makeText(QRReaderActivityStockOutMaster.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Toast.makeText(QRReaderActivityStockOutMaster.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();
                barcodeView.resume();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF2();
            }
        }
    }


    public void showErrorDialogQR(Context context, String message){
        MaterialAlertDialogBuilder alertBuilder= new MaterialAlertDialogBuilder(context, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog);
        alertBuilder.setTitle("에러 발생");
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                barcodeView.resume();
                dialog.dismiss();
            }
        });
        alertBuilder.setCancelable(false);
        alertBuilder.show();
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                //진동
                Vibrator vibe= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                    VibrationEffect vbe = VibrationEffect.createOneShot(200, 255);
                    vibe.vibrate(vbe);
                }
                else{
                    vibe.vibrate(200);
                }

                //소리
                ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                tone.startTone(ToneGenerator.TONE_DTMF_0,200);

                startProgress();
                barcodeView.pause();
                setInventorySurvey(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    public void setInventorySurvey(String itemTag) {
        String url = getString(R.string.service_address) + "setInventorySurvey";
        ContentValues values = new ContentValues();
        values.put("ItemTag", itemTag);
        values.put("Zone", Zone);
        values.put("ZoneSeqNo", ZoneSeqNo);
        values.put("UserCode", Users.UserID);
        values.put("SeqNo", Users.SeqNo);
        SetInventorySurvey gsod = new SetInventorySurvey(url, values, itemTag);
        gsod.execute();
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

