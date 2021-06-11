package com.example.kumkangreader.Activity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.kumkangreader.Application.Application;
import com.example.kumkangreader.BuildConfig;
import com.example.kumkangreader.Constants;
import com.example.kumkangreader.MainActivity2;
import com.example.kumkangreader.Object.Users;
import com.example.kumkangreader.R;
import com.example.kumkangreader.RequestHttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class SplashScreenActivity extends BaseActivity {

    //pda 스캐너 변수
    private String mCurrentStatus;
    private String mSavedStatus;
    private static final String STATUS_CLOSE = "STATUS_CLOSE";
    private static final String STATUS_OPEN = "STATUS_OPEN";
    private static final String STATUS_TRIGGER_ON = "STATUS_TRIGGER_ON";
    private static final int SEQ_BARCODE_OPEN = 100;
    private static final int SEQ_BARCODE_CLOSE = 200;
    private static final int SEQ_BARCODE_GET_STATUS = 300;
    private static final int SEQ_BARCODE_SET_TRIGGER_ON = 400;
    private static final int SEQ_BARCODE_SET_TRIGGER_OFF = 500;
    private static final int SEQ_BARCODE_SET_PARAMETER = 600;
    private static final int SEQ_BARCODE_GET_PARAMETER = 700;
    private boolean mIsOpened = false;
    private int mBarcodeHandle = -1;
    //pda스캐너변수 끝

    /*
    버전다운로드 관련 변수
     */
    DownloadManager mDm;
    long mId = 0;
    //Handler mHandler;
    String serverVersion;
    String downloadUrl;
    ProgressDialog mProgressDialog;
    //버전 변수 끝

    SharedPreferences _pref;
    Boolean isShortcut = false;//아이콘의 생성
    private boolean mIsRegisterReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        _pref = getSharedPreferences("kumkang", MODE_PRIVATE);//sharedPreferences 이름: "kumkang"에 저장
        isShortcut = _pref.getBoolean("isShortcut", false);//"isShortcut"에 들어있는값을 가져온다.
        if (!isShortcut)//App을 처음 깔고 시작했을때 이전에 깐적이 있는지없는지 검사하고, 이름과 아이콘을 설정한다.
        {
            addShortcut(this);
        }

        checkServerVersion();//버전을 체크-> 안쪽에 권한
        Scannerinitialize();

        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (configuration.smallestScreenWidthDp < Application.minScreenWidth) {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            if (configuration.screenWidthDp < Application.minScreenWidth) {
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }



        Users.ScreenInches=(float)( getScreenInches());

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        /* Duration of wait */
        int SPLASH_DISPLAY_LENGTH = 2000;
        /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                *//* Create an Intent that will start the Menu-Activity. *//*
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);*/
    }

    int getScreenInches() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        return (int)Math.ceil(screenInches);
    }

    private void Scannerinitialize() {
        mSavedStatus = mCurrentStatus = STATUS_CLOSE;
        mIsRegisterReceiver = false;

        Intent intent = new Intent();
        String action = Constants.ACTION_BARCODE_CLOSE;
        int seq = SEQ_BARCODE_CLOSE;
        if (mCurrentStatus.equals(STATUS_CLOSE)) action = Constants.ACTION_BARCODE_OPEN;
        intent.setAction(action);
        if (mIsOpened) intent.putExtra(Constants.EXTRA_HANDLE, mBarcodeHandle);
        if (mCurrentStatus.equals(STATUS_CLOSE)) seq = SEQ_BARCODE_OPEN;
        intent.putExtra(Constants.EXTRA_INT_DATA3, seq);
        sendBroadcast(intent);
        if (mCurrentStatus.equals(STATUS_CLOSE)) {
            mIsOpened = true;
        } else {
            mIsOpened = false;
        }

    }


    private void addShortcut(Context context) {

        Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClassName(context, getClass().getName());
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        //FLAG_ACTIVITY_NEW_TASK: 실행한 액티비티와 관련된 태스크가 존재하면 동일한 태스크내에서 실행하고, 그렇지 않으면 새로운 태스크에서 액티비티를 실행하는 플래그
        //FLAG_ACTIVITY_RESET_TASK_IF_NEEDED: 사용자가 홈스크린이나 "최근 실행 액티비티목록"에서 태스크를 시작할 경우 시스템이 설정하는 플래그, 이플래그는 새로 태스크를
        //시작하거나 백그라운드 태스크를 포그라운드로 가지고 오는 경우가 아니라면 영향을 주지 않는다, "최근 실행 액티비티 목록":  홈 키를 오랫동안 눌렀을 떄 보여지는 액티비티 목록

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);//putExtra(이름, 실제값)
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "KUMKANGREADER");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(this, R.drawable.logo2));
        //Intent.ShortcutIconResource.fromContext(context, R.drawable.img_kumkang);
        intent.putExtra("duplicate", false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        sendBroadcast(intent);
        SharedPreferences.Editor editor = _pref.edit();
        editor.putBoolean("isShortcut", true);

        editor.commit();
    }

    private void checkServerVersion() {
        String url = getString(R.string.service_address) + "checkAppVersion";
        ContentValues values = new ContentValues();
        values.put("AppCode", getString(R.string.app_code));
        CheckAppVersion cav = new CheckAppVersion(url, values);
        cav.execute();
    }

    public class CheckAppVersion extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        CheckAppVersion(String url, ContentValues values) {
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
                if (result.equals("")) {
                    Toast.makeText(SplashScreenActivity.this, "서버연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    ActivityCompat.finishAffinity(SplashScreenActivity.this);
                }
                JSONArray jsonArray = new JSONArray(result);

                JSONObject child = jsonArray.getJSONObject(0);
                downloadUrl = child.getString("Message");
                serverVersion = child.getString("ResultCode");

                if (result.equals(""))
                    finish();
                else {
                    if (Double.parseDouble(serverVersion) > getCurrentVersion()) {//좌측이 DB에 있는 버전
                        newVersionDownload();
                    } else {
                        // finish();
                    }
                }
                CheckPermission();
            } catch (Exception er) {

            } finally {
                progressOFF();
            }
        }
    }

    private void newVersionDownload() {
        new android.app.AlertDialog.Builder(SplashScreenActivity.this).setMessage("새로운 버전이 있습니다. 다운로드 할까요?")
                .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                mProgressDialog = ProgressDialog.show(SplashScreenActivity.this, "다운로드", "잠시만 기다려주세요");

                Uri uri = Uri.parse(downloadUrl);
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setTitle("금강공업 출고관리 어플리케이션 다운로드");
                req.setDestinationInExternalFilesDir(SplashScreenActivity.this, Environment.DIRECTORY_DOWNLOADS, "KUMKANG.apk");

                //req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, pathSegments.get(pathSegments.size() - 1));
                //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();


                req.setDescription("금강공업 출고관리 어플리케이션 설치파일을 다운로드 합니다.");
                req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                mId = mDm.enqueue(req);
                IntentFilter filter = new IntentFilter();
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

                registerReceiver(mDownComplete2, filter);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SplashScreenActivity.this, "최신버전으로 업데이트 하시기 바랍니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.finishAffinity(SplashScreenActivity.this);
            }
        }).show();
    }

    /**
     * 다운로드 완료 이후의 작업을 처리한다.(다운로드 파일 열기)
     */
    BroadcastReceiver mDownComplete2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Toast.makeText(context, "다운로드 완료", Toast.LENGTH_SHORT).show();

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(mId);
            Cursor cursor = mDm.query(query);
            if (cursor.moveToFirst()) {

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(columnIndex);

                //String fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                //int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    String fileUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    openFile(fileUri);
                }
            }
        }
    };

    protected void openFile(String uri) {

        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri)).toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent open = new Intent(Intent.ACTION_VIEW);
        open.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        /*if(open.getFlags()!=Intent.FLAG_GRANT_READ_URI_PERMISSION){//권한 허락을 안한다면
            Toast.makeText(getBaseContext(), "Look!", Toast.LENGTH_LONG).show();
            finish();
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//누가 버전 이상이라면 FileProvider를 사용한다.
            Toast.makeText(getBaseContext(), "test1", Toast.LENGTH_LONG).show();
            uri = uri.substring(7);
            File file = new File(uri);
            Uri u = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
            open.setDataAndType(u, mimetype);
        } else {
            open.setDataAndType(Uri.parse(uri), mimetype);
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }


        Toast.makeText(getBaseContext(), "설치 완료 후, 어플리케이션을 다시 시작하여 주십시요.", Toast.LENGTH_LONG).show();
        startActivity(open);
        // finish();//startActivity 전일까 후일까 잘판단

    }

    public int getCurrentVersion() {

        int version;

        try {
            mDm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            PackageInfo i = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            version = i.versionCode;
            //Users.CurrentVersion = version;

            return version;

        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private void CheckPermission() {
        TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                //권한이없으면 여기
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {// 이전에 권한 요청 거절을 했는지 안했는지 검사: 이전에도 했으면 true
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                }
            } else {//권한이 있으면, 번호받기
                try {
                    Users.AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                    if (Users.AndroidID == null)
                        Users.AndroidID = "";
                    Users.Model = Build.MODEL;
                    if (Users.Model == null)
                        Users.Model = "";
                    Users.PhoneNumber = systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                    //Users.PhoneNumber = "010-6737-5288";//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                    if (Users.PhoneNumber == null)
                        Users.PhoneNumber = "";
                    else
                        Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");
                    Users.DeviceOS = Build.VERSION.RELEASE;
                    if (Users.DeviceOS == null)
                        Users.DeviceOS = "";
                    Users.Remark = "";
                    Users.DeviceName = BluetoothAdapter.getDefaultAdapter().getName();//블루투스 권한 필요 manifest확인: 블루투스가 없으면 에러남
                } catch (Exception e) {
                    /*String str=e.getMessage();
                    String str2=str;*/
                } finally {
                    InsertAppLoginHistory();
                }
            }
        } else {//낮은 버전이면 바로 번호 받기가능
            try {
                Users.AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (Users.AndroidID == null)
                    Users.AndroidID = "";
                Users.Model = Build.MODEL;
                if (Users.Model == null)
                    Users.Model = "";
                Users.PhoneNumber = systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                //Users.PhoneNumber = "010-6737-5288";//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                if (Users.PhoneNumber == null)
                    Users.PhoneNumber = "";
                else
                    Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");
                Users.DeviceOS = Build.VERSION.RELEASE;
                if (Users.DeviceOS == null)
                    Users.DeviceOS = "";
                Users.Remark = "";
                Users.DeviceName = BluetoothAdapter.getDefaultAdapter().getName();//블루투스 권한 필요 manifest확인: 블루투스가 없으면 에러남
            } catch (Exception e) {
                String str = e.getMessage();

                String str2 = str;
            } finally {
                InsertAppLoginHistory();
            }
        }
    }


    private void InsertAppLoginHistory() { //InsertAppLoginHistory-> CheckAppUsersByPhoneNumber
        String url = getString(R.string.service_address) + "insertAppLoginHistory";
        ContentValues values = new ContentValues();
        values.put("AppCode", getString(R.string.app_code));
        values.put("AndroidID", Users.AndroidID);
        values.put("Model", Users.Model);
        values.put("PhoneNumber", Users.PhoneNumber);
        values.put("DeviceName", Users.DeviceName);
        values.put("DeviceOS", Users.DeviceOS);
        values.put("AppVersion", getCurrentVersion());
        values.put("Remark", "");
        InsertAppLoginHistory ilh = new InsertAppLoginHistory(url, values);
        ilh.execute();
    }

    private void CheckAppProgramsPower() {
        String url = getString(R.string.service_address) + "CheckAppProgramsPower";
        ContentValues values = new ContentValues();
        values.put("UserID", Users.PhoneNumber);
        values.put("AppCode", getString(R.string.app_code));
        CheckAppProgramsPower capp = new CheckAppProgramsPower(url, values);
        capp.execute();
    }

    public class CheckAppProgramsPower extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        CheckAppProgramsPower(String url, ContentValues values) {
            this.url = url;
            this.values = values;
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
        protected void onPostExecute(String result) {//권한이 없을시, 로그인 화면 출력
            // 통신이 완료되면 호출됩니다.
            // 결과에 따른 UI 수정 등은 여기서 합니다
            try {
                Users.authorityList = new ArrayList<>();
                Users.authorityNameList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(result);
                String ErrorCheck = "";

                if (jsonArray.length() == 0) {//권한이 없을시, 로그인 화면 출력
                   /* Intent loginIntent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    return;*/
                    Toast.makeText(getBaseContext(), "사용자 혹은 권한이 등록되어있지 않습니다.", Toast.LENGTH_SHORT).show();
                    Intent Intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(Intent);
                    return;
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject child = jsonArray.getJSONObject(i);
                        if (!child.getString("ErrorCheck").equals("null")) {//문제가 있을 시, 에러 메시지 호출 후 종료
                            ErrorCheck = child.getString("ErrorCheck");
                            Toast.makeText(getBaseContext(), ErrorCheck, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Users.UserName=child.getString("UserName");
                        Users.UserID=child.getString("UserID");
                        Users.authorityList.add(Integer.parseInt(child.getString("Authority")));
                        Users.authorityNameList.add(child.getString("AuthorityName"));
                    }
                    Intent Intent = new Intent(SplashScreenActivity.this, MainActivity2.class);
                    startActivity(Intent);
                    return;

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                progressOFF();
            }
        }
    }

    public class InsertAppLoginHistory extends AsyncTask<Void, Void, String> {
        String url;
        ContentValues values;

        InsertAppLoginHistory(String url, ContentValues values) {
            this.url = url;
            this.values = values;
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
                CheckAppProgramsPower();
            } catch (Exception e) {

            } finally {
                progressOFF();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        // 하나라도 거부한다면.
                        new AlertDialog.Builder(this).setTitle("알림").setMessage("권한을 허용해주셔야 앱을 이용할 수 있습니다.")
                                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setCancelable(false).show();

                        return;
                    } else {//권한 다받았다면, 여기, 최초 권한 받았을때만 들어옴
                        TelephonyManager systemService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        try {
                            Users.AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                            if (Users.AndroidID == null)
                                Users.AndroidID = "";
                            Users.Model = Build.MODEL;
                            if (Users.Model == null)
                                Users.Model = "";
                            Users.PhoneNumber = systemService.getLine1Number();//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                            //Users.PhoneNumber = "010-6737-5288";//없으면 null이들어갈수도있다 -> if(Users.PhoneNumber==null) 으로 활용가능
                            if (Users.PhoneNumber == null)
                                Users.PhoneNumber = "";
                            else
                                Users.PhoneNumber = Users.PhoneNumber.replace("+82", "0");
                            Users.DeviceOS = Build.VERSION.RELEASE;
                            if (Users.DeviceOS == null)
                                Users.DeviceOS = "";
                            Users.Remark = "";
                            Users.DeviceName = BluetoothAdapter.getDefaultAdapter().getName();//블루투스 권한 필요 manifest확인: 블루투스가 없으면 에러남
                        } catch (Exception e) {
                    /*String str=e.getMessage();
                    String str2=str;*/
                        } finally {
                            InsertAppLoginHistory();
                        }
                    }
                }
            }
        }
    }


    /*private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int handle = intent.getIntExtra(Constants.EXTRA_HANDLE, 0);
            int seq = intent.getIntExtra(Constants.EXTRA_INT_DATA3, 0);
            if(action.equals(Constants.ACTION_BARCODE_CALLBACK_DECODING_DATA))
            {
                byte[] data = intent.getByteArrayExtra(Constants.EXTRA_BARCODE_DECODING_DATA);
                int symbology = intent.getIntExtra(Constants.EXTRA_INT_DATA2, -1);
                String dataResult = "";
                if(data!=null)
                {
                    dataResult = new String(data);
                    if(dataResult.contains("�"))
                    {
                        try {
                            dataResult = new String(data, "Shift-JIS");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    String scanResult;
                    scanResult = dataResult.replaceAll("\n","");

                    //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    //Intent i = new Intent(getBaseContext(), ActivityStockOut.class);

                    *//*mHandler = new Handler();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog = ProgressDialog.show(MainActivity.this, "",
                                    "잠시만 기다려 주세요.", true);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 4000);
                        }
                    });*//*
                    new GetStockOutMaster(scanResult).execute(getString(R.string.service_address) + "getStockOutMaster");// 마스터 가져온 후 -> 디테일 가져온다.

                }
            }
            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_REQUEST_SUCCESS))
            {
                if(seq == SEQ_BARCODE_OPEN)
                {
                    mBarcodeHandle = intent.getIntExtra(Constants.EXTRA_HANDLE, 0);
                    mCurrentStatus = STATUS_OPEN;
                }
                else if(seq == SEQ_BARCODE_CLOSE)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(seq == SEQ_BARCODE_GET_STATUS)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(seq == SEQ_BARCODE_SET_TRIGGER_ON) mCurrentStatus = STATUS_TRIGGER_ON;
                else if(seq == SEQ_BARCODE_SET_TRIGGER_OFF) mCurrentStatus = STATUS_OPEN;
                else if(seq == SEQ_BARCODE_SET_PARAMETER)
                {
                }
                else
                {
                }

            }
            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_REQUEST_FAILED))
            {
                int result = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0);
                if(result == Constants.ERROR_BARCODE_DECODING_TIMEOUT)
                {
                }
                else if(result == Constants.ERROR_NOT_SUPPORTED)
                {
                }
                else if(result == Constants.ERROR_BARCODE_ERROR_USE_TIMEOUT)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(result == Constants.ERROR_BARCODE_ERROR_ALREADY_OPENED)
                {
                    mCurrentStatus = STATUS_OPEN;
                }
                else if(result == Constants.ERROR_BATTERY_LOW)
                {
                    mCurrentStatus = STATUS_CLOSE;
                }
                else if(result == Constants.ERROR_NO_RESPONSE)
                {
                    int notiCode = intent.getIntExtra(Constants.EXTRA_INT_DATA3, 0);
                    mCurrentStatus = STATUS_CLOSE;
                }
                else
                {
                }
                if(seq == SEQ_BARCODE_SET_PARAMETER)
                {
                }
            }
            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_PARAMETER))
            {
                int parameter = intent.getIntExtra(Constants.EXTRA_INT_DATA2, -1);
                String value = intent.getStringExtra(Constants.EXTRA_STR_DATA1);
            }
            else if(action.equals(Constants.ACTION_BARCODE_CALLBACK_GET_STATUS))
            {
                int status = intent.getIntExtra(Constants.EXTRA_INT_DATA2, 0);
            }

        }
    };*/

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
