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
import de.keplerware.heutelernen.manager.BildManager;
import de.keplerware.heutelernen.manager.DataManager;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MySpinner;
import de.keplerware.heutelernen.ui.MyText;

public class FragmentMain extends MyFragment {
    private MySpinner s;
    private LinearLayout l;
    private CheckBox filterK;
    private CheckBox filterS;

    public void send(){
        l.removeAllViews();

        Internet.angebote(s.getSelectedItemPosition(), filterK.isChecked()?Sitzung.info.klasseZahl:0, filterS.isChecked()?Sitzung.info.schule:-1, new Internet.AngebotListener(){
            public void ok(Internet.Angebot[] as){
                if(as == null){
                    l.addView(new MyText("Es gibt für dich für dieses Fach leider keine passenden Nachhilfelehrer"){{setGravity(Gravity.CENTER);}});
                } else{
                    MyList<Internet.Angebot> liste = new MyList<Internet.Angebot>(as){
                        public View view(final Internet.Angebot a){
                            View r = Screen.inflate(R.layout.user);
                            r.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View v){
                                    ScreenProfil.show(a.info);
                                }
                            });
                            BildManager.get(a.info.id, r.findViewById(R.id.angeboteitem_bild), getActivity());
                            ((TextView) r.findViewById(R.id.angeboteitem_detail)).setText(a.info.schulInfo);
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
        filterK = (CheckBox) v.findViewById(R.id.main_filter_klasse);
        filterS = (CheckBox) v.findViewById(R.id.main_filter_schule);
        s.fill(DataManager.faecher);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long ll){
                send();
            }

            public void onNothingSelected(AdapterView<?> adapterView){}
        });

        filterK.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton compoundButton, boolean b){
                send();
            }
        });
        filterS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton compoundButton, boolean b){
                send();
            }
        });
        return v;
    }
}
