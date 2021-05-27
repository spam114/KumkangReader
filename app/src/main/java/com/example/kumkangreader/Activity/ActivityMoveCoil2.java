package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kumkangreader.Object.Bin;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ActivityMoveCoil2 extends BaseActivity {

    String coilNo = "";
    String partCode = "";
    String partSpec = "";
    String locationNo = "";
    String zone="";
    int maxRow = 0;
    int maxCol = 0;
    float textSize=0;

    ArrayList<Bin> binArrayList;

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
        setContentView(R.layout.activity_move_coil2);
        textSize=Users.ScreenInches*2;

        //잠깐주석
        binArrayList=new ArrayList<>();
        this.locationNo = getIntent().getStringExtra("locationNo");
        this.maxRow = Integer.parseInt(getIntent().getStringExtra("maxRow"));
        this.maxCol = Integer.parseInt(getIntent().getStringExtra("maxCol"));
        this.zone="A";//zone A로 고정
        /*this.maxRow = 12;
        this.maxCol = 4;*/
        getBin();
    }

    private void getBin() {
        String url = getString(R.string.service_address) + "getBin";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", "2");
        values.put("Zone", this.zone);//A로 고정
        GetBin gsod = new GetBin(url, values);
        gsod.execute();
    }


    public class GetBin extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetBin(String url, ContentValues values) {
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
                DecimalFormat myFormatter = new DecimalFormat("###,###");
                Bin bin;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                binArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double weight = Double.parseDouble(child.getString("Weight"));
                    String weightString = myFormatter.format(weight);
                    bin = new Bin(
                            child.getString("BinNo"),
                            child.getString("PartCode"),
                            child.getString("PartSpec"),
                            child.getString("MSpec"),
                            child.getString("PartName"),
                            child.getString("Uncommitted"),
                            weightString,
                            child.getString("LinkCode"),
                            child.getString("Color"),
                            child.getString("PublishDate"),
                            child.getString("Used"),
                            Integer.parseInt(child.getString("RowNum")),
                            Integer.parseInt(child.getString("ColNum"))
                    );
                    binArrayList.add(bin);
                }
                DrawTable();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }


    private void DrawTable() {
        try {
            int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 1, getResources().getDisplayMetrics());

            //DecimalFormat myFormatter = new DecimalFormat("###,###");
            //사이즈 가로세로 사이즈 구하기
            Display display = getWindowManager().getDefaultDisplay();  // in Activity
            /* getActivity().getWindowManager().getDefaultDisplay() */ // in Fragment
            Point size = new Point();
            display.getRealSize(size); // or getSize(size)
            int width = size.x - 100;
            int height = size.y;

            TableLayout tStart = (TableLayout) findViewById(R.id.tStart);
            tStart.removeAllViews();
            TableLayout t0 = (TableLayout) findViewById(R.id.t0);
            t0.removeAllViews();
            TableRow tr = new TableRow(this);
            //꼭지점
            //TableRow row = new TableRow(this);
            TextView tv = new TextView(this);
            tv.setWidth(100);
            tv.setHeight(100);
            tv.setText("");
            tv.setBackgroundResource(R.drawable.background_tablecell);
            tv.setTextColor(Color.BLUE);
            tv.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            tv.setPadding(10, 25, 0, 25);
            tr.addView(tv);
            //tStart.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            TableLayout t1 = (TableLayout) findViewById(R.id.t1);
            t1.removeAllViews();

            //Column

            for (int j = 0; j < maxCol; j++) {

                //일자를 넣어야함
                TextView tvNo = new TextView(this);
                tvNo.setWidth(width / 4);
                tvNo.setHeight(100);
                tvNo.setText(Integer.toString(j + 1));
                tvNo.setTextColor(Color.BLUE);
                tvNo.setBackgroundResource(R.drawable.background_tablecell);
                tvNo.setPadding(10, 25, 0, 25);
                tvNo.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

                tr.addView(tvNo);
            }
            tStart.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            //Row
            for (int i = 0; i < maxRow; i++) {
                TableRow tableRow = new TableRow(this);
                TextView tvRow = new TextView(this);
                tvRow.setWidth(100);
                tvRow.setHeight(300);
                tvRow.setText(Integer.toString(i + 1));
                tvRow.setBackgroundResource(R.drawable.background_tablecell);
                tvRow.setTextColor(Color.BLUE);
                tvRow.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
                tvRow.setPadding(10, 25, 0, 25);
                tableRow.addView(tvRow);
                t0.addView(tableRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


           /* TableLayout.LayoutParams tlparams = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT);
            tlparams.height=0;*/
                //DecimalFormat myFormatter = new DecimalFormat("##");


                //주데이터
                TableRow tr_date = new TableRow(this);
                //tr_date.setLayoutParams(tlparams);
                int totalLength = 0;
                int partLength = 0;
                int coilLength = 0;
                int publishDateLength = 0;
                int weightLength = 0;
                String bin="";
                for (int j = 0; j < maxCol; j++) {
                    String contents = "";
                    Bin binContent = null;

                    for (int k = 0; k < binArrayList.size(); k++) {
                        if (i == binArrayList.get(k).RowNum && j == binArrayList.get(k).ColNum) {


                            contents = binArrayList.get(k).PartName + "(" + binArrayList.get(k).PartSpec + ")" + "\n"
                                    + binArrayList.get(k).LinkCode + "\n"
                                    + binArrayList.get(k).PublishDate + "\n"
                                    + binArrayList.get(k).Weight + " KG";
                            /*totalLength = (binArrayList.get(k).PartName + "(" + binArrayList.get(k).PartSpec + ")" + "\n"
                                    + binArrayList.get(k).LinkCode + "\n"
                                    + binArrayList.get(k).PublishDate + "\n" + binArrayList.get(k).Weight + " KG").length();*/
                            coilLength = (binArrayList.get(k).LinkCode + "\n").length();
                            partLength = (binArrayList.get(k).PartName + "(" + binArrayList.get(k).PartSpec + ")" + "\n").length();
                            publishDateLength=(binArrayList.get(k).PublishDate + "\n").length();
                            weightLength=(binArrayList.get(k).Weight + " KG").length();
                            binContent = binArrayList.get(k);
                            break;
                        }
                        bin=this.zone+"-"+String.format("%02d", j+1)+String.format("%02d", i+1);
                    }
                    SpannableStringBuilder ssb = new SpannableStringBuilder(contents);
                    if (contents.length() != 0) {
                        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#000BFF")), 0, partLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#C00000")), partLength, partLength+coilLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), partLength+coilLength, partLength+coilLength+publishDateLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#7030A0")), partLength+coilLength+publishDateLength, partLength+coilLength+publishDateLength+weightLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    }
                    //ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#002060")), totalLength-partLength, partLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    //주데이터
                    TextView tvNo = new TextView(this);
                    LinearLayout.LayoutParams linearLayoutParams =

                            new LinearLayout.LayoutParams(
                                    width / 4,
                                    300

                            );
                    tvNo.setWidth(width / 4);
                    tvNo.setHeight(300);
                    tvNo.setTextSize(textSize);
                    //tvNo.setMovementMethod(new ScrollingMovementMethod());
                    //tvNo.setSingleLine(false);
                    //tvNo.setLayoutParams(linearLayoutParams);
                    if(contents.length() != 0){
                        tvNo.setText(ssb);
                        //tvNo.setLayoutParams(linearLayoutParams);
                    }
                    else{
                        tvNo.setText(bin);
                        tvNo.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvNo.setTextSize(textSize);
                    }

                    tvNo.setTextColor(Color.BLACK);
                    if (binContent == null) {
                        tvNo.setBackgroundResource(R.drawable.background_table_white);
                    } else if (binContent.Color.equals("")) {
                        tvNo.setBackgroundResource(R.drawable.background_table_gray);
                    } else if (binContent.Color.equals("노랑")) {
                        tvNo.setBackgroundResource(R.drawable.background_table_yellow);
                    } else if (binContent.Color.equals("흰색")) {
                        tvNo.setBackgroundResource(R.drawable.background_table_white);
                    } else if (binContent.Color.equals("파랑")) {
                        tvNo.setBackgroundResource(R.drawable.background_table_skyblue);
                    } else if (binContent.Color.equals("초록")) {
                        tvNo.setBackgroundResource(R.drawable.background_table_lightgreen);
                    } else if (binContent.Color.equals("주황")) {
                        tvNo.setBackgroundResource(R.drawable.background_table_lightsalmon);
                    }
                    tvNo.setPadding(10, 15, 0, 15);
                    //tvNo.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

                    tvNo.setTag(R.id.objBin, binContent);
                    tvNo.setTag(R.id.rowIndex, Integer.toString(i));
                    tvNo.setTag(R.id.colIndex, Integer.toString(j));
                    tvNo.setTag(R.id.binNo, bin);
                    tvNo.setOnClickListener(mOnclickListener);

                /*TableRow.LayoutParams tlparams = new TableRow.LayoutParams();
                tlparams.width=width/4;
                tlparams.height=300;
                tr_date.setLayoutParams(tlparams);*/
                    tr_date.addView(tvNo);
                }
                t1.addView(tr_date);

            }


            //TableLayout t1 = (TableLayout)findViewById(R.id.t1);
        /*t1.removeAllViews();

        TableRow tr_date = new TableRow(this);
        for (int j = 0; j < maxCol; j++) {
//일자를 넣어야함
            TextView tvNo = new TextView(this);
            tvNo.setWidth(70 * dip);
            tvNo.setText("세로");
            tvNo.setTextColor(Color.BLUE);
            tvNo.setBackgroundResource(R.drawable.background_tableheader);
            tvNo.setPadding(10, 25, 0, 25);
            tvNo.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

            tr_date.addView(tvNo);
        }*/
        } catch (Exception e) {
            String sdf = e.getStackTrace().toString();
            Toast.makeText(ActivityMoveCoil2.this, sdf, Toast.LENGTH_SHORT).show();

        }
    }

    TextView.OnClickListener mOnclickListener = new
            View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        /*dailyCost = (DailyCost2)v.getTag(R.id.costDate);

                        String costType = v.getTag(R.id.costType).toString();
                        String type = toCostType(v.getTag(R.id.costType).toString());
                        String date = dailyCost.CostDate.toString();

                        //다이알로그
                        ShowDialog(dailyCost, costType, type, date, (TextView)v);*/

                    Bin bin = (Bin) v.getTag(R.id.objBin);
                    final String rowIndex = v.getTag(R.id.rowIndex).toString();
                    final String colIndex = v.getTag(R.id.colIndex).toString();
                    String binNo=v.getTag(R.id.binNo).toString();
                    if (bin != null)//들어있으면 이동불가
                        return;

                    new MaterialAlertDialogBuilder(ActivityMoveCoil2.this)
                            .setTitle("코일 적재")
                            .setMessage("코일을 '"+binNo+"'에 적재하겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            moveCoil("A", colIndex, rowIndex, coilNo, partCode, partSpec);
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ActivityMoveCoil2.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }).show();


                }


            };

    public void moveCoil(String zone, String colNum, String rowNum, String coilNo, String partCode, String partSpec) {
        String url = getString(R.string.service_address) + "moveCoil";
        ContentValues values = new ContentValues();
        values.put("Zone", zone);
        values.put("ColNum", colNum);
        values.put("RowNum", rowNum);
        values.put("CoilNo", coilNo);
        values.put("PartCode", partCode);
        values.put("PartSpec", partSpec);
        values.put("UserCode", Users.PhoneNumber);
        MoveCoil gsod = new MoveCoil(url, values);
        gsod.execute();
    }


    public class MoveCoil extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        MoveCoil(String url, ContentValues values) {
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

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getBin();
                progressOFF();
            }

        }
    }


    public Handler mHandler = new Handler() { //ScrapAdapter 와 통신을 위함
        @Override
        public void handleMessage(Message msg) {
            /*scrapDataArrayList = (ArrayList<ScrapData>)msg.getData().getSerializable("scrapDataArrayList");
            if(scrapDataArrayList.size()==0){
                Toast.makeText(ActivityScrap.this, "등록 된 불량품이 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            scrapAdapter=new ScrapAdapter(ActivityScrap.this, R.layout.listview_scrap_row, scrapDataArrayList, itemTag, costCenter, mHandler);
            listViewScrap.setAdapter(scrapAdapter);*/
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