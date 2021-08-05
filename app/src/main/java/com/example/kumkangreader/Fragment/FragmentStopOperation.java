package com.example.kumkangreader.Fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kumkangreader.Activity.ActivityCostCenterStopOperations;
import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Dialog.KeyInDialog;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.CostCenter;
import com.example.kumkangreader.Object.NonOperationClassCodes;
import com.example.kumkangreader.Object.NonOperationCodes;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.PreferenceManager;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentStopOperation extends Fragment  implements BaseActivityInterface {
    //TextInputEditText edtScan;
    Context context;
    Spinner costCenterSpinner;
    Spinner nonOperationClassCodesSpinner;
    Spinner nonOperationCodeSpinner;
    TextView txtState;
    TextInputLayout menu;
    LinearLayout layoutCostCenter;
    LinearLayout layoutClass;
    LinearLayout layoutClass2;
    LinearLayout layoutOnlyView;
    LinearLayout layoutOnlyView2;
    LinearLayout layoutTime;
    TextView txtCurrentClass;
    TextView txtCurrentCode;
    //TextView txtTest;
    Chronometer chronometer;
    Button btnStop;
    Button btnStart;
    Button btnKeyIn;
    Button btnSearch;
    ArrayList<CostCenter> costCenterArrayList2;
    ArrayList<NonOperationClassCodes> nonOperationClassCodesArrayList2;
    ArrayList<NonOperationCodes> nonOperationCodeArrayList2;

    public void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);
    }

    public FragmentStopOperation(){

    }

    public FragmentStopOperation(Context context){
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout4, container, false);
        this.costCenterSpinner= rootView.findViewById(R.id.costCenterSpinner);
        this.nonOperationClassCodesSpinner = rootView.findViewById(R.id.nonOperationClassCodesSpinner);
        this.nonOperationCodeSpinner=rootView.findViewById(R.id.nonOperationCodeSpinner);
        this.txtState=rootView.findViewById(R.id.txtState);
        this.layoutCostCenter=rootView.findViewById(R.id.layoutCostCenter);
        this.layoutClass=rootView.findViewById(R.id.layoutClass);
        this.layoutClass2=rootView.findViewById(R.id.layoutClass2);
        this.layoutOnlyView=rootView.findViewById(R.id.layoutOnlyView);
        this.layoutOnlyView2=rootView.findViewById(R.id.layoutOnlyView2);
        this.layoutTime=rootView.findViewById(R.id.layoutTime);
        this.txtCurrentClass=rootView.findViewById(R.id.txtCurrentClass);
        this.txtCurrentCode=rootView.findViewById(R.id.txtCurrentCode);
        //this.txtTest=rootView.findViewById(R.id.txtTest);
        this.chronometer = rootView.findViewById(R.id.chronometer);
        this.btnStart=rootView.findViewById(R.id.btnStart);
        this.btnKeyIn=rootView.findViewById(R.id.btnKeyIn);
        this.btnSearch=rootView.findViewById(R.id.btnSearch);
        this.btnStop=rootView.findViewById(R.id.btnStop);

        this.btnStart.setOnClickListener(new View.OnClickListener() {//현재 가동상태
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(context, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("비가동 시작")
                        .setMessage("'"+costCenterArrayList2.get(costCenterSpinner.getSelectedItemPosition()).CostCenterName+"' 의 비가동을 시작하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton
                                ("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(nonOperationCodeArrayList2.get(nonOperationCodeSpinner.getSelectedItemPosition()).NonOperationCodeName.equals("등록안됨")){
                                            //Toast.makeText(context, "비가동 코드가 등록되어있지 않습니다. 관리자에게 문의하시기 바랍니다.", Toast.LENGTH_SHORT).show();
                                            showErrorDialog(context, "비가동 코드가 등록되어있지 않습니다. 관리자에게 문의하시기 바랍니다.",2);
                                            return;
                                        }
                                        setCostCenterStopOperations(costCenterArrayList2.get(costCenterSpinner.getSelectedItemPosition()).CostCenter, "2",
                                                nonOperationCodeArrayList2.get(nonOperationCodeSpinner.getSelectedItemPosition()).NonOperationCode);
                                    }
                                })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        this.btnStop.setOnClickListener(new View.OnClickListener() {//현재 비가동상태
            @Override
            public void onClick(View v) {

                new MaterialAlertDialogBuilder(context, R.style.Body_ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                        .setTitle("비가동 종료")
                        .setMessage("'"+costCenterArrayList2.get(costCenterSpinner.getSelectedItemPosition()).CostCenterName+"' 의 비가동을 종료하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton
                                ("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setCostCenterStopOperations(costCenterArrayList2.get(costCenterSpinner.getSelectedItemPosition()).CostCenter, "1",
                                                nonOperationCodeArrayList2.get(nonOperationCodeSpinner.getSelectedItemPosition()).NonOperationCode);
                                    }
                                })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        this.btnSearch.setOnClickListener(new View.OnClickListener() {//내역조회
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ActivityCostCenterStopOperations.class);
                i.putExtra("costCenter", costCenterArrayList2.get(costCenterSpinner.getSelectedItemPosition()).CostCenter);
                i.putExtra("costCenterName", costCenterArrayList2.get(costCenterSpinner.getSelectedItemPosition()).CostCenterName);
                startActivity(i);
            }
        });

        this.btnKeyIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nonOperationCodeArrayList2.get(nonOperationCodeSpinner.getSelectedItemPosition()).NonOperationCodeName.equals("등록안됨")){
                    showErrorDialog(context, "비가동 코드가 등록되어있지 않습니다. 관리자에게 문의하시기 바랍니다.",2);
                    return;
                }

                KeyInDialog keyInDialog = new KeyInDialog(context,"비가동 등록", costCenterSpinner.getSelectedItem().toString()+": "
                +nonOperationClassCodesSpinner.getSelectedItem().toString()+"("+nonOperationCodeSpinner.getSelectedItem().toString()+")",
                        costCenterArrayList2.get(costCenterSpinner.getSelectedItemPosition()).CostCenter,
                        nonOperationCodeArrayList2.get(nonOperationCodeSpinner.getSelectedItemPosition()).NonOperationCode);
                //window.setLayout( WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams params2 = keyInDialog.getWindow().getAttributes();
                //params2.width = WindowManager.LayoutParams.MATCH_PARENT;
                //params2.height = WindowManager.LayoutParams.MATCH_PARENT;
                keyInDialog.getWindow().setAttributes(params2);
                keyInDialog.setCancelable(false);
                keyInDialog.show();
            }
        });

        getCostCenter();


      /*  this.edtScan=rootView.findViewById(R.id.edtScan);
        //this.edtScan.setText("WP0-210520-001/WP-S01");
        this.edtScan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edtScan.setGravity(Gravity.START);
                }
                else{
                    edtScan.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        });

        this.edtScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO
                    ((MainActivity2)(context)).getProductionBasicInfo(v.getText().toString());
                }
                return false;
            }
        });*/


        return rootView;
    }

    public void setCostCenterStopOperations(String costCenter, String currentState, String nonOperationCode) {
        String url = getString(R.string.service_address) + "setCostCenterStopOperations";
        ContentValues values = new ContentValues();
        values.put("CostCenter", costCenter);
        values.put("CurrentState", currentState);
        values.put("DayFlag", Users.WorkClassName);
        values.put("NonOperationCode", nonOperationCode);
        values.put("UserCode", Users.UserID);
        SetCostCenterStopOperations gsod = new SetCostCenterStopOperations(url, values);
        gsod.execute();
    }

    public class SetCostCenterStopOperations extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        SetCostCenterStopOperations(String url, ContentValues values) {
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
                String ReturnMessage="";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_LONG).show();
                        //showErrorDialog(context, ErrorCheck,2);
                        return;
                    }
                    ReturnMessage=child.getString("ReturnMessage");
                }
                Toast.makeText(context, ReturnMessage, Toast.LENGTH_LONG).show();
                //showErrorDialog(context, ReturnMessage,2);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                getCostCenter();
                progressOFF();
            }
        }
    }



    public void getCostCenter() {
        String url = getString(R.string.service_address) + "getCostCenter";
        ContentValues values = new ContentValues();
        //values.put("WorksOrderNo", worksOrderNo);
        //values.put("CostCenter", costCenter);
        GetCostCenter gsod = new GetCostCenter(url, values);
        gsod.execute();
    }

    public class GetCostCenter extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetCostCenter(String url, ContentValues values) {
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
                CostCenter costCenter;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                ArrayAdapter<String> costCenterArrayAdapter;
                final ArrayList<String> costCenterArrayList= new ArrayList<>();
                costCenterArrayList2= new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_LONG).show();
                        //showErrorDialog(context, ErrorCheck,2);
                        return;
                    }
                    costCenter = new CostCenter();
                    costCenter.CostCenter = child.getString("CostCenter");
                    costCenter.CostCenterName = child.getString("CostCenterName");
                    costCenterArrayList.add(costCenter.CostCenterName);
                    costCenterArrayList2.add(costCenter);
                }
                costCenterArrayAdapter = new ArrayAdapter<>(context, R.layout.list_item, costCenterArrayList);
                costCenterSpinner.setAdapter(costCenterArrayAdapter);
                costCenterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.stop();
                        getCostCenterStopOperations(costCenterArrayList2.get(position).CostCenter);
                        PreferenceManager.setInt(context, "costCenterIndex", position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                int costCenterIndex = PreferenceManager.getInt(context, "costCenterIndex");
                costCenterSpinner.setSelection(costCenterIndex);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }
        }
    }


    public void getCostCenterStopOperations(String costCenter) {
        String url = getString(R.string.service_address) + "getCostCenterStopOperations";
        ContentValues values = new ContentValues();
        //values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", costCenter);
        GetCostCenterStopOperations gsod = new GetCostCenterStopOperations(url, values, costCenter);
        gsod.execute();
    }


    public class GetCostCenterStopOperations extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String costCenter;

        GetCostCenterStopOperations(String url, ContentValues values, String costCenter) {
            this.url = url;
            this.values = values;
            this.costCenter=costCenter;
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
                if(jsonArray.length()==0){//데이터가 없으면 가동상태
                    txtState.setText("가동");
                    txtState.setTextColor(Color.BLUE);
                    layoutClass.setVisibility(View.VISIBLE);
                    layoutClass2.setVisibility(View.VISIBLE);
                    layoutOnlyView.setVisibility(View.GONE);
                    layoutOnlyView2.setVisibility(View.GONE);
                    layoutTime.setVisibility(View.GONE);
                    btnStart.setVisibility(View.VISIBLE);
                    btnKeyIn.setVisibility(View.VISIBLE);
                    btnSearch.setVisibility(View.VISIBLE);
                    //.setVisibility(View.VISIBLE);
                    getNonOperationClassCodes(costCenter);//비가동 대분류를 가져온다.
                    return;
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_LONG).show();
                        //showErrorDialog(context, ErrorCheck,2);
                        return;
                    }
                    if (!child.getString("EndTime").equals("")) {//EndTime에 데이터가 들어있다-> 가동상태
                        txtState.setText("가동");
                        txtState.setTextColor(Color.BLUE);
                        layoutClass.setVisibility(View.VISIBLE);
                        layoutClass2.setVisibility(View.VISIBLE);
                        layoutOnlyView.setVisibility(View.GONE);
                        layoutOnlyView2.setVisibility(View.GONE);
                        layoutTime.setVisibility(View.GONE);
                        btnStart.setVisibility(View.VISIBLE);
                        btnKeyIn.setVisibility(View.VISIBLE);
                        btnSearch.setVisibility(View.VISIBLE);
                        //layoutCostCenter.setVisibility(View.VISIBLE);
                        getNonOperationClassCodes(costCenter);//비가동 대분류를 가져온다.
                        return;
                    }
                    boolean running;
                    long pauseOffset;

                    txtState.setText("비가동");
                    //txtState.setText(Long.toString(SystemClock.elapsedRealtime()));
                    txtState.setTextColor(Color.RED);
                    layoutClass.setVisibility(View.GONE);
                    layoutClass2.setVisibility(View.GONE);
                    layoutOnlyView.setVisibility(View.VISIBLE);
                    layoutOnlyView2.setVisibility(View.VISIBLE);
                    layoutTime.setVisibility(View.VISIBLE);
                    txtCurrentClass.setText(child.getString("NonOperationClassCodeName"));
                    txtCurrentCode.setText(child.getString("NonOperationCodeName"));
                    btnStart.setVisibility(View.GONE);
                    btnKeyIn.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.GONE);
                    getNonOperationClassCodes(costCenter);//비가동 대분류를 가져온다.

                    getNonOperationCodes(child.getString("NonOperationClassCode"), costCenter);

                    chronometer.setFormat("%s");

                    chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
                        @Override
                        public void onChronometerTick(Chronometer cArg) {
                            long time = SystemClock.elapsedRealtime() - cArg.getBase();
                            int h   = (int)(time /3600000);
                            int m = (int)(time - h*3600000)/60000;
                            int s= (int)(time - h*3600000- m*60000)/1000 ;
                            String hh = h < 10 ? "0"+h: h+"";
                            String mm = m < 10 ? "0"+m: m+"";
                            String ss = s < 10 ? "0"+s: s+"";
                            cArg.setText(hh+":"+mm+":"+ss);
                        }
                    });
                    long diffTime=Long.parseLong(child.getString("DiffTime"));

                  /*  SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss.SS");
                    long reqTime = System.currentTimeMillis();
                    String reqTimeStr = dayTime.format(new Date(reqTime));//현재시간*/


                    //long resTime =
                   // String resTimeStr = dayTime.format(resTime);

                    chronometer.setBase(SystemClock.elapsedRealtime() - (diffTime));
                    chronometer.start();

                    //txtTest.setText(child.getString("StartTime"));
                    //layoutCostCenter.setVisibility(View.GONE);
                    return;
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }

    public void getNonOperationClassCodes(String costCenter) {
        String url = getString(R.string.service_address) + "getNonOperationClassCodes";
        ContentValues values = new ContentValues();
        GetNonOperationClassCodes gsod = new GetNonOperationClassCodes(url, values, costCenter);
        gsod.execute();
    }

    public class GetNonOperationClassCodes extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String costCenter;

        GetNonOperationClassCodes(String url, ContentValues values, String costCenter) {
            this.costCenter=costCenter;
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
                NonOperationClassCodes nonOperationClassCodes;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                ArrayAdapter<String> nonOperationClassCodesArrayAdapter;
                final ArrayList<String> nonOperationClassCodesArrayList= new ArrayList<>();
                nonOperationClassCodesArrayList2= new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_LONG).show();
                        //showErrorDialog(context, ErrorCheck,2);
                        return;
                    }
                    nonOperationClassCodes = new NonOperationClassCodes();
                    nonOperationClassCodes.NonOperationClassCode = child.getString("NonOperationClassCode");
                    nonOperationClassCodes.NonOperationClassCodeName = child.getString("NonOperationClassCodeName");
                    nonOperationClassCodesArrayList.add(nonOperationClassCodes.NonOperationClassCodeName);
                    nonOperationClassCodesArrayList2.add(nonOperationClassCodes);
                }
                nonOperationClassCodesArrayAdapter = new ArrayAdapter<>(context, R.layout.list_item, nonOperationClassCodesArrayList);
                nonOperationClassCodesSpinner.setAdapter(nonOperationClassCodesArrayAdapter);

                nonOperationClassCodesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //getCostCenterStopOperations(nonOperationClassCodesArrayList2.get(position).NonOperationClassCode);
                        getNonOperationCodes(nonOperationClassCodesArrayList2.get(position).NonOperationClassCode ,costCenter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                /*editTextFilledExposedDropdown.setAdapter(costCenterArrayAdapter);

                editTextFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        menu.setHint("");
                        editTextFilledExposedDropdown.setTextColor(Color.BLACK);


                    }
                });*/


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }

    public void getNonOperationCodes(String nonOperationClassCode ,String costCenter) {
        String url = getString(R.string.service_address) + "getNonOperationCodes";
        ContentValues values = new ContentValues();
        values.put("NonOperationClassCode", nonOperationClassCode);
        values.put("CostCenter", costCenter);
        GetNonOperationCodes gsod = new GetNonOperationCodes(url, values);
        gsod.execute();
    }


    public class GetNonOperationCodes extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetNonOperationCodes(String url, ContentValues values) {
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
                ArrayAdapter<String> nonOperationCodeArrayAdapter;
                NonOperationCodes nonOperationCodes;
                final ArrayList<String> nonOperationCodeArrayList= new ArrayList<>();
                nonOperationCodeArrayList2= new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_LONG).show();
                        //showErrorDialog(context, ErrorCheck,2);
                        return;
                    }
                    nonOperationCodes=new NonOperationCodes();
                    nonOperationCodes.NonOperationCode=child.getString("NonOperationCode");
                    nonOperationCodes.NonOperationCodeName=child.getString("NonOperationCodeName");

                    nonOperationCodeArrayList.add(nonOperationCodes.NonOperationCodeName);
                    nonOperationCodeArrayList2.add(nonOperationCodes);
                }
                nonOperationCodeArrayAdapter = new ArrayAdapter<>(context, R.layout.list_item, nonOperationCodeArrayList);
                nonOperationCodeSpinner.setAdapter(nonOperationCodeArrayAdapter);

                nonOperationCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //getCostCenterStopOperations(costCenterArrayList2.get(position).CostCenter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
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
        ApplicationClass.getInstance().progressON((Activity)getContext(), null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity)getContext(), message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }


    public void onDestroy(){

        super.onDestroy();
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();

    }
}