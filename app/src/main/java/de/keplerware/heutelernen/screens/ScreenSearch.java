package de.keplerware.heutelernen.screens;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.manager.BildManager;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MyText;

public class ScreenSearch extends Screen{
    private EditText input;
    private LinearLayout content;
    private TextView ergebnisse;

    public int getLayout(){
        return R.layout.search;
    }

    public void show(){
        content = (LinearLayout) findViewById(R.id.search_content);
        input = (EditText) findViewById(R.id.search_input);
        ergebnisse = (TextView) findViewById(R.id.search_ergebnisse);

        findViewById(R.id.search_submit).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                search();
            }
        });

        input.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    search();
                    return true;
                }
                return false;
            }
        });

        ergebnisse.setVisibility(View.GONE);
    }

    private void set(String text){
        content.removeAllViews();
        content.addView(new MyText(text));
    }

    public void search(){
        final String text = input.getEditableText().toString().trim();

        if(!text.isEmpty()){
            ergebnisse.setVisibility(View.GONE);
            set("Suche Benutzer '"+text+"'...");
            Internet.benutzerSuchen(text, false, new Internet.SuchListener(){
                public void ok(Internet.UserInfo[] infos){
                    if(infos == null){
                        set("Für '"+text+"' wurde kein Benutzer gefunden!");
                    } else{
                        ergebnisse.setVisibility(View.VISIBLE);
                        ergebnisse.setText(""+infos.length+" Ergebnis"+(infos.length == 1 ? "" : "se")+":");
                        content.removeAllViews();
                        MyList<Internet.UserInfo> liste = new MyList<Internet.UserInfo>(infos){
                            public View view(final Internet.UserInfo i){
                                View r = Screen.inflate(R.layout.user);
                                r.setOnClickListener(new View.OnClickListener(){
                                    public void onClick(View v){
                                        ScreenProfil.show(i);
                                    }
                                });
                                BildManager.get(i.id, r.findViewById(R.id.angeboteitem_bild), ScreenSearch.this);
                                ((TextView) r.findViewById(R.id.angeboteitem_detail)).setText(i.klasse+" | "+i.schuleText);
                                ((TextView) r.findViewById(R.id.angebotitem_name)).setText(i.name);
                                return r;
                            }
                        };
                        content.addView(liste);
                    }
                }
                public void fail(){
                    set("Keine Internetverbindung!");
                }
            });
        }
    }

    public String getTitel(){
        return "Benutzer suchen";
    }
}
