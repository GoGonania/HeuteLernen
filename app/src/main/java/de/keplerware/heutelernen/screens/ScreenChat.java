package de.keplerware.heutelernen.screens;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import de.keplerware.heutelernen.Event;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.Internet.Nachricht;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.MainActivity;
import de.keplerware.heutelernen.MyService;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.Util.Listener;
import de.keplerware.heutelernen.manager.NachrichtenManager;
import de.keplerware.heutelernen.manager.NachrichtenManager.Chat;
import de.keplerware.heutelernen.manager.NachrichtenManager.Message;
import de.keplerware.heutelernen.ui.MyText;

public class ScreenChat extends Screen{
	private static boolean profil;
	private static UserInfo info;
	
	private ScrollView scroller;
	private LinearLayout content;
	private EditText text;
	private Chat c;
	
	public static void show(UserInfo i, boolean p){
		info = i;
		profil = p;
		Util.setScreen(new ScreenChat());
	}
	
	protected Screen getParentScreen(){
		return profil?new ScreenProfil():new ScreenChats();
	}

	protected int getLayout(){
		return R.layout.chat;
	}

	public String getTitle(){
		return info.name;
	}
	
	public void send(){
		final String t = text.getEditableText().toString().trim();
		
		if(!t.isEmpty()){
			final View v = addContent(true, t);
			scroll();
			v.setAlpha(0.6F);
			Internet.nachricht(info, t, new Listener() {
				public void ok(String data){
					text.setText("");
					v.setAlpha(1);
				}
				
				public void fail(Exception e){
					((ViewGroup) v.getParent()).removeView(v);
				}
			});
		}
	}
	
	public void menu(Menu m){
		if(!c.ms.isEmpty()){
			m.add("Chat löschen").setIcon(R.drawable.delete).setOnMenuItemClickListener(new OnMenuItemClickListener(){
				public boolean onMenuItemClick(MenuItem item){
					c.deleteConfirm();
					return true;
				}
			}).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
	}
	
	public boolean event(int t, Object... d){
		if(t == Event.MESSAGE){
			c.read();
			Nachricht n = (Nachricht) d[0];
			addContent(false, n.text);
			scroll();
			return !MainActivity.pause;
		}
		if(t == Event.LAST){
			MyService.aktivID = info.id;
			if(((Integer) d[0]) == info.id){
				Object o = d[1];
				if(o == null){
					MainActivity.bar.setSubtitle(null);
				} else{
					String l = (String) o;
					MainActivity.bar.setSubtitle((l.length() == 1)?"online":"zuletzt online: "+(l.isEmpty()?"nie":l)); 
				}
			}
		}
		return false;
	}
	
	public View addContent(boolean i, final String text){
		LinearLayout parent = new LinearLayout(MainActivity.a);
		parent.setGravity(i?Gravity.END:Gravity.START);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		p.bottomMargin = p.topMargin = 5;
		MyText tv = new MyText(text);
		tv.setTextSize(16);
		tv.setTextColor(Color.BLACK);
		tv.setPadding(10, 4, 10, 4);
		tv.setLongClickable(true);
		tv.setOnLongClickListener(new OnLongClickListener(){
			public boolean onLongClick(View v){
				((ClipboardManager) MainActivity.a.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Nachricht von "+info.name+"", text));
				Util.toast("Nachricht wurde kopiert!");
				return true;
			}
		});
		tv.setBackgroundResource(i?R.drawable.chat_green:R.drawable.chat_gray);
		tv.setMaxWidth(500);
		tv.setLayoutParams(p);
		parent.addView(tv);
		content.addView(parent);
		return parent;
	}
	
	private void scroll(){
		scroller.post(new Runnable(){
			public void run(){
				scroller.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	public void show(){
		text = (EditText) find(R.id.chat_text);
		content = (LinearLayout) find(R.id.chat_content);
		scroller = (ScrollView) content.getParent();
		find(R.id.chat_senden).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v){
				send();
			}
		});
		c = NachrichtenManager.get(Sitzung.info.id, info.id);
		c.read();
		for(Message m : c.ms){
			addContent(m.owner, m.text);
		}
		Internet.last(info.id);
		scroll();
	}
}
