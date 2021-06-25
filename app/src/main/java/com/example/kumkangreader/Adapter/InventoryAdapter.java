package com.example.kumkangreader.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.Inventory;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InventoryAdapter extends ArrayAdapter<Inventory> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    String lastPart;
    Handler mHandler;
    //String lastPart;//마지막에 추가된 품목,규격
    //String costCenter;
    //public int lastPosition;//마지막에 변화된 행값
    //Handler mHandler;
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)



    public InventoryAdapter(Context context, int layoutResourceID, ArrayList data, String lastPart, Handler mHandler) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.lastPart=lastPart;
        this.mHandler=mHandler;
        //this.adapterType = adapterType;
        //this.stockOutNo = stockOutNo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final Inventory item = (Inventory) data.get(position);
        if (item != null) {

            TextView txtItemTag = (TextView) row.findViewById(R.id.txtItemTag);
            txtItemTag.setText(((Inventory) data.get(position)).ItemTag.replace("EI-",""));

            TextView txtPartName = (TextView) row.findViewById(R.id.txtPartName);
            txtPartName.setText(((Inventory) data.get(position)).PartName);

            TextView txtPartSpec = (TextView) row.findViewById(R.id.txtPartSpec);
            txtPartSpec.setText(((Inventory) data.get(position)).PartSpecName);

            TextView txtQty = (TextView) row.findViewById(R.id.txtQty);
            String qty=String.format("%.0f",Double.parseDouble(((Inventory) data.get(position)).Qty));
            txtQty.setText(qty);

            TextView txtSeqNo = (TextView) row.findViewById(R.id.txtSeqNo);
            txtSeqNo.setText(((Inventory) data.get(position)).SeqNo);

            LinearLayout detailLayout = row.findViewById(R.id.detailLayout);

            if ((item.ItemTag).equals(lastPart)) {//마지막 변경된 행 강조표시
                detailLayout.setBackgroundColor(Color.YELLOW);
            }
            else{
                detailLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                MaterialAlertDialogBuilder alertBuilder= new MaterialAlertDialogBuilder(context);
                alertBuilder.setTitle("TAG번호 \"" +((Inventory) data.get(position)).ItemTag+  "\"를 삭제 하시겠습니까?");
                final String[] selectedCoil = {""};;
                alertBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //삭제
                        deleteInventorySurvey(((Inventory) data.get(position)).ItemTag);
                        dialog.dismiss();
                    }
                });
                alertBuilder.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder.show();

                return true;
            }
        });
        return row;
    }

    public void deleteInventorySurvey(String itemTag) {
        String url = context.getString(R.string.service_address) + "deleteInventorySurvey";
        ContentValues values = new ContentValues();
        values.put("ItemTag", itemTag);
        DeleteInventorySurvey gsod = new DeleteInventorySurvey(url, values);
        gsod.execute();
    }

    public class DeleteInventorySurvey extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        DeleteInventorySurvey(String url, ContentValues values) {
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
                Inventory inventory;
                ArrayList<Inventory> inventoryArrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //삭제성공
                Message msg= mHandler.obtainMessage();
                mHandler.sendMessage(msg);
                Toast.makeText(context, "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
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
