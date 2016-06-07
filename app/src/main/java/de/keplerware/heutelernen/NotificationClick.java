package de.keplerware.heutelernen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import de.keplerware.heutelernen.screens.ScreenChat;
import de.keplerware.heutelernen.screens.ScreenLogin;

public class NotificationClick extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent){
        if(MyService.running){ //Service aktiv
            if(Sitzung.info == null){//Nicht eingeloggt -> LoginScreen mit NEWTASK
                Starter s = new Starter(ScreenLogin.class, context);
                s.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                s.send();
            } else{
                if(Util.screen == null || Util.screen.isFinishing()){//Keine Aktivity, aber eingeloggt -> Chat mit NEWTASK
                    Starter s = new Starter(ScreenChat.class, context);
                    s.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    s.intent.putExtras(intent);
                    s.send();
                } else{ //Aktivity und eingeloggt -> Chat
                    Starter s = new Starter(ScreenChat.class, Util.screen);
                    s.intent.putExtras(intent);
                    s.send();
                }
            }
        } else{ //Service nicht aktiv -> LoginScreen mit NEWTASK
            Starter s = new Starter(ScreenLogin.class, context);
            s.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            s.send();
        }
    }
}
