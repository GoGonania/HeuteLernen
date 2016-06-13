package de.keplerware.heutelernen.screens;

import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.MyFragment;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MySpinner;
import de.keplerware.heutelernen.ui.MyText;

public class FragmentMain extends MyFragment {
    private MySpinner s;
    private LinearLayout l;
    private CheckBox filter;

    public void send(){
        String f = s.getSelectedItem().toString();
        l.removeAllViews();

        Internet.angebote(f, Sitzung.info.klasseZahl, filter.isChecked(), new Internet.AngebotListener(){
            public void ok(Internet.Angebot[] as){
                if(as == null){
                    l.addView(new MyText("Es gibt für dich für dieses Fach leider keine passenden Nachhilfelehrer"));
                } else{
                    MyList<Internet.Angebot> liste = new MyList<Internet.Angebot>(as){
                        public View view(final Internet.Angebot a){
                            View r = Screen.inflate(R.layout.user);
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

    public View create(){
        View v = Screen.inflate(R.layout.main);
        l = (LinearLayout) v.findViewById(R.id.liste);
        s = (MySpinner) v.findViewById(R.id.main_spinner);
        filter = (CheckBox) v.findViewById(R.id.main_cb_filter);
        s.fill(R.array.facher);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long ll){
                send();
            }

            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton compoundButton, boolean b){
                send();
            }
        });
        return v;
    }
}
