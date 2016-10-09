package de.keplerware.heutelernen;

import android.content.Context;
import android.content.Intent;

public class Starter{
    public final Intent intent;
    private final Context c;

    public Starter(Class screen){
        this(screen, Util.screen);
    }

    public Starter(Class screen, Context c){
        this.c = c;
        intent = new Intent(c, screen);
    }

    public void replace(){
        Util.screen.finish();
        send();
    }

    public void send(){
        c.startActivity(intent);
    }
}
