package de.keplerware.heutelernen.screens;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Rang;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.ProfilManager;
import de.keplerware.heutelernen.ui.MyText;

public class FragmentProfil extends Fragment{
    public UserInfo info;
    public boolean owner;
    private LinearLayout angebote;

    public static FragmentProfil show(UserInfo info){
        FragmentProfil f = new FragmentProfil();
        f.setArguments(ProfilManager.create(info));
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        if(getArguments() != null){
            info = ProfilManager.get(getArguments());
        } else{
            info = Sitzung.info;
            owner = true;
        }
        View v = inflater.inflate(R.layout.profil, null);
        ((TextView) v.findViewById(R.id.profil_name)).setText(info.name);
        ((TextView) v.findViewById(R.id.profil_details)).setText(info.klasse+"\nWohnort: "+info.ort+"\nSchule: "+info.schuleText);
        final TextView tB = (TextView) v.findViewById(R.id.profil_beschreibung);
        if(info.beschreibung.isEmpty()){
            tB.setTypeface(null, Typeface.ITALIC);
            tB.setText("Keine Beschreibung");
        } else{
            tB.setText(info.beschreibung);
        }
        angebote = (LinearLayout) v.findViewById(R.id.profil_angebote);

        boolean editP = Sitzung.rang(Rang.MODERATOR) || owner;

        ImageView editB = (ImageView) v.findViewById(R.id.profil_edit_beschreibung);

        editB.setVisibility(editP ? View.VISIBLE : View.GONE);

        if(editP){
            editB.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    String b = info.beschreibung;
                    Dialog.prompt(b.isEmpty() ? "Wähle deine Beschreibung" : "Ändere deine Beschreibung", b, new Dialog.PromptListener(){
                        public void ok(String text){
                            if(text.isEmpty()) return;
                            if(text.length() > 100){
                                Util.toast("Deine Beschreibung wurde auf 100 Zeichen gekürzt");
                                text = text.substring(0, 100);
                            }
                            final String text2 = text;
                            Internet.beschreibung(info.id, text, new Util.Listener(){
                                public void ok(String data){
                                    info.beschreibung = text2;
                                    tB.setText(text2);
                                }

                                public void fail(Exception e){}
                            });
                        }
                    });
                }
            });
        }


        return v;
    }

    public void onResume(){
        super.onResume();
        Internet.angebote(info, new Internet.AngebotListener(){
            public void ok(Internet.Angebot[] as){
                angebote.removeAllViews();
                if(as.length == 0){
                    angebote.addView(new MyText("Keine Angebote gefunden!"));
                } else{
                    for(Internet.Angebot a : as){
                        angebote.addView(new MyText(a.fach));
                    }
                }
            }

            public void fail(){
                angebote.removeAllViews();
                angebote.addView(new MyText("Keine Internetverbindung!"));
            }
        });
    }
}