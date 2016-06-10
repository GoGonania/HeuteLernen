package de.keplerware.heutelernen.screens;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.MyFragment;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MySpinner;
import de.keplerware.heutelernen.ui.MyText;

public class FragmentMain extends MyFragment {
    private MySpinner s;
    private LinearLayout l;
    private MyFragment fr;

    public View create(){
        fr = this;
        View v = inflate(R.layout.main);
        l = (LinearLayout) v.findViewById(R.id.liste);
        s = (MySpinner) v.findViewById(R.id.main_spinner);
        s.fill(R.array.facher);
        v.findViewById(R.id.suchen).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String f = s.getSelectedItem().toString();
                l.removeAllViews();

                Internet.angebote(f, Sitzung.info.klasseZahl, new Internet.AngebotListener(){
                    public void ok(Internet.Angebot[] as){
                        if(as.length == 0){
                            l.addView(new MyText("Es gibt für dich für dieses Fach leider keine passenden Nachhilfefächer"));
                        } else{
                            MyList<Internet.Angebot> liste = new MyList<Internet.Angebot>(as){
                                public View view(final Internet.Angebot a){
                                    View r = fr.inflate(R.layout.angebot_item);
                                    r.setOnClickListener(new View.OnClickListener(){
                                        public void onClick(View v){
                                            ScreenProfil.show(a.info);
                                        }
                                    });
                                    ((TextView) r.findViewById(R.id.angeboteitem_detail)).setText(a.info.klasse+" | "+a.info.schuleText);
                                    ((TextView) r.findViewById(R.id.angebotitem_name)).setText(a.info.name);
                                    return r;
                                }
                            };
                            l.addView(liste);
                        }
                    }
                    public void fail(){}
                });
            }
        });
        return v;
    }
}
