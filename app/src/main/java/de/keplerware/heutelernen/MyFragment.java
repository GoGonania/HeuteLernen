package de.keplerware.heutelernen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class MyFragment extends Fragment{
    public boolean paused = true;

    public abstract View create();

    public void onResume(){
        super.onResume();
        paused = false;
    }

    public void onPause(){
        super.onPause();
        paused = true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return create();
    }
}
