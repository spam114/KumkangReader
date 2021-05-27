package com.example.kumkangreader.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.ItemTag;
import com.example.kumkangreader.Object.StockOutDetail;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StockOutDetailAdapter extends ArrayAdapter<StockOutDetail> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)
    String stockOutNo;
    LinearLayout layoutQty;


    public StockOutDetailAdapter(Context context, int layoutResourceID, ArrayList data, String stockOutNo) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        //this.adapterType = adapterType;
        this.stockOutNo = stockOutNo;
    }

    public StockOutDetailAdapter(Context context, int layoutResourceID, ArrayList data, String lastPart, String stockOutNo) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.lastPart = lastPart;
        //this.adapterType = adapterType;
        this.stockOutNo = stockOutNo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final StockOutDetail item = (StockOutDetail) data.get(position);
        if (item != null) {

            TextView textViewPartName = (TextView) row.findViewById(R.id.textViewPartName);
            textViewPartName.setText(((StockOutDetail) data.get(position)).PartName);

            TextView textViewPartSpecName = (TextView) row.findViewById(R.id.textViewPartSpecName);
            textViewPartSpecName.setText(((StockOutDetail) data.get(position)).PartSpecName);

            TextView textViewEA = (TextView) row.findViewById(R.id.textViewEA);
            textViewEA.setText(((StockOutDetail) data.get(position)).OutQty);

            TextView textViewEA2 = (TextView) row.findViewById(R.id.textViewEA2);
            textViewEA2.setText(((StockOutDetail) data.get(position)).ScanQty);

            LinearLayout layoutQty=row.findViewById(R.id.layoutQty);

            if ((item.PartCode + "-" + item.PartSpec).equals(lastPart)) {//마지막 변경된 행 강조표시
                textViewPartName.setBackgroundColor(Color.YELLOW);
                textViewPartSpecName.setBackgroundColor(Color.YELLOW);
                layoutQty.setBackgroundColor(Color.YELLOW);
                this.lastPosition = position;
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new GetOneItemData(item.PartCode, item.PartSpec, item.PartName, item.PartSpecName).execute(context.getString(R.string.service_address) + "getOneItemData");
                    String url=context.getString(R.string.service_address) + "getOneItemData";
                    ContentValues values = new ContentValues();
                    values.put("StockOutNo", stockOutNo);
                    values.put("PartCode", item.PartCode);
                    values.put("PartSpec", item.PartSpec);
                    GetOneItemData gid = new GetOneItemData(url, values, item.PartName, item.PartSpecName);
                    gid.execute();
                }
            });
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

    private void startProgress() {

        progressON("Loading...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF();
            }
        }, 3500);

    }

    public class GetOneItemData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;
        String partName;
        String partSpecName;
        GetOneItemData(String url, ContentValues values, String partName, String partSpecName){
            this.url = url;
            this.values = values;
            this.partName=partName;
            this.partSpecName=partSpecName;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progress bar를 보여주는 등의 행위
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
                ItemTag itemTag;
                JSONArray jsonArray = new JSONArray(result);
                List<String> itemList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);

                    //child.getString("PartCode")
                    itemTag = new ItemTag();
                    itemTag.ItemTag = child.getString("ItemTagNo");
                    itemTag.ItemCnt = child.getString("ItemCnt");
                    itemList.add("   " + itemTag.ItemTag + "        " + itemTag.ItemCnt + " EA");
                }
                //MaterialAlertDialogBuilder
                MaterialAlertDialogBuilder alertBuilder = new MaterialAlertDialogBuilder(context);
                //alertBuilder.setIcon(R.drawable.ic_launcher);
                alertBuilder.setTitle(partName + "(" + partSpecName + ")");

                // List Adapter 생성
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1);

                for (int i = 0; i < itemList.size(); i++) {
                    adapter.add(itemList.get(i));
                }

                // 버튼 생성
                alertBuilder.setNegativeButton("닫기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

                // Adapter 셋팅
                alertBuilder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        });
                alertBuilder.show();


            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                progressOFF();
            }

        }
    }
}

