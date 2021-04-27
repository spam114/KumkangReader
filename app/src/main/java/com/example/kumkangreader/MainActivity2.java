package com.example.kumkangreader;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.kumkangreader.Activity.ActivityProductionPerformance;
import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Fragment.FragmentInputCoil;
import com.example.kumkangreader.Fragment.FragmentProduction;
import com.example.kumkangreader.Fragment.FragmentStockOut;
import com.example.kumkangreader.Fragment.FragmentTest;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.ProductionInfo;
import com.example.kumkangreader.Object.StockOut;
import com.example.kumkangreader.Object.Users;
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

    /*Fragment3 fragment3;*/
    ImageView imvQR;
    BackPressControl backpressed;

    private final int REQUEST_STOCKOUT = 1;
    private final int REQUEST_COIL_INPUT = 2;
    private final int REQUEST_PRODUCTION = 3;
    public StockOut stockOut;
    /*ArrayList<StockOutDetail> stockOutDetailArrayList;
    ArrayList<StockOutDetail> scanDataArrayList;*/
    ArrayList<ProductionInfo> productionInfoArrayList;
    TabLayout.Tab firstTab;
    TabLayout.Tab secondTab;
    TabLayout.Tab thirdTab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        backpressed = new BackPressControl(this);
        this.imvQR=findViewById(R.id.imvQR);
        fragmentStockOut = new FragmentStockOut(this);
        fragmentProduction = new FragmentProduction();
 /*       this.stockOutDetailArrayList = new ArrayList<>();
        this.scanDataArrayList = new ArrayList<>();*/
        this.productionInfoArrayList=new ArrayList<>();
        fragmentInputCoil= new FragmentInputCoil();
        fragmentTest= new FragmentTest();
       /* fragment3 = new Fragment3();*/
        tabs = findViewById(R.id.tabs);

        firstTab = tabs.newTab().setText("코일입고").setIcon(R.drawable.outline_donut_small_black_48dp);
        secondTab = tabs.newTab().setText("생산실적").setIcon(R.drawable.outline_build_black_48dp);
        thirdTab = tabs.newTab().setText("출고등록").setIcon(R.drawable.outline_local_shipping_black_48dp);
        tabs.addTab(firstTab);
        tabs.addTab(secondTab);
        tabs.addTab(thirdTab);
/*
        tabs2=findViewById(R.id.tabs2);
        final TabLayout.Tab topTab;
        topTab = tabs2.newTab().setText("코일입고").setIcon(R.drawable.outline_build_white_24dp);
        final TabLayout.Tab topTab2;
        topTab2 = tabs2.newTab().setText("테스트").setIcon(R.drawable.outline_build_white_24dp);
        tabs2.addTab(topTab);
        tabs2.addTab(topTab2);*/



        if(Users.authorityList.contains(0) || Users.authorityList.contains(1)){//0:관리자 1:생산
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentProduction).commit();//첫실행 fragment
            firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);
            secondTab.setIcon(R.drawable.baseline_build_black_48dp);
            thirdTab.setIcon(R.drawable.outline_local_shipping_black_48dp);
            tabs.selectTab(secondTab);
        }
        else if(Users.authorityList.contains(2)) {//2:출고권한만 가졌을시
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragmentStockOut).commit();//첫실행 fragment
            firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);
            secondTab.setIcon(R.drawable.outline_build_black_48dp);
            thirdTab.setIcon(R.drawable.baseline_local_shipping_black_48dp);
            tabs.selectTab(thirdTab);
        }



        /*tabs.addTab(tabs.newTab().setText("출하"));*/

       /* tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

        });*/

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                if(position == 0) {
                    selected = fragmentInputCoil;
                    firstTab.setIcon(R.drawable.baseline_donut_small_black_48dp);

                }
                else if(position == 1) {
                    selected = fragmentProduction;
                    secondTab.setIcon(R.drawable.baseline_build_black_48dp);

                }
                else if(position == 2) {
                    selected = fragmentStockOut;
                    thirdTab.setIcon(R.drawable.baseline_local_shipping_black_48dp);
                }
                /*else if(position == 2)
                    selected = fragment3;*/
                getSupportFragmentManager().beginTransaction().replace(R.id.container, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0) {
                    firstTab.setIcon(R.drawable.outline_donut_small_black_48dp);
                }
                else if(position == 1) {
                    secondTab.setIcon(R.drawable.outline_build_black_48dp);
                }
                else if(position == 2) {
                    thirdTab.setIcon(R.drawable.outline_local_shipping_black_48dp);
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
                intentIntegrator.setBeepEnabled(false);//바코드 인식시 소리
                if(firstTab.isSelected()){//코일입고
                    //intentIntegrator.(MAIN_COIL_INPUT);
                    intentIntegrator.setPrompt(getString(R.string.qr_state_coil_input));
                }
                else if(secondTab.isSelected()){//생산실적
                  //  intentIntegrator.setRequestCode(MAIN_PRODUCTION);
                    intentIntegrator.setPrompt(getString(R.string.qr_state_production));
                }
                else{//출고등록
                   // intentIntegrator.setRequestCode(MAIN_STOCKOUT);
                    intentIntegrator.setPrompt(getString(R.string.qr_state_stockoutmaster));
                }
                // intentIntegrator.setCaptureActivity(QRReaderActivityStockOutMaster.class);
                intentIntegrator.initiateScan();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_STOCKOUT) {//ActivityStockOut에서 돌아옴
           // this.productionInfoArrayList = new ArrayList<>();
   /*         this.stockOutDetailArrayList = new ArrayList<>();
            this.scanDataArrayList = new ArrayList<>();*/
        }
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_LONG).show();
            } else {
                String scanResult;
                scanResult = result.getContents();

                if(firstTab.isSelected()) {//코일입고
                    Toast.makeText(this, scanResult,Toast.LENGTH_LONG).show();//Test 용
                }
                else if(secondTab.isSelected()) {//생산실적
                    String[] array=scanResult.split("/");
                    String worksOrderNo=array[0];
                    String costCenter = array[1];
                    String url=getString(R.string.service_address) + "getProductionBasicInfo";
                    ContentValues values = new ContentValues();
                    values.put("WorksOrderNo", worksOrderNo);
                    values.put("CostCenter", costCenter);
                    GetProductionBasicInfo gsod = new GetProductionBasicInfo(url, values);
                    gsod.execute();

                }
                else {//출고등록
                    fragmentStockOut.callGetStockOutMaster(scanResult);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public class GetProductionBasicInfo extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        GetProductionBasicInfo(String url, ContentValues values){
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
                String worksOrderNo;
                String costCenterName;

                ProductionInfo productionInfo;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                productionInfoArrayList=new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    productionInfo = new ProductionInfo(
                            child.getString("OutPutQty"),
                            child.getString("CostCenter"),
                            child.getString("CostCenterName"),
                            child.getString("WorksOrderNo"),
                            child.getString("InputQty"),
                            child.getString("IssueOutPutQty"));

                    productionInfoArrayList.add(productionInfo);
                }
                worksOrderNo=productionInfoArrayList.get(0).WorksOrderNo;
                costCenterName=productionInfoArrayList.get(0).CostCenterName;
                /*int totalQty = 0;
                int totalScanQty = 0;
                for (int j = 0; j < stockOutDetailArrayList.size(); j++) {
                    totalQty += Integer.parseInt(stockOutDetailArrayList.get(j).OutQty);
                    totalScanQty += Integer.parseInt(stockOutDetailArrayList.get(j).ScanQty);
                }*/

                Intent i = new Intent(getBaseContext(), ActivityProductionPerformance.class);
                i.putExtra("productionInfoArrayList", productionInfoArrayList);
                i.putExtra("worksOrderNo",worksOrderNo);
                i.putExtra("costCenterName",costCenterName);
                startActivityForResult(i, REQUEST_PRODUCTION);

            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
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
