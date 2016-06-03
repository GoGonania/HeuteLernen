package de.keplerware.heutelernen;

import android.content.Intent;

public class Starter{
    public Intent intent;

    public Starter(Class screen){
        intent = new Intent(HeuteLernen.context, screen);
    }

    public void send(){
        Util.screen.startActivity(intent);
    }
}
