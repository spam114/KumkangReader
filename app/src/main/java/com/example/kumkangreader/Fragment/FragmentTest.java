package com.example.kumkangreader.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kumkangreader.Interface.BaseActivityInterface;
import com.example.kumkangreader.R;

public class FragmentTest extends Fragment implements BaseActivityInterface {

    public FragmentTest(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.layout1, container, false);



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
