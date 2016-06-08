package de.keplerware.heutelernen;

import android.app.Application;
import android.content.Context;

public class HeuteLernen extends Application{
    public static Context context;

    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        Util.init(context);
    }
}
