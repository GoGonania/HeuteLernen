package de.keplerware.heutelernen.screens;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Dialog.ConfirmListener;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.Internet.Angebot;
import de.keplerware.heutelernen.Internet.AngebotListener;
import de.keplerware.heutelernen.MainActivity;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.Util.Listener;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MySpinner;

public class ScreenMain extends Screen{
	private MySpinner s;
	private LinearLayout l;
	private static Angebot[] angebote;

	public ScreenMain(){
		super(1);
	}
	
	public Screen getParentScreen(){
		return null;
	}

	public int getLayout(){
		return R.layout.main;
	}

	public String getTitle(){
		return null;
	}
	
	public void showAngebote(){
		if(angebote.length == 0){
			TextView v = new TextView(MainActivity.a);
			v.setText("Keine Ergebnisse gefunden!");
			l.addView(v);
		} else{
			MyList<Angebot> liste = new MyList<Internet.Angebot>(angebote){
				public View view(final Angebot a){
					View r = Util.inflate(R.layout.angebot_item);
					r.setOnClickListener(new View.OnClickListener(){
						public void onClick(View v){
							Util.setScreen(ScreenProfil.show(a.info));
						}
					});
					((TextView) r.findViewById(R.id.angeboteitem_detail)).setText(a.info.klasse);
					((TextView) r.findViewById(R.id.angebotitem_name)).setText(a.info.name);
					return r;
				}
			};
			l.addView(liste);
		}
	}

	public void show(){
		l = (LinearLayout) find(R.id.liste);
		((LinearLayout) find(R.id.parameter)).addView(s = new MySpinner(R.array.facher));
		
		if(angebote != null) showAngebote();
		
		find(R.id.create).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String f = s.getSelectedItem().toString();
				Internet.angebotAufgeben(f, Sitzung.info.klasseZahl, Sitzung.info.id, new Listener() {
					public void ok(String data){
						if(!data.isEmpty()) Util.toast("Angebot wurde aufgegeben!");
					}
					public void fail(Exception e){}
				});
			}
		});
		
		find(R.id.suchen).setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				String f = s.getSelectedItem().toString();
				l.removeAllViews();
				
				Internet.angebote(f, Sitzung.info.klasseZahl, new AngebotListener(){
					public void ok(Angebot[] as){
						angebote = as;
						showAngebote();
					}
					
					public void fail(){
						angebote = null;
					}
				});
			}
		});
	}
}
