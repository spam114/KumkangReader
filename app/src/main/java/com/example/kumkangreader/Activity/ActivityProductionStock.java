package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kumkangreader.Adapter.ProductionStockAdapter;
import com.example.kumkangreader.Object.Stock;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityProductionStock extends BaseActivity{
    TextView txtContent;
    TextView txtPart;
    ListView listviewCoil;
    ArrayList<Stock> stockArrayList;
    ArrayList<String> partCodeList;
    ArrayList<String> partNameList;
    ProductionStockAdapter productionStockAdapter;
    ImageView imvRefresh;
    int selectedIndex=0;

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
        setContentView(R.layout.activity_production_stock);
        this.txtPart = findViewById(R.id.txtPart);
        this.txtContent=findViewById(R.id.txtContent);
        this.listviewCoil=findViewById(R.id.listviewCoil);
        this.txtContent.setText("재고 현황");
        this.imvRefresh=findViewById(R.id.imvRefresh);
        this.imvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProductionStock("");
            }
        });
        //현재날짜 구하기
        /*final Calendar calendar = Calendar.getInstance();
        tyear = calendar.get(Calendar.YEAR);
        tmonth = calendar.get(Calendar.MONTH);
        tdate = calendar.get(Calendar.DATE);
        this.txtDate.setText(tyear + "-" + (tmonth + 1) + "-" + tdate);
        this.txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(tyear, tmonth, tdate);
            }
        });

        this.txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(tyear, tmonth, tdate);
            }
        });*/

        //String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
        getProductionStock("");
    }

    /*private void showDateTimePicker(int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog
                (ActivityProductionStock.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                tyear=year;
                                tmonth=monthOfYear;
                                tdate=dayOfMonth;
                                String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
                                getProductionStock(fromDate);
                            }
                        }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        year, month, date); // 기본값 연월일
        dpd.show();
    }*/

    public void getProductionStock(String partCode) {
        String url = getString(R.string.service_address) + "getProductionStock";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", 2);
        values.put("PartCode", partCode);
        GetProductionStock gsod = new GetProductionStock(url, values, partCode);
        gsod.execute();
    }

    public class GetProductionStock extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String partCode;

        GetProductionStock(String url, ContentValues values, String partCode) {
            this.url = url;
            this.values = values;
            this.partCode=partCode;
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
                Stock stock;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                if(partCode.equals("")){
                    partCodeList = new ArrayList<>();
                    partNameList = new ArrayList<>();

                    partCodeList.add("");
                    partNameList.add("전체");
                }
                stockArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityProductionStock.this, ErrorCheck,2);
                        return;
                    }
                    stock = new Stock();
                    stock.PartCode=child.getString("PartCode");
                    stock.PartName=child.getString("PartName");
                    stock.PartSpecName=child.getString("PartSpecName");
                    stock.Qty=child.getString("Qty");
                    stockArrayList.add(stock);
                    if (!partCodeList.contains(stock.PartCode) && partCode.equals("")) {
                        partCodeList.add(stock.PartCode);
                        partNameList.add(stock.PartName);
                    }
                }
                if(partCode.equals("")){
                    final CharSequence[] partCodeSequences = new CharSequence[partNameList.size()];
                    for (int i = 0; i < partNameList.size(); i++) {
                        partCodeSequences[i] = partNameList.get(i);
                    }
                    txtPart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            new MaterialAlertDialogBuilder(ActivityProductionStock.this)
                                    .setTitle("품명을 선택하세요")
                                    .setSingleChoiceItems(partCodeSequences, selectedIndex, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            selectedIndex = which;
                                        }
                                    })
                                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            String partCode = partCodeList.get(selectedIndex);
                                            getProductionStock(partCode);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String partCode = partCodeList.get(selectedIndex);
                                            getProductionStock(partCode);
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
                productionStockAdapter= new ProductionStockAdapter
                        (ActivityProductionStock.this, R.layout.listview_production_stock_row, stockArrayList);
                listviewCoil.setAdapter(productionStockAdapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF();
            }
        }
    }
}
