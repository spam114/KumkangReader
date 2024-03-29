package com.example.kumkangreader.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Dialog.ScrapDeleteDialog;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.InputData;
import com.example.kumkangreader.R;

import java.util.ArrayList;

public class InputAdapter extends ArrayAdapter<InputData> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
    String lastPart;//마지막에 추가된 품목,규격
    String worksOrderNo;
    String costCenter;
    public int lastPosition;//마지막에 변화된 행값
    Handler mHandler;
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)



    public InputAdapter(Context context, int layoutResourceID, ArrayList data, String worksOrderNo,String costCenter, Handler mHandler) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.worksOrderNo=worksOrderNo;
        this.costCenter=costCenter;
        this.mHandler=mHandler;
        //this.adapterType = adapterType;
        //this.stockOutNo = stockOutNo;
    }

    public InputAdapter(Context context, int layoutResourceID, ArrayList data, String lastPart, String worksOrderNo,String costCenter, Handler mHandler) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.lastPart = lastPart;
        this.worksOrderNo=worksOrderNo;
        this.costCenter=costCenter;
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

        final InputData item = (InputData) data.get(position);
        if (item != null) {

            TextView txtItemTag = (TextView) row.findViewById(R.id.txtItemTag);
            txtItemTag.setText(((InputData) data.get(position)).ItemTag.replace("EI-",""));

            TextView txtCoil = (TextView) row.findViewById(R.id.txtCoil);
            txtCoil.setText(((InputData) data.get(position)).CoilNo);

            TextView txtPartName = (TextView) row.findViewById(R.id.txtPartName);
            txtPartName.setText(((InputData) data.get(position)).PartName);

            TextView txtPartSpec = (TextView) row.findViewById(R.id.txtPartSpec);

            if(!((InputData) data.get(position)).MSpec.equals("")){
                txtPartSpec.setText(((InputData) data.get(position)).PartSpec+"("+((InputData) data.get(position)).MSpec+")");
            }
            else{
                txtPartSpec.setText(((InputData) data.get(position)).PartSpec);
            }

            TextView txtQty = (TextView) row.findViewById(R.id.txtQty);
            String qty=String.format("%.0f",Double.parseDouble(((InputData) data.get(position)).Qty));
            txtQty.setText(qty);

            TextView txtSeqNo = (TextView) row.findViewById(R.id.txtSeqNo);
            txtSeqNo.setText(((InputData) data.get(position)).SeqNo);

            //TextView txtUseFlag = (TextView) row.findViewById(R.id.txtUseFlag);
            String useFlag="";
            if(((InputData) data.get(position)).UseFlag.equals("0")){
                //useFlag = "N";
                txtQty.setTextColor(Color.RED);
            } else {
                txtQty.setTextColor(Color.parseColor("#757575"));
               // useFlag = "Y";
            }

            LinearLayout detailLayout = row.findViewById(R.id.detailLayout);

            if ((item.ItemTag).equals(lastPart)) {//마지막 변경된 행 강조표시
                detailLayout.setBackgroundColor(Color.YELLOW);
                this.lastPosition = position;
                //detailLayout.findFocus();
            }
            else{
                detailLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ScrapDeleteDialog sdd = new ScrapDeleteDialog(context, ((InputData) data.get(position)).ItemTag, worksOrderNo,costCenter, mHandler);
                //sdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                sdd.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
                sdd.show();


                return true;
            }
        });
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

    /*public class GetOneItemData extends AsyncTask<Void, Void, String> {
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

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
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
    }*/
}
