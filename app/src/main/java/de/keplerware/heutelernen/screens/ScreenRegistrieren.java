package de.keplerware.heutelernen.screens;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Save;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.Util.Listener;

public class ScreenRegistrieren extends Screen{
	private EditText v_vname;
	private EditText v_nname;
	private EditText v_passwort1;
	private EditText v_passwort2;
	private EditText v_mail;
	private EditText v_ort;
	private EditText v_klasse;

	private RadioButton klasseFrei;
    private RadioButton klasseNichtFrei;
	
	private boolean frei = true;
	
	public int getLayout(){
		return R.layout.start;
	}

	public String getTitle(){
		return "Konto erstellen";
	}

	public void show(){
		((RadioButton) find(R.id.start_alterfrei)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				frei = isChecked;
				find(R.id.start_klasse).setActivated(isChecked);
			}
		});
		
		v_vname = (EditText) find(R.id.start_vname);
		v_nname = (EditText) find(R.id.start_nname);
		v_passwort1 = (EditText) find(R.id.login_passwort);
		v_passwort2 = (EditText) find(R.id.start_passwort2);
		v_ort = (EditText) find(R.id.start_ort);
		v_mail = (EditText) find(R.id.login_email);
		v_klasse = (EditText) find(R.id.start_klasse);
        klasseFrei = (RadioButton) find(R.id.start_alterfrei);
        klasseNichtFrei = (RadioButton) find(R.id.start_alternichtfrei);

        klasseFrei.setChecked(true);

        klasseFrei.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b){
                frei = b;
                v_klasse.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
            }
        });
		
		find(R.id.login_register).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Util.setScreen(new ScreenLogin());
			}
		});
		
		find(R.id.login_login).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String vname = v_vname.getEditableText().toString().trim();
				String nname = v_nname.getEditableText().toString().trim();
				final String p1 = v_passwort1.getEditableText().toString().trim();
				String p2 = v_passwort2.getEditableText().toString().trim();
				String ort = v_ort.getEditableText().toString().trim();
				final String mail = v_mail.getEditableText().toString().trim();
				int klasse = 0;
				if(frei){
					try{
						klasse = Integer.parseInt(v_klasse.getEditableText().toString());
					}catch(Exception e){}
				} else{
					klasse = 13;
				}
				
				if(vname.isEmpty() || nname.isEmpty() || p1.isEmpty() || p2.isEmpty() || ort.isEmpty() || mail.isEmpty()){
					Util.toast("Bitte fülle alle Felder aus!");
				} else{
					if(p1.length() >= 6){
						if(p1.equals(p2)){
							if(mail.contains("@")){
								if(!frei || (klasse >= 5 && klasse <= 12)){
									Internet.register(vname, nname, klasse, mail, ort, p1, new Internet.RegisterListener(){
										public void ok(){
											Util.toast("Registriert!");
											Save.setData(mail, p1, -1);
											Util.setScreen(new ScreenLogin());
										}
										
										public void fail(boolean c){
                                            if(!c) Util.toast("Diese E-Mail wurde bereits verwendet!");
                                        }
									});
								} else{
									Util.toast("Erlaubte Klasse: 5-12");
								}
							} else{
								Util.toast("Bitte gib eine gültige E-Mail an!");
							}
						} else{
							Util.toast("Passwörter stimmen nicht überein!");
						}
					} else{
						Util.toast("Dein Passwort muss mindestens 6 Zeichen lang sein!");
					}
				}
			}
		});
	}

	public Screen getParentScreen(){
		return new ScreenLogin();
	}
}
