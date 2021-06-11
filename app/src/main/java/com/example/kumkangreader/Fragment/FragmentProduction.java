package com.example.kumkangreader.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kumkangreader.MainActivity2;
import com.example.kumkangreader.R;
import com.google.android.material.textfield.TextInputEditText;

public class FragmentProduction extends Fragment {
    TextInputEditText edtScan;
    Context context;

    public FragmentProduction(){

    }

    public FragmentProduction(Context context){
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout2, container, false);
        this.edtScan=rootView.findViewById(R.id.edtScan);
        //this.edtScan.setText("WP0-210520-001/WP-S01");
        this.edtScan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    edtScan.setGravity(Gravity.START);
                }
                else{
                    edtScan.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }
        });

        this.edtScan.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE){ // IME_ACTION_SEARCH , IME_ACTION_GO


                    try{
                        ((MainActivity2)(context)).getProductionBasicInfo(v.getText().toString());
                    }
                    catch (ArrayIndexOutOfBoundsException aoe){
                        Toast.makeText(context, "올바른 발주서 태그가 아닙니다.", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });


        return rootView;
    }
}