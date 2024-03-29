package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
    String locationNo = "";
    String zone="";
    int maxRow = 0;
    int maxCol = 0;
    float textSize=0;
    TextView txtSelectedCoil;
    ArrayList<Bin> binArrayList;
    String selectedCoil;
    String selectedPartCode;
    String selectedPartSpec;
    String rowIndex;
    String colIndex;
    String binNo;
    Boolean firstInputCoil;
    Spinner spinnerZone;


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
        startProgress();
        textSize=Users.ScreenInches*2;
        this.txtSelectedCoil=findViewById(R.id.txtSelectedCoil);
        //잠깐주석
        binArrayList=new ArrayList<>();
        this.selectedCoil="";
        this.selectedPartCode="";
        this.selectedPartSpec="";
        this.firstInputCoil=false;
        this.locationNo ="23961";//원자재 창고(코일)
        this.spinnerZone=findViewById(R.id.spinnerZone);

        this.rowIndex="-1";
        this.colIndex="-1";
        this.binNo="-1";

        String[] zoneList=new String[6];
        zoneList[0]="A";
        zoneList[1]="B";
        zoneList[2]="C";
        zoneList[3]="D";
        zoneList[4]="E";
        zoneList[5]="F";


        if(!getIntent().getStringExtra("coilNo").equals("")){//최초코일입고
            this.selectedCoil=getIntent().getStringExtra("coilNo");
            this.selectedPartCode=getIntent().getStringExtra("partCode");
            this.selectedPartSpec=getIntent().getStringExtra("partSpec");
            firstInputCoil=true;
            SelectCoilData(selectedCoil,selectedPartCode,selectedPartSpec);
        }

        ArrayAdapter<String> zoneAdapter = new ArrayAdapter<String>(ActivityMoveCoil2.this, android.R.layout.simple_spinner_dropdown_item, zoneList);
        this.spinnerZone.setAdapter(zoneAdapter);
        this.spinnerZone.setDropDownWidth(150);
        this.spinnerZone.setSelection(0);
        this.spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        zone="A";
                        break;
                    case 1:
                        zone="B";
                        break;
                    case 2:
                        zone="C";
                        break;
                    case 3:
                        zone="D";
                        break;
                    case 4:
                        zone="E";
                        break;
                    case 5:
                        zone="F";
                        break;
                }
                getMaxLengthTable(zone);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * 존별 테이블의 가로 세로 최대값을 가져온후, 태그이동 액티비티로 이동
     */
    public void getMaxLengthTable(String zone){
        String url = getString(R.string.service_address) + "getMaxLengthTable";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", "2");
        values.put("Zone", zone);
        GetMaxLengthTable gsod = new GetMaxLengthTable(url, values);
        gsod.execute();
    }

    public class GetMaxLengthTable extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetMaxLengthTable(String url, ContentValues values) {
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
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        showErrorDialog(ActivityMoveCoil2.this, ErrorCheck,2);
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    maxRow = Integer.parseInt(child.getString("MaxRow"));
                    maxCol = Integer.parseInt(child.getString("MaxCol"));
                }

                getBin();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

        }
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
                        showErrorDialog(ActivityMoveCoil2.this, ErrorCheck,2);
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
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
            tv.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            tv.setPadding(10, 25, 0, 25);
            tr.addView(tv);
            //tStart.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            TableLayout t1 = (TableLayout) findViewById(R.id.t1);
            t1.removeAllViews();

            //Column

            for (int j = 0; j < maxCol; j++) {
                TextView tvNo = new TextView(this);
                tvNo.setWidth(width / 4);
                tvNo.setHeight(100);
                tvNo.setText(Integer.toString(j + 1));
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
                if (i % 2 == 1)//홀수행
                    tvRow.setBackgroundResource(R.drawable.background_tablecell);
                else
                    tvRow.setBackgroundResource(R.drawable.background_table_white);
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
                    }
                    bin=this.zone+"-"+String.format("%02d", i+1)+String.format("%02d", j+1);
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
                    if(contents.length() != 0){
                        tvNo.setText(ssb);
                    }
                    else{
                        tvNo.setText(bin);
                        tvNo.setGravity(Gravity.CENTER_HORIZONTAL);
                        tvNo.setTextSize(textSize);
                    }

                    tvNo.setTextColor(Color.BLACK);
                    if (i % 2 == 1)//홀수행
                        tvNo.setBackgroundResource(R.drawable.background_tablecell);
                    else
                        tvNo.setBackgroundResource(R.drawable.background_table_white);

                   /* if (binContent == null) {
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
                    }*/
                    tvNo.setPadding(10, 15, 0, 15);
                    //tvNo.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

                    tvNo.setTag(R.id.objBin, binContent);
                    tvNo.setTag(R.id.rowIndex, Integer.toString(i));
                    tvNo.setTag(R.id.colIndex, Integer.toString(j));
                    tvNo.setTag(R.id.binNo, bin);
                    tvNo.setOnClickListener(mOnclickListener);

                    if(binContent!=null) {
                        if (binContent.LinkCode.equals(selectedCoil)) {
                            tvNo.setTypeface(null, Typeface.BOLD_ITALIC);
                            tvNo.setPaintFlags(tvNo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                        }
                    }

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
            String sdf = e.getMessage().toString();
            //Toast.makeText(ActivityMoveCoil2.this, sdf, Toast.LENGTH_SHORT).show();
            showErrorDialog(ActivityMoveCoil2.this, sdf,2);

        }
    }

    TextView.OnClickListener mOnclickListener = new
            View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bin bin = (Bin) v.getTag(R.id.objBin);
                    rowIndex = v.getTag(R.id.rowIndex).toString();
                    colIndex = v.getTag(R.id.colIndex).toString();
                    binNo=v.getTag(R.id.binNo).toString();
                    if (bin != null) {//데이터가 들어있다면 -> 이동할 코일 선택
                        //여기만적용

                        if(firstInputCoil){
                            //Toast.makeText(ActivityMoveCoil2.this, "이미 코일이 적재되어 있습니다.", Toast.LENGTH_SHORT).show();
                            showErrorDialog(ActivityMoveCoil2.this, "이미 코일이 적재되어 있습니다.",2);
                            return;
                        }
                        SelectCoilData(bin.LinkCode, bin.PartCode, bin.PartSpec);
                        getBin();
                    }
                    else{//데이터가 들어가있지 않다면
                        if(selectedCoil.equals("")){//선택안한상태
                            //Toast.makeText(ActivityMoveCoil2.this, "이동할 코일을 선택하여 주세요.", Toast.LENGTH_SHORT).show();
                            showErrorDialog(ActivityMoveCoil2.this, "이동할 코일을 선택하여 주세요.",2);
                            getBin();
                        }
                        else{//선택한상태
                            //코일적재
                            new MaterialAlertDialogBuilder(ActivityMoveCoil2.this)
                                    .setTitle("코일 적재")
                                    .setMessage("코일("+selectedCoil+")을 '"+binNo+"'에 적재하겠습니까?")
                                    .setCancelable(true)
                                    .setPositiveButton
                                            ("확인", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    moveCoil(zone, colIndex, rowIndex, selectedCoil, selectedPartCode, selectedPartSpec);
                                                }
                                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(ActivityMoveCoil2.this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                                    showErrorDialog(ActivityMoveCoil2.this, "취소 되었습니다.",1);
                                }
                            }).show();
                        }
                    }
                    return;
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
        values.put("UserCode", Users.UserID);
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

                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityMoveCoil2.this, ErrorCheck,2);
                        return;
                    }
                }
                //코일 이동후 미선택 코일 미선택 상태로 변경
                SelectCoilData("","","");
                firstInputCoil=false;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getBin();
                //progressOFF();
            }

        }
    }

    private void SelectCoilData(String coilNo, String partCode, String partSpec){
        selectedCoil=coilNo;
        selectedPartCode=partCode;
        selectedPartSpec=partSpec;

        if(coilNo.equals("")){
            this.txtSelectedCoil.setText("이동시킬 코일을 선택");
        }
        else{
            this.txtSelectedCoil.setText("선택된 코일("+coilNo+")");
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