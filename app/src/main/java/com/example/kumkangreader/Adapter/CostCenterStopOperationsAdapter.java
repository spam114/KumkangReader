package com.example.kumkangreader.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.CostCenterStopOperations;
import com.example.kumkangreader.R;

import java.util.ArrayList;

public class CostCenterStopOperationsAdapter extends ArrayAdapter<CostCenterStopOperations> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)


    public CostCenterStopOperationsAdapter(Context context, int layoutResourceID, ArrayList data) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final CostCenterStopOperations item = (CostCenterStopOperations) data.get(position);
        if (item != null) {

            TextView txtDayFlag = (TextView) row.findViewById(R.id.txtDayFlag);
            txtDayFlag.setText(((CostCenterStopOperations) data.get(position)).DayFlag);

            TextView txtNonOperationClassCodeName = (TextView) row.findViewById(R.id.txtNonOperationClassCodeName);
            txtNonOperationClassCodeName.setText(((CostCenterStopOperations) data.get(position)).NonOperationClassCodeName);

            TextView txtNonOperationCodeName = (TextView) row.findViewById(R.id.txtNonOperationCodeName);
            txtNonOperationCodeName.setText(((CostCenterStopOperations) data.get(position)).NonOperationCodeName);

            TextView txtStartTime = (TextView) row.findViewById(R.id.txtStartTime);
            txtStartTime.setText(((CostCenterStopOperations) data.get(position)).StartTime);

            TextView txtEndTime = (TextView) row.findViewById(R.id.txtEndTime);
            txtEndTime.setText(((CostCenterStopOperations) data.get(position)).EndTime);

            TextView txtStartUser = (TextView) row.findViewById(R.id.txtStartUser);
            txtStartUser.setText(((CostCenterStopOperations) data.get(position)).StartUser);

            TextView txtEndUser = (TextView) row.findViewById(R.id.txtEndUser);
            txtEndUser.setText(((CostCenterStopOperations) data.get(position)).EndUser);


           /* if ((item.PartCode + "-" + item.PartSpec).equals(lastPart)) {//마지막 변경된 행 강조표시
                textViewPartName.setBackgroundColor(Color.YELLOW);
                textViewPartSpecName.setBackgroundColor(Color.YELLOW);
                layoutQty.setBackgroundColor(Color.YELLOW);
                this.lastPosition = position;
            }
            else{
                textViewPartName.setBackgroundColor(Color.TRANSPARENT);
                textViewPartSpecName.setBackgroundColor(Color.TRANSPARENT);
                layoutQty.setBackgroundColor(Color.TRANSPARENT);
            }*/

           /* row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });*/
        }
        return row;
    }

    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {
        ApplicationClass.getInstance().progressON((Activity)context, null);
    }

    @Override
    public void progressON(String message) {
        ApplicationClass.getInstance().progressON((Activity)context, message);
    }

    @Override
    public void progressOFF() {
        ApplicationClass.getInstance().progressOFF();
    }

    @Override
    public void progressON(String message, Handler handler) {
        ApplicationClass.getInstance().progressON((Activity)context, message, handler);
    }

    @Override
    public void progressOFF2() {
        ApplicationClass.getInstance().progressOFF2();
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

}

