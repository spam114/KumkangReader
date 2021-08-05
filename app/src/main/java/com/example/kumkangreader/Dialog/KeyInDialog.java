package com.example.kumkangreader.Dialog;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

public class KeyInDialog extends Dialog implements BaseActivityInterface {

    TextView txtDialogName;
    TextView txtDate;
    TextView txtState;
    TextView txtChange;
    Button btnOK;
    Button btnCancel;

    Context context;
    String dialogName;
    String state;
    String costCenter;
    String nonOperationCode;

    //String startTime;
    //String endTime;

    TimePicker startTimePicker;
    TimePicker endTimePicker;

    int tyear;
    int tmonth;
    int tdate;

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
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON(getOwnerActivity(), message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void showErrorDialog(Context context, String message, int type) {
        ApplicationClass.getInstance().showErrorDialog(context, message, type);
    }

    public KeyInDialog(Context context, String dialogName, String state, String costCenter, String nonOperationCode) {
        super(context);
        this.context = context;
        this.dialogName = dialogName;
        this.state = state;
        this.costCenter=costCenter;
        this.nonOperationCode=nonOperationCode;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_key_in);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE));
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 1);
        getWindow().setAttributes(lp);

        this.txtDialogName = findViewById(R.id.txtDialogName);
        this.btnCancel = findViewById(R.id.btnCancel);
        this.btnOK = findViewById(R.id.btnOK);
        this.txtDate = findViewById(R.id.txtDate);
        this.txtState = findViewById(R.id.txtState);
        this.txtState.setText(state);
        this.txtChange = findViewById(R.id.txtChange);

        this.startTimePicker=findViewById(R.id.startTimePicker);
        this.endTimePicker=findViewById(R.id.endTimePicker);

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

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyInDialog.this.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workDate=tyear+"-"+(tmonth+1)+"-"+tdate;
                String startTime=tyear+"-"+(tmonth+1)+"-"+tdate+" "+startTimePicker.getCurrentHour()+":"+startTimePicker.getCurrentMinute()+":00";
                String endTime=tyear+"-"+(tmonth+1)+"-"+tdate+" "+endTimePicker.getCurrentHour()+":"+endTimePicker.getCurrentMinute()+":00";
                setCostCenterStopOperationsByKeyIn(workDate, startTime, endTime);
            }
        });

        progressOFF();
    }

    public void setCostCenterStopOperationsByKeyIn(String workDate, String startTime, String endTime) {
        String url = context.getString(R.string.service_address) + "setCostCenterStopOperationsByKeyIn";
        ContentValues values = new ContentValues();
        values.put("CostCenter", costCenter);
        values.put("DayFlag", Users.WorkClassName);
        values.put("NonOperationCode", nonOperationCode);
        values.put("WorkDate", workDate);
        values.put("StartTime", startTime);
        values.put("EndTime", endTime);
        values.put("UserCode", Users.UserID);
        SetCostCenterStopOperationsByKeyIn gsod = new SetCostCenterStopOperationsByKeyIn(url, values);
        gsod.execute();
    }

    public class SetCostCenterStopOperationsByKeyIn extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        SetCostCenterStopOperationsByKeyIn(String url, ContentValues values) {
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
                        //Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck,2);
                        return;
                    }

                }
                //Toast.makeText(context, "등록 되었습니다.", Toast.LENGTH_SHORT).show();
                showErrorDialog(context, "등록 되었습니다.",1);
                KeyInDialog.this.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }
        }
    }

    private void showDateTimePicker(int year, int month, int date) {
        DatePickerDialog dpd = new DatePickerDialog
                (context, // 현재화면의 제어권자
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                tyear=year;
                                tmonth=monthOfYear;
                                tdate=dayOfMonth;
                            }
                        }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        year, month, date); // 기본값 연월일
        dpd.show();
    }
}

