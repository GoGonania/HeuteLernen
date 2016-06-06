package de.keplerware.heutelernen.screens;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.keplerware.heutelernen.MyFragment;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.manager.NachrichtenManager;
import de.keplerware.heutelernen.manager.NachrichtenManager.Chat;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MyText;

public class FragmentChats extends MyFragment {
    private LinearLayout main;

    public void resume(){
        if(main == null) return;
        main.removeAllViews();
        Chat[] c = NachrichtenManager.get();
        if(c.length == 0){
            main.addView(new MyText("Du hast gerade keine aktive Chats"){{setGravity(Gravity.CENTER);}});
        } else{
            MyList<Chat> liste = new MyList<Chat>(NachrichtenManager.get()){
                public View view(final Chat t){
                    View r = Screen.inflater.inflate(R.layout.chat_item, null);
                    r.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            ScreenChat.show(t.info);
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
                    NachrichtenManager.Message last = t.last();
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

    public void onResume(){
        super.onResume();
        resume();
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.chats, null);
        main = (LinearLayout) v.findViewById(R.id.chats_main);
        return v;
    }
}
