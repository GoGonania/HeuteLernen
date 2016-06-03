package de.keplerware.heutelernen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;

public abstract class Screen extends AppCompatActivity{
    private LayoutInflater inflater;
    public Toolbar bar;

    public abstract int getLayout();
    public abstract void show();
    public abstract String getTitel();

    protected void onCreate(Bundle savedInstanceState){
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup base = inflate(R.layout.base);
        ViewGroup content = (ViewGroup) base.findViewById(R.id.content);
        super.onCreate(savedInstanceState);
        Util.screen = this;
        System.out.println("Creating "+getClass().getSimpleName());
        content.removeAllViews();
        content.addView(inflate(getLayout()));
        setContentView(base);
        show();
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar);
        bar.setTitle(getTitel());
        bar.setSubtitle(null);
    }

    public ViewGroup inflate(int r){
        return (ViewGroup) inflater.inflate(r, null);
    }

    protected void onPause(){
        super.onPause();
        HeuteLernen.pause = true;
    }

    protected void onResume(){
        super.onResume();
        HeuteLernen.pause = false;
    }
	
	public boolean event(int t, Object... d){return false;}
}
