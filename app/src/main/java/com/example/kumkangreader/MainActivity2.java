package com.example.kumkangreader;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.kumkangreader.Activity.ActivityMoveCoil2;
import com.example.kumkangreader.Activity.ActivityProductionPerformance;
import com.example.kumkangreader.Activity.ActivityStockOutNew;
import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Fragment.FragmentInputCoil;
import com.example.kumkangreader.Fragment.FragmentProduction;
import com.example.kumkangreader.Fragment.FragmentStockOut;
import com.example.kumkangreader.Fragment.FragmentStopOperation;
import com.example.kumkangreader.Fragment.FragmentTest;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.Coil;
import com.example.kumkangreader.Object.ProductionInfo;
import com.example.kumkangreader.Object.StockOut;
import com.example.kumkangreader.Object.Users;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity2 extends FragmentActivity implements BaseActivityInterface {
    TabLayout tabs;
    //TabLayout tabs2;
    FragmentStockOut fragmentStockOut;
    FragmentProduction fragmentProduction;
    FragmentInputCoil fragmentInputCoil;
    FragmentTest fragmentTest;
    FragmentStopOperation fragmentStopOperation;
    boolean testFlag=false;
    ImageView imageView5;

    /*Fragment3 fragment3;*/
    ImageView imvQR;
    BackPressControl backpressed;

    private final int REQUEST_STOCKOUT = 1;
    private final int REQUEST_COIL_INPUT = 2;
    private final int REQUEST_PRODUCTION = 3;
    private final int TEST_INT=99;
    public StockOut stockOut;
    /*ArrayList<StockOutDetail> stockOutDetailArrayList;
    ArrayList<StockOutDetail> scanDataArrayList;*/
    ArrayList<ProductionInfo> productionInfoArrayList;
    TabLayout.Tab firstTab;
    TabLayout.Tab secondTab;
    TabLayout.Tab thirdTab;
    TabLayout.Tab fourthTab;

    TextView textView7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        backpressed = new BackPressControl(this);
        this.imvQR = findViewById(R.id.imvQR);
        fragmentStockOut = new FragmentStockOut(this);
        fragmentProduction = new FragmentProduction(this);
        fragmentStopOperation =  new FragmentStopOperation(this);
 /*       this.stockOutDetailArrayList = new ArrayList<>();
        this.scanDataArrayList = new ArrayList<>();*/
        this.productionInfoArrayList = new ArrayList<>();
        fragmentInputCoil = new FragmentInputCoil(this);
        fragmentTest = new FragmentTest();
        /* fragment3 = new Fragment3();*/
        tabs = findViewById(R.id.tabs);
        imageView5=findViewById(R.id.imageView5);
        textView7=findViewById(R.id.textView7);//????????????

        /*if(Users.authorityList.contains(0)){//????????? ????????? ?????????,
            textView7.setText(" ????????????(??????)");
        }

        textView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Users.authorityList.contains(0)){//????????? ????????? ?????????,
                    Intent i = new Intent(getBaseContext(), ActivityInventorySurvey.class);
                    startActivity(i);
                }
            }
        });*/

        //????????????
        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(MainActivity2.this);
                //alertBuilder.setIcon(R.drawable.ic_launcher);
                //alertBuilder.setTitle(partName + "(" + partSpecName + ")");
                // List Adapter ??????
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity2.this,
                        android.R.layout.simple_list_item_1);

                try {
                    String content="\n??????????????????: "+(String) getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(getPackageManager().getPackageInfo(getPackageName(), 0).packageName, PackageManager.GET_UNINSTALLED_PACKAGES))+"\n"+
                            "??????: "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName+"\n"+
                            "???????????????: "+Users.PhoneNumber+"\n"+
                            "????????????: "+Users.UserName+"\n"+
                            "??????: ";
                    for(int i=0;i<Users.authorityNameList.size();i++){
                        content+=Users.authorityNameList.get(i).toString()+", ";
                    }
                    content=content.substring(0,content.length()-2)+"\n";
                    content+="?????????: "+Users.WorkClassName;

                    adapter.add(content);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                // ?????? ??????
                alertBuilder.setNegativeButton("??????",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                // Adapter ??????
                alertBuilder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        });
                alertBuilder.show();


            }
        });

        firstTab = tabs.newTab().setText("????????????").setIcon(R.drawable.outline_donut_small_black_48dp);
        secondTab = tabs.newTab().setText("????????????").setIcon(R.drawable.outline_build_black_48dp);
        thirdTab = tabs.newTab().setText("????????????").setIcon(R.drawable.outline_local_shipping_black_48dp);
        fourthTab = tabs.newTab().setText("?????????").setIcon(R.drawable.outline_do_not_disturb_on_black_24);
        tabs.addTab(firstTab);
        tabs.addTab(secondTab);
        tabs.addTab(fourthTab);
        tabs.addTab(thirdTab);

/*
        tabs2=findViewById(R.id.tabs2);
        final TabLayout.Tab topTab;
        topTab = tabs2.newTab().setText("????????????").setIcon(R.drawable.outline_build_white_24dp);
        final TabLayout.Tab topTab2;
        topTab2 = tabs2.newTab().setText("?????????").setIcon(R.drawable.outline_build_white_24dp);
        tabs2.addTab(topTab);
        tabs2.addTab(topTab2);*/

        firstTab.view.setClickable(false);
        secondTab.view.setClickable(false);
        thirdTab.view.setClickable(false);
        fourthTab.view.setClickable(false);

        if (Users.authorityList.contains(0) || Users.authorityList.contains(1)) {//0:????????? 1:??????
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentProduction).commit();//????????? fragment
            firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);//????????????
            secondTab.setIcon(R.drawable.baseline_build_black_48dp);//????????????
            thirdTab.setIcon(R.drawable.outline_local_shipping_black_48dp);//????????????
            fourthTab.setIcon(R.drawable.outline_do_not_disturb_on_black_24);//?????????
            tabs.selectTab(secondTab);
        }
        else if (Users.authorityList.contains(2)) {//2:??????????????? ????????????
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentStockOut).commit();//????????? fragment
            firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);
            secondTab.setIcon(R.drawable.outline_build_black_48dp);
            thirdTab.setIcon(R.drawable.baseline_local_shipping_black_48dp);
            fourthTab.setIcon(R.drawable.outline_do_not_disturb_on_black_24);
            tabs.selectTab(thirdTab);
        }


        if(Users.authorityList.contains(0)){
            firstTab.view.setClickable(true);
            secondTab.view.setClickable(true);
            thirdTab.view.setClickable(true);
            fourthTab.view.setClickable(true);
        }
        if(Users.authorityList.contains(1)){
            firstTab.view.setClickable(true);
            secondTab.view.setClickable(true);
            fourthTab.view.setClickable(true);
        }
        if(Users.authorityList.contains(2)){
            thirdTab.view.setClickable(true);
        }


        /*tabs.addTab(tabs.newTab().setText("??????"));*/

       /* tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

        });*/

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                imvQR.setVisibility(View.VISIBLE);
                Fragment selected = null;
                if (position == 0) {
                    selected = fragmentInputCoil;
                    firstTab.setIcon(R.drawable.baseline_donut_small_black_48dp);

                } else if (position == 1) {
                    selected = fragmentProduction;
                    secondTab.setIcon(R.drawable.baseline_build_black_48dp);

                } else if (position == 3) {
                    selected = fragmentStockOut;
                    thirdTab.setIcon(R.drawable.baseline_local_shipping_black_48dp);
                }
                else if (position==2){
                    imvQR.setVisibility(View.INVISIBLE);
                    selected = fragmentStopOperation;
                    fourthTab.setIcon(R.drawable.baseline_do_not_disturb_on_black_24);
                }
                /*else if(position == 2)
                    selected = fragment3;*/
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);
                } else if (position == 1) {
                    secondTab.setIcon(R.drawable.outline_build_black_48dp);
                } else if (position == 3) {
                    thirdTab.setIcon(R.drawable.outline_local_shipping_black_48dp);
                }
                else if (position == 2) {
                    fourthTab.setIcon(R.drawable.outline_do_not_disturb_on_black_24);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
               /* int position = tab.getPosition();
                if(position == 0) {

                }
                else if(position == 1) {

                }*/
            }
        });


    /*    tabs2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0) {
                    selected = fragmentInputCoil;
                    topTab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                }
                else if(position == 1) {
                    selected = fragmentTest;
                    topTab2.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
                }
                *//*else if(position == 2)
                    selected = fragment3;*//*
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0) {
                    topTab.getIcon().setColorFilter(Color.parseColor("#7E868C"), PorterDuff.Mode.SRC_IN);
                }
                else if(position == 1) {
                    topTab2.getIcon().setColorFilter(Color.parseColor("#7E868C"), PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/


        imvQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity2.this);
                intentIntegrator.setBeepEnabled(true);//????????? ????????? ??????
                if (firstTab.isSelected()) {//????????????
                    //intentIntegrator.(MAIN_COIL_INPUT);
                    intentIntegrator.setPrompt(getString(R.string.qr_state_coil_input));
                } else if (secondTab.isSelected()) {//????????????
                    //  intentIntegrator.setRequestCode(MAIN_PRODUCTION);
                    intentIntegrator.setPrompt(getString(R.string.qr_state_production));
                }  else if (thirdTab.isSelected()) {//????????????
                    // intentIntegrator.setRequestCode(MAIN_STOCKOUT);
                    intentIntegrator.setPrompt(getString(R.string.qr_state_stockoutmaster));
                }
                else{//?????????

                }

                // intentIntegrator.setCaptureActivity(QRReaderActivityStockOutMaster.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_STOCKOUT) {//ActivityStockOut?????? ?????????
            // this.productionInfoArrayList = new ArrayList<>();
   /*         this.stockOutDetailArrayList = new ArrayList<>();
            this.scanDataArrayList = new ArrayList<>();*/
        }
        if(testFlag){
            //Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            showErrorDialog(this, result.getContents(),2);
            testFlag=false;
            return;
        }

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                //showErrorDialog(this, "?????? ???????????????.",1);
            } else {
                String scanResult;
                scanResult = result.getContents();
                if (firstTab.isSelected()) {//????????????
                    inputCoil(scanResult);
                } else if (secondTab.isSelected()) {
                    //????????????
                    try {
                        judgeItemTagOrWorksOrder(scanResult);
                    } catch (ArrayIndexOutOfBoundsException aoe) {
                        //Toast.makeText(this, "?????? ???????????? TAG ?????????.", Toast.LENGTH_SHORT).show();
                        showErrorDialog(this, "?????? ???????????? TAG ?????????.",2);
                        progressOFF();
                    }
                    finally {

                    }
                } else {//????????????
                    //fragmentStockOut.callGetStockOutMaster(scanResult); //????????????
                    getStockOutMaster(scanResult);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * ?????? ?????? ?????? ????????? ???????????? ????????????. ?????? ???, ?????? ????????? ?????? ??? ??????
     * ?????? ?????? ?????? ????????? ?????? : 1
     * ?????? ?????? : 2
     * @param scanResult
     */
    public void judgeItemTagOrWorksOrder(String scanResult){
        String url=getString(R.string.service_address) + "judgeItemTagOrWorksOrder";
        ContentValues values = new ContentValues();
        values.put("ScanInput", scanResult);
        JudgeItemTagOrWorksOrder gsom = new JudgeItemTagOrWorksOrder(url, values, scanResult);
        gsom.execute();
    }

    public class JudgeItemTagOrWorksOrder extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String scanResult;
        JudgeItemTagOrWorksOrder(String url, ContentValues values, String scanResult){
            this.url = url;
            this.values = values;
            this.scanResult=scanResult;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar??? ???????????? ?????? ??????
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
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                String type="";

                JSONObject child = jsonArray.getJSONObject(0);

                if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                    ErrorCheck = child.getString("ErrorCheck");
                    //Toast.makeText(MainActivity2.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                    showErrorDialog(MainActivity2.this, ErrorCheck,2);
                    return;
                }
                type = child.getString("Type");

                if(type.equals("1")){//???????????? ??????
                    getProductionBasicInfo(child.getString("WorksOrderNo")+"/"+child.getString("CostCenter"), child.getString("ItemTag"));
                }
                else{
                    getProductionBasicInfo(scanResult, "");
                }
            }
            catch (ArrayIndexOutOfBoundsException aoe){
                showErrorDialog(MainActivity2.this, "?????? ???????????? TAG ?????????.",2);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }


    public void inputCoil(String scanResult){
        String url = getString(R.string.service_address) + "inputCoil";
        ContentValues values = new ContentValues();
        values.put("CoilNo", scanResult);
        values.put("BusinessClassCode", "2");
        values.put("UserCode", Users.UserID);
        values.put("Zone", "A");//???????????? zone A
        InputCoil gsod = new InputCoil(url, values);
        gsod.execute();
    }



    public void getStockOutMaster(String scanResult){
        String url=getString(R.string.service_address) + "getStockOutMaster";
        ContentValues values = new ContentValues();
        values.put("ScanInput", scanResult);
        GetStockOutMaster gsom = new GetStockOutMaster(url, values, scanResult);
        gsom.execute();
    }


    public class GetStockOutMaster extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String stockOutNo;
        GetStockOutMaster(String url, ContentValues values, String stockOutNo){
            this.url = url;
            this.values = values;
            this.stockOutNo=stockOutNo;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar??? ???????????? ?????? ??????
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
                //Log.i("ReadJSONFeedTask", result);
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                String StockOutNo = "";
                String CustomerLocation = "";
                String AreaCarNumber = "";

                JSONObject child = jsonArray.getJSONObject(0);

                if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                    ErrorCheck = child.getString("ErrorCheck");
                    //Toast.makeText(MainActivity2.this, ErrorCheck, Toast.LENGTH_SHORT).show();
                    showErrorDialog(MainActivity2.this, ErrorCheck,2);
                    return;
                }
                StockOutNo = child.getString("StockOutNo");
                CustomerLocation = child.getString("CustomerLocation");
                AreaCarNumber = child.getString("AreaCarNumber");

                stockOut = new StockOut();
                stockOut.StockOutNo = StockOutNo;
                stockOut.CustomerLocation = CustomerLocation;
                stockOut.AreaCarNumber = AreaCarNumber;

                Intent i = new Intent(getBaseContext(), ActivityStockOutNew.class);
                i.putExtra("stockOut", stockOut);
                startActivityForResult(i, REQUEST_STOCKOUT);

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }


    public class InputCoil extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        InputCoil(String url, ContentValues values) {
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

                //ProductionInfo productionInfo;
                Coil coil;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                String coilNo="";
                String partCode="";
                String partSpec="";
                String locationNo="";
                String maxRow="";
                String maxCol="";
                ArrayList<String> coilArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(MainActivity2.this, ErrorCheck,2);
                        return;
                    }
                    else if(!child.getString("DuplicatedCoilNo").equals("null")){//????????? ????????? ????????????.
                        coilArrayList.add(child.getString("DuplicatedCoilNo"));
                        continue;
                    }

                    coilNo = child.getString("CoilNo");
                    partCode = child.getString("PartCode");
                    partSpec = child.getString("PartSpec");
                    locationNo = child.getString("LocationNo");
                    maxRow = child.getString("MaxRow");
                    maxCol = child.getString("MaxCol");
                }

                if(coilArrayList.size()>0){
                    //??????????????? ????????? ?????? ????????? ??????

                    final String coilArr[]= new String[coilArrayList.size()];

                    for (int i = 0; i < coilArrayList.size(); i++) {
                        coilArr[i]=coilArrayList.get(i);
                    }
                    MaterialAlertDialogBuilder alertBuilder= new MaterialAlertDialogBuilder(MainActivity2.this);
                    alertBuilder.setTitle("???????????? ??????");
                    final String[] selectedCoil = {""};

                    alertBuilder.setItems(coilArr,  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedCoil[0] =coilArr[which];
                            inputCoil(selectedCoil[0]);
                        }
                    });
                    alertBuilder.show();
                    progressOFF();
                    return;
                }

                Intent i = new Intent(getBaseContext(), ActivityMoveCoil2.class);
                i.putExtra("coilNo", coilNo);
                i.putExtra("partCode", partCode);
                i.putExtra("partSpec", partSpec);
                startActivity(i);

                Toast.makeText(getBaseContext(), "?????? ????????? ?????????????????????.\n??????????????? ???????????? ?????????.", Toast.LENGTH_LONG).show();
                //showErrorDialog(MainActivity2.this, "?????? ????????? ?????????????????????.\n??????????????? ???????????? ?????????.",1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }






    public void getProductionBasicInfo(String scanResult, String itemTag){
        String[] array = scanResult.split("/");
        String worksOrderNo = array[0];
        String costCenter = array[1];
        String url = getString(R.string.service_address) + "getProductionBasicInfo";
        ContentValues values = new ContentValues();
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetProductionBasicInfo gsod = new GetProductionBasicInfo(url, values, itemTag);
        gsod.execute();
    }

    public class GetProductionBasicInfo extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String itemTag;

        GetProductionBasicInfo(String url, ContentValues values, String itemTag) {
            this.url = url;
            this.values = values;
            this.itemTag=itemTag;
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
                String worksOrderNo;
                String costCenter;
                String costCenterName;
                String locationNo;

                ProductionInfo productionInfo;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                productionInfoArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//????????? ?????? ???, ?????? ????????? ?????? ??? ??????
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(MainActivity2.this, ErrorCheck,2);
                        return;
                    }
                    productionInfo = new ProductionInfo(
                            child.getString("OutputQty"),
                            child.getString("OutputQtyBD"),
                            child.getString("CostCenter"),
                            child.getString("CostCenterName"),
                            child.getString("WorksOrderNo"),
                            child.getString("InputQty"),
                            child.getString("InputQtyBD"),
                            child.getString("IssueOutputQty"),
                            child.getString("IssueOutputQtyBD"),
                            child.getString("LocationNo"),
                            child.getString("ScrappedQty"),
                            child.getString("CenterSpec"),
                            child.getString("PartName"),
                            child.getString("PartSpecName")
                    );

                    productionInfoArrayList.add(productionInfo);
                }
                worksOrderNo = productionInfoArrayList.get(0).WorksOrderNo;
                costCenter = productionInfoArrayList.get(0).CostCenter;
                costCenterName = productionInfoArrayList.get(0).CostCenterName;
                locationNo = productionInfoArrayList.get(0).LocationNo;
                /*int totalQty = 0;
                int totalScanQty = 0;
                for (int j = 0; j < stockOutDetailArrayList.size(); j++) {
                    totalQty += Integer.parseInt(stockOutDetailArrayList.get(j).OutQty);
                    totalScanQty += Integer.parseInt(stockOutDetailArrayList.get(j).ScanQty);
                }*/

                Intent i = new Intent(getBaseContext(), ActivityProductionPerformance.class);
                i.putExtra("productionInfoArrayList", productionInfoArrayList);
                i.putExtra("worksOrderNo", worksOrderNo);
                i.putExtra("costCenter", costCenter);
                i.putExtra("costCenterName", costCenterName);
                i.putExtra("locationNo", locationNo);
                i.putExtra("itemTag", itemTag);

                startActivityForResult(i, REQUEST_PRODUCTION);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //progressOFF();
            }

        }
    }

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

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }


    private void startProgress() {
        progressON("Loading...");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);
    }

    @Override
    public void onBackPressed() {

        backpressed.onBackPressed();
    }
}
