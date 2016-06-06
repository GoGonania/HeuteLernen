package de.keplerware.heutelernen;

import android.support.v4.app.Fragment;

public class MyFragment extends Fragment{
    public boolean paused = true;

    public void onResume(){
        super.onResume();
        paused = false;
    }

    public void onPause(){
        super.onPause();
        paused = true;
    }
}
