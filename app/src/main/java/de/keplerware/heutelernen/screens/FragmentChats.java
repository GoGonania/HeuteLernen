package de.keplerware.heutelernen.screens;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.keplerware.heutelernen.MyFragment;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.manager.BildManager;
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
            main.addView(new MyText("Du hast gerade keine aktive Chats\n\nUm zu schreiben, gehe auf das Profil eines Benutzers und klicke oben rechts auf Chat"){{setGravity(Gravity.CENTER);}});
        } else{
            MyList<Chat> liste = new MyList<Chat>(NachrichtenManager.get()){
                public View view(final Chat t){
                    View r = Screen.inflate(R.layout.chat_item);
                    r.setOnClickListener(new View.OnClickListener(){
                        public void onClick(View v){
                            ScreenChat.show(t.info);
                        }
                    });
                    r.setLongClickable(true);
                    r.setOnLongClickListener(new OnLongClickListener(){
                        public boolean onLongClick(View v){
                            t.deleteConfirm(false);
                            return true;
                        }
                    });
                    int u = t.unread();
                    NachrichtenManager.Message last = t.last();
                    BildManager.get(t.partner, r.findViewById(R.id.chatitem_bild), getActivity());
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

    public View create(){
        View v = Screen.inflate(R.layout.chats);
        main = (LinearLayout) v.findViewById(R.id.chats_main);
        return v;
    }
}
