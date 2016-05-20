package de.keplerware.heutelernen.screens;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.keplerware.heutelernen.Event;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.NachrichtenManager;
import de.keplerware.heutelernen.manager.NachrichtenManager.Chat;
import de.keplerware.heutelernen.manager.NachrichtenManager.Message;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MyText;

public class ScreenChats extends Screen{
	public ScreenChats(){
		super(0);
	}

	protected Screen getParentScreen(){
		return new ScreenMain();
	}

	protected int getLayout(){
		return R.layout.chats;
	}

	public String getTitle(){
		return null;
	}
	
	public boolean event(int t, Object... d){
		if(t == Event.MESSAGE) Util.refreshScreen();
		return false;
	}

	public void show(){
		LinearLayout main = (LinearLayout) find(R.id.chats_main);
		Chat[] c = NachrichtenManager.get();
		if(c.length == 0){
			main.addView(new MyText("Du hast gerade keine aktive Chats"){{setGravity(Gravity.CENTER);}});
		} else{
			MyList<Chat> liste = new MyList<Chat>(NachrichtenManager.get()){
				public View view(final Chat t){
					View r = Util.inflate(R.layout.chat_item);
					r.setOnClickListener(new View.OnClickListener(){
						public void onClick(View v){
							ScreenChat.show(t.info, false);
						}
					});
					r.setLongClickable(true);
					r.setOnLongClickListener(new OnLongClickListener(){
						public boolean onLongClick(View v){
							t.deleteConfirm();
							return true;
						}
					});
					int u = t.unread();
					Message last = t.last();
					((TextView) r.findViewById(R.id.chatitem_name)).setText(t.info.name);
					((TextView) r.findViewById(R.id.chatitem_detail)).setText((last.owner?"Du: ":"")+""+last.text);
					
					TextView counter = (TextView) r.findViewById(R.id.chatitem_counter);
					
					if(u == 0){
						counter.setVisibility(View.INVISIBLE);
					} else{
						counter.setText(""+u);
					}
					
					return r;
				}
			};
			main.addView(liste);
		}
	}
}
