package com.example.kumkangreader.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kumkangreader.R;

import java.util.HashMap;
import java.util.Map;

public class Activity_test extends AppCompatActivity {

    private LinearLayout linearLayout;
    public int btnNo;
    public int height = 0;
    public static Context mContext; // PotoListActivity에서 확인버튼 클릭 시 버튼 색 변경을 위한 전달함수
    public Map<String, Button> btnList = new HashMap<String, Button>(); // 버튼 정보를 담는 리스트

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test);

        mContext = this;

        final String[] floor = {"-층 선택-", "1층", "2층", "3층", "4층", "5층", "6층"};
        Spinner spiner = (Spinner) findViewById(R.id.spinner1);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, floor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner.setAdapter(adapter);

        spiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Selected Floor: " + floor[position], Toast.LENGTH_SHORT).show(); // 선택한 층을 메시지박스로 보여준다.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spiner.setSelection(0);

        // 카메라 버튼을 생성한다.
        MakeCameraButton();

   /*     TextView textView = new TextView(this);
        textView.setText("Anchor를 선택해주세요.");
        textView.setBackgroundColor(Color.parseColor("#F2F2F2"));
        textView.setTextColor(Color.parseColor("#8C8C8C"));
        textView.setTextAlignment(textView.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(textView);*/


    }

   /* @Override // 생성한 menu.xml의 요소들을 실제 뷰로 inflate 시켜준다.
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }*/

    @Override // 생성한 메뉴들을 누르면 어떤행동을 취할지 설정
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    // 카메라 버튼을 생성한다.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void MakeCameraButton() {

        //int width = linearLayout.getWidth() / 5;
        //int height = linearLayout.getHeight() / 6;

        // 버튼 선언
        for(int i = 1; i < 31; i++) {
            final Button btn = new Button(this);
            btn.setId(i);
            btn.setText(String.valueOf(i));
            btn.setBackgroundColor(Color.parseColor("#DEEBF7"));

            //GradientDrawable bgShape = (GradientDrawable) linearLayout.getBackground();
            //bgShape.setColor(Color.rgb(222,235,247));
            //linearLayout.setBackground(bgShape);

            if(i > 0 && i <=5)
                linearLayout = (LinearLayout) findViewById(R.id.view_btn1);
            else if(i > 5 && i <=10)
                linearLayout = (LinearLayout) findViewById(R.id.view_btn2);
            else if(i > 10 && i <=15)
                linearLayout = (LinearLayout) findViewById(R.id.view_btn3);
            else if(i > 15 && i <=20)
                linearLayout = (LinearLayout) findViewById(R.id.view_btn4);
            else if(i > 20 && i <=25)
                linearLayout = (LinearLayout) findViewById(R.id.view_btn5);
            else if(i > 25 && i <=30)
                linearLayout = (LinearLayout) findViewById(R.id.view_btn6);

            //btn.setWidth(10);
            //btn.setHeight(10);


            //LinearLayout.LayoutParams.MATCH_PARENT
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(5,5,5,5);
            params.weight = 1;
            btn.setLayoutParams(params);

            btnList.put(String.valueOf(i), btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // camera Application을 실행 -> Intent를 통해 카메라를 불러온다.
                    startActivityForResult(cameraApp, 101);

                    btnNo = btn.getId();

                    /*startActivityForResult(cameraApp, 10000);*/
                }
            });

            linearLayout.addView(btn);
        }

        //LinearLayout layout = (LinearLayout) findViewById(R.id.layout_btn);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params.weight = 7;
        //layout.setLayoutParams(params);
    }

    @Override // 카메라 촬영 시 onActivityResult를 통해 사진을 가져온다.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      /*  // 이미지를 비트맵으로 변경하여 용량을 줄임
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8; // 8분의 1크기로 비트맵 객체를 생성
        Bitmap bitmap = (Bitmap) data.getExtras().get("data"); // data에 이미지 정보가 담겨있다.

        Intent intent = new Intent(this, PotoListActivity.class);
        intent.putExtra("img", bitmap); // putExtra를 통해 호출한 액티비티로 사진 객체를 전송한다.
        intent.putExtra("btnNo", btnNo); // 선택한 버튼의 id값을 넘긴다. -> 서버에 저장 시 버튼 색 변경을 위함
        startActivity(intent);*/

    }

    public void ChangeButtonColor(int buttonNumber) {

        if(btnList.containsKey(String.valueOf(buttonNumber))) {
            Button button = (Button) btnList.get(String.valueOf(buttonNumber));
            button.setBackgroundColor(Color.parseColor("#D9D9D9"));
        }
    }

    @Override // onCreate()는 레이아웃이 그려지기전에 호출되기 때문에 해당 함수를 오버라이딩해서 값을 구해야한다.
    public void onWindowFocusChanged(boolean hasFocus) {
        height = linearLayout.getHeight();
    }

}