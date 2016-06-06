package de.keplerware.heutelernen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.keplerware.heutelernen.screens.ScreenChat;
import de.keplerware.heutelernen.screens.ScreenLogin;

public class NotificationClick extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent){
        if(MyService.running){
            if(HeuteLernen.pause){
                Starter s = new Starter(ScreenLogin.class, Util.screen != null ? Util.screen : context);
                s.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                s.send();
            } else{
                Starter s = new Starter(ScreenChat.class, Util.screen != null ? Util.screen : context);
                s.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                s.intent.putExtras(intent);
                s.send();
            }
        }
    }
}
