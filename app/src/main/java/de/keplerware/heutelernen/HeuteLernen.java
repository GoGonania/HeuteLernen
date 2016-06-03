package de.keplerware.heutelernen;

import android.app.Application;
import android.content.Context;

public class HeuteLernen extends Application{
    public static Context context;
    public static boolean pause = true;

    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        Util.init(context);
    }
}
