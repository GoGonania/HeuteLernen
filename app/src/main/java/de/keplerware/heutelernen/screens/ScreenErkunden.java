package de.keplerware.heutelernen.screens;

import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.manager.BildManager;
import de.keplerware.heutelernen.manager.DataManager;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MySpinner;
import de.keplerware.heutelernen.ui.MyText;

public class ScreenErkunden extends Screen{
    public int getLayout(){
        return R.layout.erkunden;
    }

    private boolean sending;
    private MySpinner s;
    private MySpinner s2;
    private LinearLayout l;

    public void send(){
        if(sending) return;
        sending = true;
        l.removeAllViews();

        Internet.angebote(s.getSelectedItemPosition(), 0, s2.getSelectedItemPosition(), new Internet.AngebotListener(){
            public void ok(Internet.Angebot[] as){
                sending = false;
                if(as == null){
                    l.addView(new MyText("Es gibt für dieses Fach und diese Schule leider keine passenden Nachhilfelehrer"){{setGravity(Gravity.CENTER);}});
                } else{
                    MyList<Internet.Angebot> liste = new MyList<Internet.Angebot>(as){
                        public View view(final Internet.Angebot a){
                            View r = Screen.inflate(R.layout.user);
                            r.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View v){
                                    Dialog.alert("Anmelden", "Für weitere Aktionen musst du angemeldet sein!");
                                }
                            });
                            BildManager.get(a.info.id, r.findViewById(R.id.angeboteitem_bild), ScreenErkunden.this);
                            ((TextView) r.findViewById(R.id.angeboteitem_detail)).setText(a.info.schulInfo);
                            ((TextView) r.findViewById(R.id.angebotitem_name)).setText(a.info.name);
                            return r;
                        }
                    };
                    l.addView(liste);
                }
            }
            public void fail(){
                sending = false;
            }
        });
    }

    public void show(){
        l = (LinearLayout) findViewById(R.id.liste);
        s = (MySpinner) findViewById(R.id.main_spinner);
        s.fill(DataManager.faecher);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long ll){
                send();
            }

            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        s2 = (MySpinner) findViewById(R.id.main_spinner2);
        s2.fill(DataManager.schulen);
        s2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long ll){
                send();
            }

            public void onNothingSelected(AdapterView<?> adapterView){}
        });
    }

    public String getTitel(){
        return "Nachhilfeangebot erkunden";
    }
}
