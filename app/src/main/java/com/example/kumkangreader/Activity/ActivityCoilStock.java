package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kumkangreader.Adapter.CoilStockAdapter;
import com.example.kumkangreader.Object.Location;
import com.example.kumkangreader.Object.Stock;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class ActivityCoilStock extends BaseActivity {
    TextView txtContent;
    TextView txtPart;
    ListView listviewCoil;
    Map<Integer, Location> locationMap;
    CoilStockAdapter coilStockAdapter;
    ArrayList<Stock> stockArrayList;
    ArrayList<String> partCodeList;
    ArrayList<String> partNameList;
    ImageView imvRefresh;
    Spinner spinnerLocation;
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
        setContentView(R.layout.activity_coil_stock);
        this.txtPart = findViewById(R.id.txtPart);
        this.txtContent = findViewById(R.id.txtContent);
        this.listviewCoil = findViewById(R.id.listviewCoil);
        this.txtContent.setText("재고 현황");
        this.imvRefresh = findViewById(R.id.imvRefresh);
        this.imvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationNo = locationMap.get(spinnerLocation.getSelectedItemPosition()).LocationNo;
                getCoilStock(locationNo, "");
            }
        });
        this.spinnerLocation = findViewById(R.id.spinnerLocation);
        getLocationForCoilStock();
    }

    public void getLocationForCoilStock() {
        String url = getString(R.string.service_address) + "getLocationForCoilStock";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", 2);
        //values.put("FromDate", fromDate);
        GetLocationForCoilStock gsod = new GetLocationForCoilStock(url, values);
        gsod.execute();
    }

    public class GetLocationForCoilStock extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetLocationForCoilStock(String url, ContentValues values) {
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
                Location location;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                locationMap = new TreeMap<>();
                ArrayList<String> locationArrayList;
                locationArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityCoilStock.this, ErrorCheck, 2);
                        return;
                    }
                    location = new Location();
                    location.index = i;
                    location.LocationNo = child.getString("LocationNo");
                    location.LocationName = child.getString("LocationName");
                    locationMap.put(i, location);
                    locationArrayList.add(location.LocationName);
                }
                ArrayAdapter<String> zoneAdapter = new ArrayAdapter<>(ActivityCoilStock.this, android.R.layout.simple_spinner_dropdown_item, locationArrayList);
                spinnerLocation.setAdapter(zoneAdapter);
                //spinnerLocation.setMinimumWidth(150);
                //spinnerLocation.setDropDownWidth(150);
                spinnerLocation.setSelection(0);
                spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String locationNo = locationMap.get(position).LocationNo;
                        getCoilStock(locationNo, "");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                /*productionStockAdapter= new ProductionStockAdapter
                        (ActivityCoilStock.this, R.layout.listview_production_stock_row, stockArrayList);
                listviewCoil.setAdapter(productionStockAdapter);*/

            } catch (Exception e) {
                e.printStackTrace();
                progressOFF();

            } finally {
                //progressOFF();
            }
        }
    }

    public void getCoilStock(String locationNo, String partCode) {
        String url = getString(R.string.service_address) + "getCoilStock";
        ContentValues values = new ContentValues();
        values.put("BusinessClassCode", 2);
        values.put("LocationNo", locationNo);
        values.put("PartCode", partCode);
        GetCoilStock gsod = new GetCoilStock(url, values, partCode);
        gsod.execute();
    }

    public class GetCoilStock extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String partCode;

        GetCoilStock(String url, ContentValues values, String partCode) {
            this.url = url;
            this.values = values;
            this.partCode = partCode;
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
                        showErrorDialog(ActivityCoilStock.this, ErrorCheck, 2);
                        return;
                    }
                    stock = new Stock();
                    stock.PartCode = child.getString("PartCode");
                    stock.PartName = child.getString("PartName");
                    stock.Size1 = child.getString("Size1");
                    stock.Size2 = child.getString("Size2");
                    stock.Qty = child.getString("Qty");
                    stock.Weight = child.getString("Weight");
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

                            new MaterialAlertDialogBuilder(ActivityCoilStock.this)
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
                                            String locationNo = locationMap.get(spinnerLocation.getSelectedItemPosition()).LocationNo;
                                            String partCode = partCodeList.get(selectedIndex);

                                            getCoilStock(locationNo, partCode);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String locationNo = locationMap.get(spinnerLocation.getSelectedItemPosition()).LocationNo;
                                            String partCode = partCodeList.get(selectedIndex);

                                            getCoilStock(locationNo, partCode);
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    });
                }
                coilStockAdapter = new CoilStockAdapter
                        (ActivityCoilStock.this, R.layout.listview_coil_stock_row, stockArrayList);
                listviewCoil.setAdapter(coilStockAdapter);

            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                progressOFF();
            }
        }
    }
}
