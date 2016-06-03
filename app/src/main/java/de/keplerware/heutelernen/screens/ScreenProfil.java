package de.keplerware.heutelernen.screens;

import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Rang;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Starter;

public class ScreenProfil extends Screen{
	private FragmentProfil f;
    private static UserInfo info;
	
	public static void show(UserInfo i){
        info = i;
		new Starter(ScreenProfil.class).send();
	}

    public String getTitel(){
        return info.owner()?"Dein Profil":"Profil";
    }
	
	public void menu(Menu m){
		if(!f.owner){
			m.add("Chat öffnen").setIcon(R.drawable.chat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
				public boolean onMenuItemClick(MenuItem p1){
					ScreenChat.show(f.info);
					return true;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		
		if(Sitzung.rang(Rang.MODERATOR)){
			m.add("Info").setIcon(R.drawable.info).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item){
					Dialog.alert(f.info.name+":", "Benutzer-ID: "+f.info.id+"\nRang: "+f.info.rang+"\nMail: "+f.info.mail);
					return true;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}		
	}

    public int getLayout(){
        return R.layout.profil_page;
    }

    public void show(){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        f = FragmentProfil.show(info);
        t.add(R.id.profil_frame, f);
        t.commit();
	}
}
