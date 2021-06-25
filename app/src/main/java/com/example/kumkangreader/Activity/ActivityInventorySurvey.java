package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kumkangreader.Adapter.InventoryAdapter;
import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Object.Inventory;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityInventorySurvey extends BaseActivity {

    ArrayList<Inventory> inventoryArrayList2;
    ListView listViewInventory;
    InventoryAdapter inventoryAdapter;
    ImageView imvQR;
    Spinner spinnerZone;
    Spinner spinnerZoneSeqNo;

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
        setContentView(R.layout.activity_inventory_survey);
        this.imvQR = findViewById(R.id.imvQR);
        this.spinnerZone = findViewById(R.id.spinnerZone);
        this.spinnerZoneSeqNo = findViewById(R.id.spinnerZoneSeqNo);
        this.listViewInventory = findViewById(R.id.listViewInventory);
        this.imvQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(ActivityInventorySurvey.this);
                intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리
                intentIntegrator.initiateScan();
            }
        });
        getZone();

        //this.scrapDataArrayList = (ArrayList<ScrapData>) getIntent().getSerializableExtra("scrapDataArrayList");
        //this.inventoryAdapter=new InventoryAdapter(ActivityInventorySurvey.this, R.layout.listview_inventory_row, scrapDataArrayList, this.itemTag, this.costCenter, mHandler);
        //this.listViewScrap=findViewById(R.id.listviewScrap);
        //this.listViewScrap.setAdapter(this.scrapAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                String scanResult;
                scanResult = result.getContents();
                setInventorySurvey(scanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setInventorySurvey(String itemTag) {
        String url = getString(R.string.service_address) + "setInventorySurvey";
        ContentValues values = new ContentValues();
        values.put("ItemTag", itemTag);
        values.put("Zone", spinnerZone.getSelectedItem().toString());
        values.put("ZoneSeqNo", spinnerZoneSeqNo.getSelectedItem().toString());
        values.put("UserCode", Users.PhoneNumber);
        SetInventorySurvey gsod = new SetInventorySurvey(url, values, itemTag);
        gsod.execute();
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
                        Toast.makeText(ActivityInventorySurvey.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                getInventorySurvey(spinnerZone.getSelectedItem().toString(), spinnerZoneSeqNo.getSelectedItem().toString(), itemTag);
                Toast.makeText(ActivityInventorySurvey.this, "저장 되었습니다.", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }


    public void getZone() {
        String url = getString(R.string.service_address) + "getZone";
        ContentValues values = new ContentValues();
        GetZone gsod = new GetZone(url, values);
        gsod.execute();
    }

    public class GetZone extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetZone(String url, ContentValues values) {
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
                ArrayAdapter<String> zoneArrayAdapter;
                final ArrayList<String> zoneArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityInventorySurvey.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    zoneArrayList.add(child.getString("Zone"));
                }
                zoneArrayAdapter = new ArrayAdapter<>(ActivityInventorySurvey.this, R.layout.list_item, zoneArrayList);
                spinnerZone.setAdapter(zoneArrayAdapter);
                spinnerZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        getZoneSeqNo(spinnerZone.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    public void getZoneSeqNo(String zone) {
        String url = getString(R.string.service_address) + "getZoneSeqNo";
        ContentValues values = new ContentValues();
        values.put("Zone", zone);
        GetZoneSeqNo gsod = new GetZoneSeqNo(url, values);
        gsod.execute();
    }


    public class GetZoneSeqNo extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetZoneSeqNo(String url, ContentValues values) {
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
                ArrayAdapter<String> zoneSeqNoArrayAdapter;
                final ArrayList<String> zoneSeqNoArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityInventorySurvey.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    zoneSeqNoArrayList.add(child.getString("ZoneSeqNo"));
                }
                zoneSeqNoArrayAdapter = new ArrayAdapter<>(ActivityInventorySurvey.this, R.layout.list_item, zoneSeqNoArrayList);
                spinnerZoneSeqNo.setAdapter(zoneSeqNoArrayAdapter);
                spinnerZoneSeqNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        getInventorySurvey(spinnerZone.getSelectedItem().toString(), spinnerZoneSeqNo.getSelectedItem().toString(), "");
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    public void getInventorySurvey(String zone, String zoneSeqNo, String itemTag) {
        String url = getString(R.string.service_address) + "getInventorySurvey";
        ContentValues values = new ContentValues();
        values.put("Zone", zone);
        values.put("ZoneSeqNo", zoneSeqNo);
        GetInventorySurvey gsod = new GetInventorySurvey(url, values, itemTag);
        gsod.execute();
    }


    public class GetInventorySurvey extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTag;

        GetInventorySurvey(String url, ContentValues values, String itemTag) {
            this.url = url;
            this.values = values;
            this.itemTag = itemTag;
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
                Inventory inventory;
                ArrayList<Inventory> inventoryArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityInventorySurvey.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    inventory = new Inventory();
                    inventory.ItemTag = child.getString("ItemTag");
                    inventory.PartName = child.getString("PartName");
                    inventory.PartSpecName = child.getString("PartSpecName");
                    inventory.SeqNo = child.getString("SeqNo");
                    inventory.Qty = child.getString("Qty");
                    inventoryArrayList.add(inventory);
                }
                inventoryAdapter = new InventoryAdapter(ActivityInventorySurvey.this, R.layout.listview_inventory_row, inventoryArrayList, itemTag, mHandler);
                listViewInventory.setAdapter(inventoryAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }
        }
    }

    public Handler mHandler = new Handler() { //다이얼로그 종료시 액티비티 데이터 전송을 위함
        @Override
        public void handleMessage(Message msg) {
            getInventorySurvey(spinnerZone.getSelectedItem().toString(),spinnerZoneSeqNo.getSelectedItem().toString(), "");

        }
    };

    /*public Handler mHandler = new Handler() { //ScrapAdapter 와 통신을 위함
        @Override
        public void handleMessage(Message msg) {
            scrapDataArrayList = (ArrayList<ScrapData>)msg.getData().getSerializable("scrapDataArrayList");
            if(scrapDataArrayList.size()==0){
                Toast.makeText(ActivityInventorySurvey.this, "등록 된 불량품이 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
            scrapAdapter=new ScrapAdapter(ActivityInventorySurvey.this, R.layout.listview_scrap_row, scrapDataArrayList, itemTag, costCenter, mHandler);
            listViewScrap.setAdapter(scrapAdapter);
        }
    };*/

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON(this, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(this, message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }


}