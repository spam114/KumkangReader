package com.example.kumkangreader.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.kumkangreader.BackPressControl;
import com.example.kumkangreader.R;

public class LoginActivity extends BaseActivity {

    BackPressControl backpressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        backpressed = new BackPressControl(this);


    }

    /**
     * 버튼 클릭
     */
    public void mOnClick(View v) {

        switch (v.getId()) {
            case R.id.txtRegister:
                Intent loginIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(loginIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {

        backpressed.onBackPressed();
    }
}
