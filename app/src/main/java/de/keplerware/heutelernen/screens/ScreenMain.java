package de.keplerware.heutelernen.screens;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Event;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Starter;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.NachrichtenManager;

public class ScreenMain extends Screen{
    private FragmentChats chats;
    private FragmentProfil profil;
    private TabLayout tabs;

    public static Starter show(int tab){
        Starter s = new Starter(ScreenMain.class);
        s.intent.putExtra("tab", tab);
        return s;
    }

    public int getLayout(){
        return R.layout.app;
    }

    public void onBackPressed(){
        Dialog.confirmClose();
    }

    public void show(){
        chats = new FragmentChats();
        profil = new FragmentProfil();
        ViewPager pager = (ViewPager) findViewById(R.id.app);
        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()){
            public Fragment getItem(int position){
                switch(position){
                    case 0:
                        return chats;
                    case 1:
                        return new FragmentMain();
                    case 2:
                        return profil;
                }
                return null;
            }

            public CharSequence getPageTitle(int position){
                switch(position) {
                    case 0:
                        return "Chats";
                    case 1:
                        return "Nachhilfe";
                    case 2:
                        return Sitzung.info.vname;
                }
                return null;
            }

            public int getCount(){
                return 3;
            }
        };

        pager.setAdapter(adapter);
        tabs = ((TabLayout) findViewById(R.id.tab_layout));
        tabs.setupWithViewPager(pager);

        if(getIntent().hasExtra("tab")){
            int tab = getIntent().getIntExtra("tab", 0);
            pager.setCurrentItem(tab);
        } else{
            pager.setCurrentItem(NachrichtenManager.unread() > 0 ? 0 : 1);
        }
	}

    public boolean event(int t, Object... d){
        if(t == Event.MESSAGE){
            if(!chats.paused) chats.resume();
            resume();
        }
        return false;
    }

    public void resume(){
        int u = NachrichtenManager.unread();
        if(u == 0){
            tabs.getTabAt(0).setText("Chats");
        } else {
            tabs.getTabAt(0).setText("Chats ("+u+")");
        }
    }

    protected void onResume(){
        super.onResume();
        resume();
    }

    public String getTitel(){
        return Util.appname;
    }

    public void menu(Menu m){
        m.add("Ausloggen").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem p1){
                Dialog.confirm("Willst du dich wirklich ausloggen?", new Dialog.ConfirmListener() {
                    public void ok() {
                        Sitzung.logout();
                    }
                });
                return true;
            }
        });
        m.add("Nachhilfefach hinzufügen").setIcon(R.drawable.add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem menuItem){
                Dialog.fachSelect("Nachhilfe geben in...", new Dialog.FachListener(){
                    public void ok(final String fach) {
                        Internet.angebotAufgeben(fach, Sitzung.info.klasseZahl, Sitzung.info.id, new Util.Listener() {
                            public void ok(String data){
                                if(!data.isEmpty()) {
                                    Util.toast("Du hast nun '"+fach+"' als Nachhilfefach!");
                                    profil.update();
                                } else{
                                    Util.toast("Du hast bereits '"+fach+"' als Nachhilfefach!");
                                }
                            }
                            public void fail(Exception e){}
                        });
                    }
                });
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }
}
