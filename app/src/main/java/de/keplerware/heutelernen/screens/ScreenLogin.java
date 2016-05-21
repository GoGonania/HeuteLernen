package de.keplerware.heutelernen.screens;

import android.view.View;
import android.widget.EditText;
import de.keplerware.heutelernen.Internet.InfoListener;
import de.keplerware.heutelernen.Internet.LoginListener;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.LoginError;
import de.keplerware.heutelernen.MainActivity;
import de.keplerware.heutelernen.MyService;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Save;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.ProfilManager;

public class ScreenLogin extends Screen{
	private EditText mail;
	private EditText passwort;
	
	public int getLayout(){
		return R.layout.login;
	}

	public String getTitle(){
		return "Einloggen";
	}

	public void show(){
		mail = (EditText) find(R.id.login_email);
		passwort = (EditText) find(R.id.login_passwort);
		
		String m = Save.mail();
		String p = Save.passwort();
		
		if(m != null) mail.setText(m);
		if(p != null) passwort.setText(p);
		
		find(R.id.login_register).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				Util.setScreen(new ScreenRegistrieren());
			}
		});
		
		find(R.id.login_login).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				send();
			}
		});
		
		if(MyService.running) send();
	}
	
	private void send(){
		String m = mail.getText().toString();
		String p = passwort.getText().toString();
		
		if(m.isEmpty() || p.isEmpty()) return;
		
		Sitzung.login(m, p, false, new LoginListener(){
			public void ok(UserInfo info){
				Util.toast("Eingeloggt als "+info.name+"!");
				if(MainActivity.aktivChat != -1 && MainActivity.aktivID == Sitzung.info.id){
					ProfilManager.get(MainActivity.aktivChat, new InfoListener(){
						public void ok(UserInfo info){
							ScreenChat.show(info, false);
						}
						
						public void fail(){}
					});
				} else{
					Util.setScreen(new ScreenMain());
				}
				MainActivity.aktivChat = -1;
			}

			public void fail(int e) {
				if(e == LoginError.Passwort) Util.toast("Fehler beim Einloggen!\nFalsches Passwort ?");
				if(e == LoginError.Bestaetigen) Util.toast("Du hast deine E-Mail noch nicht bestätigt!");
			}
		});
	}

	public Screen getParentScreen(){
		return null;
	}
}
