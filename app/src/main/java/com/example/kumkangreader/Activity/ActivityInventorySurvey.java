package com.example.kumkangreader.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kumkangreader.Adapter.InventoryAdapter;
import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Object.Inventory;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.textfield.TextInputEditText;
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
    TextInputEditText edtNumber;

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
                intentIntegrator.setBeepEnabled(true);//????????? ????????? ??????
                intentIntegrator.initiateScan();
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.edtNumber=findViewById(R.id.edtNumber);
        this.edtNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edtNumber.setGravity(Gravity.START);
                }
                else{
                    edtNumber.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        });
        this.edtNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO
                    setInventorySurvey("EI-"+v.getText().toString());
                }
                return false;
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
                Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_LONG).show();
                //showErrorDialog(this, "?????? ???????????????.",2);
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
        values.put("UserCode", Users.UserID);
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
            //progress bar??? ???????????? ????????? ??????
            startProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????

            try {
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        //showErrorDialog(ActivityInventorySurvey.this, ErrorCheck,2);
                        Toast.makeText(ActivityInventorySurvey.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
                getInventorySurvey(spinnerZone.getSelectedItem().toString(), spinnerZoneSeqNo.getSelectedItem().toString(), itemTag);
                //showErrorDialog(ActivityInventorySurvey.this, "?????? ???????????????.",1);
                //Toast.makeText(ActivityInventorySurvey.this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();

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
            //progress bar??? ???????????? ????????? ??????
            startProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????

            try {
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                ArrayAdapter<String> zoneArrayAdapter;
                final ArrayList<String> zoneArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityInventorySurvey.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog(ActivityInventorySurvey.this, ErrorCheck,2);
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
            //progress bar??? ???????????? ????????? ??????
            //startProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????

            try {
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                ArrayAdapter<String> zoneSeqNoArrayAdapter;
                final ArrayList<String> zoneSeqNoArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(ActivityInventorySurvey.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                        //showErrorDialog (ActivityInventorySurvey.this, ErrorCheck,2);
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
            //progress bar??? ???????????? ????????? ??????
            //startProgress();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);
            return result; // ????????? ????????? ????????????. ?????? onPostExecute()??? ??????????????? ???????????????.
        }

        @Override
        protected void onPostExecute(String result) {
            // ????????? ???????????? ???????????????.
            // ????????? ?????? UI ?????? ?????? ????????? ?????????

            try {
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                Inventory inventory;
                ArrayList<Inventory> inventoryArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        //showErrorDialog(ActivityInventorySurvey.this, ErrorCheck,2);
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

    public Handler mHandler = new Handler() { //??????????????? ????????? ???????????? ????????? ????????? ??????
        @Override
        public void handleMessage(Message msg) {
            getInventorySurvey(spinnerZone.getSelectedItem().toString(),spinnerZoneSeqNo.getSelectedItem().toString(), "");

        }
    };

    /*public Handler mHandler = new Handler() { //ScrapAdapter ??? ????????? ??????
        @Override
        public void handleMessage(Message msg) {
            scrapDataArrayList = (ArrayList<ScrapData>)msg.getData().getSerializable("scrapDataArrayList");
            if(scrapDataArrayList.size()==0){
                Toast.makeText(ActivityInventorySurvey.this, "?????? ??? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
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