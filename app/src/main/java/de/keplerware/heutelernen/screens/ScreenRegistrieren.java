package de.keplerware.heutelernen.screens;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Save;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Starter;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.DataManager;
import de.keplerware.heutelernen.ui.MySpinner;

public class ScreenRegistrieren extends Screen{
	private MySpinner schule;
	private EditText v_vname;
	private EditText v_nname;
	private EditText v_passwort1;
	private EditText v_passwort2;
	private EditText v_mail;
	private EditText v_ort;
	private TextView v_klasse;
    private Button klassePlus;
    private Button klasseMinus;

    private View schulContainer;

    private int klasse = 8;

    public int getLayout(){
        return R.layout.register;
    }

	public void show(){
		v_vname = (EditText) findViewById(R.id.start_vname);
		v_nname = (EditText) findViewById(R.id.start_nname);
		v_passwort1 = (EditText) findViewById(R.id.login_passwort);
		v_passwort2 = (EditText) findViewById(R.id.start_passwort2);
		v_ort = (EditText) findViewById(R.id.start_ort);
		v_mail = (EditText) findViewById(R.id.login_email);
		v_klasse = (TextView) findViewById(R.id.register_klasse);
        schule = (MySpinner) findViewById(R.id.register_schule);
        klassePlus = (Button) findViewById(R.id.register_klassePlus);
        klasseMinus = (Button) findViewById(R.id.register_klasseMinus);
        schulContainer = findViewById(R.id.register_nochInSchule);
        RadioButton klasseFrei = (RadioButton) findViewById(R.id.start_alterfrei);

		schule.fill(DataManager.schulen);

        klasseFrei.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b){
                if(b){
                    klasse = 8;
                    updateKlasse();
                    schulContainer.animate().alpha(1).translationY(0).setDuration(400).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation){
                            schulContainer.setVisibility(View.VISIBLE);
                        }
                    });
                } else{
                    klasse = 13;
                    schulContainer.animate().alpha(0).translationY(-schulContainer.getHeight()).setDuration(400).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation){
                            schulContainer.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        klassePlus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                klasse++;
                updateKlasse();
            }
        });

        klasseMinus.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                klasse--;
                updateKlasse();
            }
        });
		findViewById(R.id.login_login).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String vname = v_vname.getEditableText().toString().trim();
				String nname = v_nname.getEditableText().toString().trim();
				final String p1 = v_passwort1.getEditableText().toString().trim();
				String p2 = v_passwort2.getEditableText().toString().trim();
				String ort = v_ort.getEditableText().toString().trim();
				final String mail = v_mail.getEditableText().toString().trim();
				
				if(vname.isEmpty() || nname.isEmpty() || p1.isEmpty() || p2.isEmpty() || ort.isEmpty() || mail.isEmpty()){
					Util.toast("Bitte fülle alle Felder aus!");
				} else{
					if(p1.length() >= 6){
						if(p1.equals(p2)){
							if(mail.contains("@")){
                                Internet.register(vname, nname, klasse, mail, ort, p1, schule.getSelectedItemPosition(), new Internet.RegisterListener(){
                                    public void ok(){
                                        Save.setData(mail, p1, -1);
                                        ScreenLogin.first = true;
                                        new Starter(ScreenLogin.class).replace();
                                    }
										
                                    public void fail(boolean c){
                                        if(!c) Util.toast("Diese E-Mail wurde bereits verwendet!");
                                    }
                                });
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

        updateKlasse();
	}

    public void updateKlasse(){
        klasseMinus.setEnabled(klasse > 5);
        klassePlus.setEnabled(klasse < 12);
        v_klasse.setText(klasse+". Klasse");
    }

    public String getTitel(){
        return "Konto erstellen";
    }
}
