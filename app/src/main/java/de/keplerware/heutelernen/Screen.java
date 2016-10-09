package de.keplerware.heutelernen;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;

import de.keplerware.heutelernen.screens.ScreenLogin;
import de.keplerware.heutelernen.screens.ScreenHome;

public abstract class Screen extends AppCompatActivity{
    private static LayoutInflater inflater;
    public Toolbar bar;
    public boolean aktiv;

    public abstract int getLayout();
    public abstract void show();
    public abstract String getTitel();

    protected void onCreate(Bundle savedInstanceState){
        Util.init(this);
        aktiv();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup base = inflate(R.layout.base);
        ViewGroup content = (ViewGroup) base.findViewById(R.id.content);
        super.onCreate(savedInstanceState);
        content.removeAllViews();
        content.addView(inflate(getLayout()));
        setContentView(base);
        bar = (Toolbar) findViewById(R.id.toolbar);
        show();
        bar.setTitle(getTitel());
        bar.setSubtitle(null);
        try{bar.setElevation(this instanceof ScreenHome ? 0 : 10);}catch(NoSuchMethodError e){}
        setSupportActionBar(bar);
        if(this instanceof ScreenHome || this instanceof ScreenLogin){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        menu(menu);
        return true;
    }

    public static ViewGroup inflate(int r){
        return (ViewGroup) inflater.inflate(r, null);
    }

    protected void onPause(){
        super.onPause();
        aktiv = false;
    }

    private void aktiv(){
        aktiv = true;
        Util.screen = this;
    }

    protected void onResume(){
        super.onResume();
        aktiv();
    }

    public void finish(){
        aktiv = false;
        super.finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void menu(Menu m){}
	public boolean event(int t, Object... d){return false;}
}
