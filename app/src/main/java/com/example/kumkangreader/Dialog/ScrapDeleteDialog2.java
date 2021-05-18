package com.example.kumkangreader.Dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.kumkangreader.Activity.ActivityScrap;
import com.example.kumkangreader.Application.ApplicationClass;
import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.Object.OutputData;
import com.example.kumkangreader.Object.ScrapCode;
import com.example.kumkangreader.Object.ScrapData;
import com.example.kumkangreader.Object.ScrapGrade;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScrapDeleteDialog2 extends Dialog implements BaseActivityInterface {

    Context context;
    Button btnScrap;
    Button btnDeleteScrap;
    Button btnDelete;
    Button btnCancel;

    String itemTag;
    String costCenter;
    Handler mHandler;

    OutputData outputData;
    ArrayList<ScrapCode> scrapCodeArrayList;
    ArrayList<ScrapGrade> scrapGradeArrayList;

    //ScrapDeleteDialog2.OnDialogResult mDialogResult

    public ScrapDeleteDialog2(@NonNull Context context, String itemTag, String costCenter, Handler mHandler) {
        super(context);
        this.context=context;
        this.itemTag=itemTag;
        this.costCenter=costCenter;
        this.mHandler=mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_delete_dialog2);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = ((WindowManager) context.getApplicationContext().getSystemService(context.getApplicationContext().WINDOW_SERVICE));

        this.btnScrap = findViewById(R.id.btnScrap);
        this.btnDeleteScrap=findViewById(R.id.btnDeleteScrap);
        this.btnDelete = findViewById(R.id.btnDelete);
        this.btnCancel = findViewById(R.id.btnCancel);
        lp.width = (int) (wm.getDefaultDisplay().getWidth() * 0.8);

        this.btnScrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getScrapBasic();
                dismiss();
            }
        });

        this.btnDeleteScrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getScrap();
            }
        });

        this.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOutputData();
                dismiss();
            }
        });



        this.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getWindow().setAttributes(lp);
    }

    private void getScrap() {
        String url = context.getString(R.string.service_address) + "getScrap";
        ContentValues values = new ContentValues();
        values.put("ItemTag", itemTag);
        values.put("CostCenter", costCenter);
        GetScrap gsod = new GetScrap(url, values);
        gsod.execute();
    }

    public class GetScrap extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetScrap(String url, ContentValues values) {
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

                for (int i = 0; i < jsonArray.length(); i++) {
                    ScrapData scrapData;
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
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
                }

                if(scrapDataArrayList.size()==0){
                    Toast.makeText(context, "삭제할 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                Intent i = new Intent(context, ActivityScrap.class);
                i.putExtra("scrapDataArrayList", scrapDataArrayList);
                i.putExtra("itemTag",itemTag);
                i.putExtra("costCenter",costCenter);

                context.startActivity(i);
                dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }



    private void getScrapBasic() {
        String url = context.getString(R.string.service_address) + "getScrapBasic";
        ContentValues values = new ContentValues();
        values.put("ItemTag", itemTag);
        GetScrapBasic gsod = new GetScrapBasic(url, values);
        gsod.execute();
    }

    public class GetScrapBasic extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetScrapBasic(String url, ContentValues values) {
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
                //OutputData outputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    outputData= new OutputData(
                            child.getString("ItemTag"),
                            child.getString("CoilNo"),
                            child.getString("PartCode"),
                            child.getString("PartName"),
                            child.getString("PartSpec"),
                            child.getString("PartSpecName"),
                            child.getString("MSpec"),
                            "0"
                    );
                }

                getScrapCode();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }

    private void getScrapCode() {
        String url = context.getString(R.string.service_address) + "getScrapCode";
        ContentValues values = new ContentValues();
        GetScrapCode gsod = new GetScrapCode(url, values);
        gsod.execute();
    }

    public class GetScrapCode extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetScrapCode(String url, ContentValues values) {
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
                //OutputData outputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                scrapCodeArrayList =  new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ScrapCode scrapCode= new ScrapCode(
                            child.getString("Code"),
                            child.getString("ScrapCodeName"),
                            child.getString("SortNo")
                    );

                    scrapCodeArrayList.add(scrapCode);
                }
                getScrapGrade();


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
    }

    private void getScrapGrade() {
        String url = context.getString(R.string.service_address) + "getScrapGrade";
        ContentValues values = new ContentValues();
        GetScrapGrade gsod = new GetScrapGrade(url, values);
        gsod.execute();
    }


    public class GetScrapGrade extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        GetScrapGrade(String url, ContentValues values) {
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
                //OutputData outputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";
                scrapGradeArrayList =  new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ScrapGrade scrapGrade= new ScrapGrade(
                            child.getString("Grade"),
                            child.getString("ScrapGradeName"),
                            child.getString("SortNo")
                    );

                    scrapGradeArrayList.add(scrapGrade);
                }

                DialogInsertScrap sdd = new DialogInsertScrap(context, outputData, scrapCodeArrayList, scrapGradeArrayList, mHandler);
                //sdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                sdd.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
                sdd.show();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }

        }
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

    private void deleteOutputData() {
        String url = context.getString(R.string.service_address) + "deleteOutputData";
        ContentValues values = new ContentValues();
        values.put("ItemTag", itemTag);
        values.put("CostCenter", costCenter);
        values.put("UserCode", Users.PhoneNumber);
        DeleteOutputData gsod = new DeleteOutputData(url, values);
        gsod.execute();
    }

    public class DeleteOutputData extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        DeleteOutputData(String url, ContentValues values) {
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
                //OutputData outputData;
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject child = jsonArray.getJSONObject(i);
                    if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                        ErrorCheck = child.getString("ErrorCheck");
                        Toast.makeText(context, ErrorCheck, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                //데이터및 리스트뷰 다시불러오기
                //ActivityProductionPerformance.getInputData("","");
                Message msg= mHandler.obtainMessage();
                mHandler.sendMessage(msg);

                //getInputData("", values.get("ItemTag").toString());

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
}