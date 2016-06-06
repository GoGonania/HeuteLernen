package de.keplerware.heutelernen.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.keplerware.heutelernen.HeuteLernen;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.MyFragment;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MySpinner;

public class FragmentMain extends MyFragment {
    private MySpinner s;
    private LinearLayout l;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.main, null);
        l = (LinearLayout) v.findViewById(R.id.liste);
        ((LinearLayout) v.findViewById(R.id.parameter)).addView(s = new MySpinner(R.array.facher));

        v.findViewById(R.id.create).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String f = s.getSelectedItem().toString();
                Internet.angebotAufgeben(f, Sitzung.info.klasseZahl, Sitzung.info.id, new Util.Listener() {
                    public void ok(String data){
                        if(!data.isEmpty()) Util.toast("Angebot wurde aufgegeben!");
                    }
                    public void fail(Exception e){}
                });
            }
        });

        v.findViewById(R.id.suchen).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String f = s.getSelectedItem().toString();
                l.removeAllViews();

                Internet.angebote(f, Sitzung.info.klasseZahl, new Internet.AngebotListener(){
                    public void ok(Internet.Angebot[] as){
                        if(as.length == 0){
                            TextView v = new TextView(HeuteLernen.context);
                            v.setText("Keine Ergebnisse gefunden!");
                            l.addView(v);
                        } else{
                            MyList<Internet.Angebot> liste = new MyList<Internet.Angebot>(as){
                                public View view(final Internet.Angebot a){
                                    View r = inflater.inflate(R.layout.angebot_item, null);
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
