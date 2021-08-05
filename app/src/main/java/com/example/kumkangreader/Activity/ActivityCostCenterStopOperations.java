package com.example.kumkangreader.Activity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kumkangreader.Adapter.CostCenterStopOperationsAdapter;
import com.example.kumkangreader.Object.CostCenterStopOperations;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityCostCenterStopOperations extends BaseActivity{
    TextView txtContent;
    TextView txtDate;
    int tyear;
    int tmonth;
    int tdate;
    TextView txtChange;
    ListView listviewStopOperation;
    ArrayList<CostCenterStopOperations> costCenterStopOperationsArrayList;
    CostCenterStopOperationsAdapter costCenterStopOperationsAdapter;//지시 어뎁터

    String costCenter;
    String costCenterName;

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
        setContentView(R.layout.activity_costcenter_stop_operation);
        this.txtContent=findViewById(R.id.txtContent);
        this.txtDate=findViewById(R.id.txtDate);
        this.txtChange=findViewById(R.id.txtChange);
        this.listviewStopOperation=findViewById(R.id.listviewStopOperation);

        this.costCenter = getIntent().getStringExtra("costCenter");
        this.costCenterName = getIntent().getStringExtra("costCenterName");
        this.txtContent.setText("비가동 내역 ("+this.costCenterName+")");
        //현재날짜 구하기
        final Calendar calendar = Calendar.getInstance();
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
        });

        String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
        GetCostCenterStopOperationsByCostCenter(fromDate);
    }

    private void showDateTimePicker(int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog
                (ActivityCostCenterStopOperations.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                tyear=year;
                                tmonth=monthOfYear;
                                tdate=dayOfMonth;
                                String fromDate=tyear+"-"+(tmonth+1)+"-"+tdate;
                                GetCostCenterStopOperationsByCostCenter(fromDate);
                            }
                        }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        year, month, date); // 기본값 연월일
        dpd.show();
    }

    public void GetCostCenterStopOperationsByCostCenter(String fromDate) {
        String url = getString(R.string.service_address) + "getCostCenterStopOperationsByCostCenter";
        ContentValues values = new ContentValues();
        values.put("CostCenter", costCenter);
        values.put("FromDate", fromDate);
        GetCostCenterStopOperationsByCostCenter gsod = new GetCostCenterStopOperationsByCostCenter(url, values);
        gsod.execute();
    }

    public class GetCostCenterStopOperationsByCostCenter extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetCostCenterStopOperationsByCostCenter(String url, ContentValues values) {
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
                CostCenterStopOperations costCenterStopOperations;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                costCenterStopOperationsArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(ActivityCostCenterStopOperations.this, ErrorCheck,2);
                        return;
                    }
                    costCenterStopOperations = new CostCenterStopOperations();
                    costCenterStopOperations.DayFlag=child.getString("DayFlag");
                    costCenterStopOperations.NonOperationClassCodeName=child.getString("NonOperationClassCodeName");
                    costCenterStopOperations.NonOperationCodeName=child.getString("NonOperationCodeName");
                    costCenterStopOperations.StartTime=child.getString("StartTime");
                    costCenterStopOperations.EndTime=child.getString("EndTime");
                    costCenterStopOperations.StartUser=child.getString("StartUser");
                    costCenterStopOperations.EndUser=child.getString("EndUser");

                    costCenterStopOperationsArrayList.add(costCenterStopOperations);
                }
                costCenterStopOperationsAdapter= new CostCenterStopOperationsAdapter
                        (ActivityCostCenterStopOperations.this, R.layout.listview_stop_operation_row, costCenterStopOperationsArrayList);
                listviewStopOperation.setAdapter(costCenterStopOperationsAdapter);
                progressOFF();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {

            }

        }
    }


}
