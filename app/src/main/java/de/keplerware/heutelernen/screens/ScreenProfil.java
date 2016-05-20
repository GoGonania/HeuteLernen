package de.keplerware.heutelernen.screens;

import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.Internet.Angebot;
import de.keplerware.heutelernen.Internet.AngebotListener;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Rang;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.ui.MyButton;
import de.keplerware.heutelernen.ui.MyText;

public class ScreenProfil extends Screen{
	public static UserInfo info;
	private static boolean owner;
	
	public static void show(UserInfo i){
		info = i;
		owner = (i.id == Sitzung.info.id);
		Util.setScreen(new ScreenProfil());
	}
	
	public Screen getParentScreen(){
		return new ScreenMain();
	}

	public int getLayout(){
		return R.layout.profil;
	}

	public String getTitle(){
		return owner?"Dein Profil":"Profil";
	}
	
	public void menu(Menu m){
		if(!owner){
			m.add("Chat öffnen").setIcon(R.drawable.chat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
				public boolean onMenuItemClick(MenuItem p1){
					ScreenChat.show(info, true);
					return true;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		
		if(Sitzung.rang(Rang.MODERATOR)){
			m.add("Info").setIcon(R.drawable.info).setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item){
					Dialog.alert(info.name+":", "Benutzer-ID: "+info.id+"\nRang: "+info.rang+"\nMail: "+info.mail);
					return true;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}		
	}

	public void show(){
		((TextView) find(R.id.profil_name)).setText(info.name);
		((TextView) find(R.id.profil_details)).setText(info.klasse+"\nWohnort: "+info.ort+"");
		final LinearLayout angebote = (LinearLayout) find(R.id.profil_angebote);
		
		Button suchen = new MyButton("Nachhilfefächer", true){
			public void klick(){
				Internet.angebote(info, new AngebotListener(){
					public void ok(Angebot[] as){
						angebote.removeAllViews();
						if(as.length == 0){
							angebote.addView(new MyText("Keine Angebote gefunden!"));
						} else{
							for(Angebot a : as){
								angebote.addView(new MyText(a.fach));
							}
						}
					}
					
					public void fail(){}
				});
			}
		};
		
		angebote.addView(suchen);
	}
}
