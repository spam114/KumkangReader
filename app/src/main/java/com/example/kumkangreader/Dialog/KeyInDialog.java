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

        //???????????? ?????????
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
                        //Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck,2);
                        return;
                    }

                }
                //Toast.makeText(context, "?????? ???????????????.", Toast.LENGTH_SHORT).show();
                showErrorDialog(context, "?????? ???????????????.",1);
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
                (context, // ??????????????? ????????????
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view,
                                                  int year, int monthOfYear, int dayOfMonth) {
                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                tyear=year;
                                tmonth=monthOfYear;
                                tdate=dayOfMonth;
                            }
                        }
                        , // ???????????? ???????????? ??? ??????????????? ???????????????
                        //    ????????? ????????? ??????
                        year, month, date); // ????????? ?????????
        dpd.show();
    }
}

