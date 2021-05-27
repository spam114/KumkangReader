package com.example.kumkangreader.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.MainActivity2;
import com.example.kumkangreader.R;
import com.google.android.material.textfield.TextInputEditText;

public class FragmentInputCoil extends Fragment implements BaseActivityInterface {

    TextInputEditText edtScan;
    Context context;
    Button btnMoveCoil;

    public FragmentInputCoil(Context context){
        this.context=context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout3, container, false);
        this.edtScan=rootView.findViewById(R.id.edtScan);
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
                    ((MainActivity2)(context)).inputCoil(v.getText().toString());
                }
                return false;
            }
        });
        this.btnMoveCoil = rootView.findViewById(R.id.btnMoveCoil);
        this.btnMoveCoil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity2)(context)).getMaxLengthTable();
            }
        });

        return rootView;
    }


    @Override
    public int checkTagState(String tag) {
        return 0;
    }

    @Override
    public void progressON() {

    }

    @Override
    public void progressON(String message) {

    }

    @Override
    public void progressOFF() {

    }
}
