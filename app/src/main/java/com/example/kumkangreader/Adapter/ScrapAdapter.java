package com.example.kumkangreader.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.ScrapData;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScrapAdapter extends ArrayAdapter<ScrapAdapter> implements BaseActivityInterface {

    Context context;
    int layoutRsourceId;
    ArrayList data;
   /* String lastPart;//마지막에 추가된 품목,규격
    public int lastPosition;//마지막에 변화된 행값*/
    String worksOrderNo;
    String costCenter;
    Handler mHandler;
    String itemTag;
    //int adapterType;//0번instruction(지시어뎁터), 1번스캔(input어뎁터)

    TextView txtPartName;
    TextView txtPartSpecName;
    TextView txtScrapGrade;
    TextView txtScrapCode;
    TextView txtQty;
    ImageView imageView;


    public ScrapAdapter(Context context, int layoutResourceID, ArrayList data, String itemTag,  String worksOrderNo, String costCenter, Handler mHandler) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.worksOrderNo=worksOrderNo;
        this.costCenter=costCenter;
        this.itemTag=itemTag;
        this.mHandler= mHandler;
        //this.adapterType = adapterType;
        //this.stockOutNo = stockOutNo;
    }

   /* public ScrapAdapter(Context context, int layoutResourceID, ArrayList data, String lastPart, String costCenter, Handler mHandler) {
        super(context, layoutResourceID, data);
        this.context = context;
        this.layoutRsourceId = layoutResourceID;
        this.data = data;
        this.lastPart = lastPart;
        this.costCenter=costCenter;
        this.mHandler= mHandler;
        //this.adapterType = adapterType;
        //this.stockOutNo = stockOutNo;
    }*/

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutRsourceId, null);
        }

        final ScrapData item = (ScrapData) data.get(position);
        if (item != null) {

            txtPartName = (TextView) row.findViewById(R.id.txtPartName);
            txtPartName.setText(((ScrapData) data.get(position)).PartName);

            txtPartSpecName = (TextView) row.findViewById(R.id.txtPartSpecName);
            if(!((ScrapData) data.get(position)).MSpec.equals("")){
                txtPartSpecName.setText(((ScrapData) data.get(position)).PartSpec+"("+((ScrapData) data.get(position)).MSpec+")");
            }
            else{
                txtPartSpecName.setText(((ScrapData) data.get(position)).PartSpec);
            }

            txtScrapGrade = (TextView) row.findViewById(R.id.txtScrapGrade);
            txtScrapGrade.setText(((ScrapData) data.get(position)).ScrapGrade);

            txtScrapCode = (TextView) row.findViewById(R.id.txtScrapCode);
            txtScrapCode.setText(((ScrapData) data.get(position)).ScrapCode);

            txtQty = (TextView) row.findViewById(R.id.txtQty);
            String qty=String.format("%.0f",Double.parseDouble(((ScrapData) data.get(position)).ScrappedQty));
            txtQty.setText(qty);

            imageView = (ImageView) row.findViewById(R.id.imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //딜리트작업

                    ShowDeleteDialog(item);
                }

                private void ShowDeleteDialog(final ScrapData item) {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("불량품 삭제")
                            .setMessage("불량품 정보를 삭제 하시겠습니까?")
                            .setCancelable(true)
                            .setPositiveButton
                                    ("확인", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteScrap(item);
                                        }
                                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(getContext(), "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                            showErrorDialog(context, "취소 되었습니다.",1);
                        }
                    }).show();
                }




            });

            //LinearLayout detailLayout = row.findViewById(R.id.detailLayout);

           /* if ((item.ItemTag).equals(lastPart)) {//마지막 변경된 행 강조표시
                detailLayout.setBackgroundColor(Color.YELLOW);

                this.lastPosition = position;
            }*/

            //LinearLayout layoutQty=row.findViewById(R.id.layoutQty);

            /*if ((item.PartCode + "-" + item.PartSpec).equals(lastPart)) {//마지막 변경된 행 강조표시
                textViewPartName.setBackgroundColor(Color.YELLOW);
                textViewPartSpecName.setBackgroundColor(Color.YELLOW);
                layoutQty.setBackgroundColor(Color.YELLOW);

                this.lastPosition = position;
            }*/

            /*row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //new GetOneItemData(item.PartCode, item.PartSpec, item.PartName, item.PartSpecName).execute(context.getString(R.string.service_address) + "getOneItemData");
                    String url=context.getString(R.string.service_address) + "getOneItemData";
                    ContentValues values = new ContentValues();
                    values.put("PartCode", item.PartCode);
                    values.put("PartSpec", item.PartSpec);
                    InputAdapter.GetOneItemData gid = new InputAdapter.GetOneItemData(url, values, item.PartName, item.PartSpecName);
                    gid.execute();
                }
            });*/
        }

        /*row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ScrapDeleteDialog2 sdd = new ScrapDeleteDialog2(context, ((ScrapData) data.get(position)).ItemTag, costCenter, mHandler);
                //sdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                sdd.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
                sdd.show();

                return true;
            }
        });*/

        return row;
    }


    public void deleteScrap(ScrapData item) {
        String url = context.getString(R.string.service_address) + "deleteScrap";
        ContentValues values = new ContentValues();
        values.put("WorderLot", item.WorderLot);
        values.put("PartCode", item.PartCode);
        values.put("PartSpec", item.PartSpec);
        values.put("ScrappedQty", item.ScrappedQty);
        values.put("ScrappedWeight", item.ScrappedWeight);
        values.put("ScrapCode", item.ScrapCode);
        values.put("ScrapGrade", item.ScrapGrade);
        values.put("MSpec", item.MSpec);

        values.put("ItemTag", this.itemTag);
        values.put("WorksOrderNo", worksOrderNo);
        values.put("CostCenter", this.costCenter);
        values.put("UserCode", Users.UserID);
        DeleteScrap gsod = new DeleteScrap(url, values);
        gsod.execute();

    }


    public class DeleteScrap extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        DeleteScrap(String url, ContentValues values) {
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

                ArrayList<ScrapData> scrapDataArrayList = new ArrayList<>();

                if(jsonArray.length()==0){//데이터가 없
                    Bundle data = new Bundle();
                    Message msg= mHandler.obtainMessage();
                    data.putSerializable("scrapDataArrayList", scrapDataArrayList);
                    msg.setData(data);
                    mHandler.sendMessage(msg);
                }

                for (int i = 0; i < jsonArray.length(); i++) {
                    ScrapData scrapData;
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        //Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        showErrorDialog(context, ErrorCheck,2);
                        return;
                    }

                    scrapData = new ScrapData(
                            child.getString("WorderLot"),
                            child.getString("PartCode"),
                            child.getString("PartSpec"),
                            child.getString("MSpec"),
                            child.getString("ScrapCode"),
                            child.getString("ScrapGrade"),
                            child.getString("ScrappedQty"),
                            child.getString("ScrappedWeight"),
                            child.getString("MoveNo"),
                            child.getString("MoveSeqNo"),
                            child.getString("MLOT"),
                            child.getString("QRCode"),
                            child.getString("OutsourcingCost"),
                            child.getString("ScrapCodeName"),
                            child.getString("PartName"),
                            child.getString("MeasureUnit"),
                            child.getString("UnitWeight")
                    );

                    scrapDataArrayList.add(scrapData);
                    //이데이터를 ActivityScrap에 보내기만 하면 Good


                    Bundle data = new Bundle();
                    Message msg= mHandler.obtainMessage();
                    data.putSerializable("scrapDataArrayList", scrapDataArrayList);
                    msg.setData(data);
                    mHandler.sendMessage(msg);
                }

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
