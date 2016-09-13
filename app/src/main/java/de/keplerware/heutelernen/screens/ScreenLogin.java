package de.keplerware.heutelernen.screens;

import android.view.View;
import android.widget.EditText;

import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Internet.LoginListener;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.LoginError;
import de.keplerware.heutelernen.MyService;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Save;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Starter;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.DataManager;
import de.keplerware.heutelernen.manager.NachrichtenManager;

public class ScreenLogin extends Screen{
    public static boolean first;
	private EditText mail;
	private EditText passwort;

    public int getLayout(){
        return R.layout.login;
    }

	public void show(){
		mail = (EditText) findViewById(R.id.login_email);
		passwort = (EditText) findViewById(R.id.login_passwort);
		
		String m = Save.mail;
		String p = Save.passwort;
		
		if(m != null) mail.setText(m);
		if(p != null) passwort.setText(p);
		
		findViewById(R.id.login_register).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
                DataManager.load(new Util.Listener(){
                    public void ok(String data){
                        new Starter(ScreenRegistrieren.class).send();
                    }

                    public void fail(Exception e){}
                });
			}
		});
		
		findViewById(R.id.login_login).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				send();
			}
		});
		findViewById(R.id.login_erkunden).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
                DataManager.load(new Util.Listener(){
                    public void ok(String data){
                        new Starter(ScreenErkunden.class).send();
                    }

                    public void fail(Exception e){}
                });
			}
		});

        if(first){
            Dialog.alert("Konto wurde erstellt!", "Wir freuen uns dich bei HeuteLernen begrüßen zu dürfen. Bei Rückmeldung stehen wir gerne unter der E-Mail auf unserer Internetseite bereit");
            first = false;
        }
		
		if(MyService.running) {
            send();
        } else{
            Util.checkUpdate();
        }
	}
	
	private void send(){
		final String m = mail.getText().toString();
		final String p = passwort.getText().toString();
		
		if(m.isEmpty() || p.isEmpty()) return;

        DataManager.load(new Util.Listener(){
            public void ok(String data){
                Sitzung.login(m, p, false, new LoginListener(){
                    public void ok(UserInfo info){
                        Util.toast("Eingeloggt als "+info.name+"!");
                        ScreenHome.show(NachrichtenManager.unread() > 0 ? 0 : 1).replace();
                    }

                    public void fail(int e) {
                        if(e == LoginError.Passwort) Util.toast("Fehler beim Einloggen!\nFalsches Passwort?");
                        if(e == LoginError.Bestaetigen) Util.toast("Du hast deine E-Mail noch nicht bestätigt!");
                    }
                });
            }
            public void fail(Exception e){}
        });

	}

    public String getTitel(){
        return "Einloggen";
    }
}
