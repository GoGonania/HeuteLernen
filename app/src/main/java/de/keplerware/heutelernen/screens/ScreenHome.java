package de.keplerware.heutelernen.screens;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Event;
import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Rang;
import de.keplerware.heutelernen.Save;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Starter;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.manager.BildManager;
import de.keplerware.heutelernen.manager.DataManager;
import de.keplerware.heutelernen.manager.NachrichtenManager;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MyText;

public class ScreenHome extends Screen{
    public static FragmentChats chats;
    private FragmentProfil profil;
    private TabLayout tabs;
    private ViewPager pager;

    private LinearLayout container;
    private LinearLayout content;
    private TextView ergebnisse;

    public static Starter show(int tab){
        Starter s = new Starter(ScreenHome.class);
        s.intent.putExtra("tab", tab);
        return s;
    }

    public int getLayout(){
        return R.layout.home;
    }

    public void onBackPressed(){
        Dialog.confirmClose();
    }

    public void show(){
        container = (LinearLayout) findViewById(R.id.home_search);
        content = (LinearLayout) findViewById(R.id.search_content);
        ergebnisse = (TextView) findViewById(R.id.search_ergebnisse);
        chats = new FragmentChats();
        profil = new FragmentProfil();
        pager = (ViewPager) findViewById(R.id.app);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()){
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
            item(getIntent().getIntExtra("tab", 0));
        } else{
            item(1);
        }

        Util.checkUpdate();
	}

    public void item(int tab){
        pager.setCurrentItem(tab);
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
        if(Sitzung.rang(Rang.MODERATOR)){
            m.add("Statistik").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
                public boolean onMenuItemClick(MenuItem p1){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("http://www.heutelernen.de/app/statistik.php"));
                    startActivity(i);
                    return true;
                }
            });
        }
        m.add("Passwort ändern").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem p1){
                Dialog.prompt("Passwort ändern", "", new Dialog.PromptListener(){
                    public void ok(String text){
                        final String t = text.trim();
                        if(t.length() >= 6){
                            Internet.passwort(t, new Util.Listener() {
                                public void ok(String data){
                                    Util.toast("Passwort wurde geändert!");
                                    Save.updatePasswort(t);
                                }

                                public void fail(Exception e){}
                            });
                        } else{
                            Util.toast("Dein neues Passwort muss mindestens 6 Zeichen lang sein!");
                        }
                    }
                });
                return true;
            }
        });
        m.add("Tutorial").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem p1){
                ScreenTutorial.show();
                return true;
            }
        });
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
        final SearchView search = new SearchView(this);
        search.setQueryHint("Benutzer suchen");
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            public boolean onQueryTextSubmit(String query){
                return false;
            }

            public boolean onQueryTextChange(String newText){
                search(newText.trim());
                return false;
            }
        });
        MenuItem i = m.add("Benutzer suchen");
        i.setIcon(R.drawable.search).setActionView(search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        MenuItemCompat.setOnActionExpandListener(i, new MenuItemCompat.OnActionExpandListener() {
            public boolean onMenuItemActionExpand(MenuItem item){return true;}
            public boolean onMenuItemActionCollapse(MenuItem item){v(false); return true;}
        });

        m.add("Nachhilfefach hinzufügen").setIcon(R.drawable.add).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem menuItem){
                Dialog.fachSelect("Nachhilfe geben in...", new Dialog.FachListener(){
                    public void ok(final int fach){
                        Internet.angebotAufgeben(fach, new Util.Listener() {
                            public void ok(String data){
                                if(!data.isEmpty()) {
                                    Util.toast("Du hast nun '"+ DataManager.fach(fach)+"' als Nachhilfefach!");
                                    profil.update();
                                    item(2);
                                } else{
                                    Util.toast("Du hast bereits '"+DataManager.fach(fach)+"' als Nachhilfefach!");
                                }
                            }
                            public void fail(Exception e){}
                        });
                    }
                });
                return true;
            }
        }).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        v(false);
    }

    private void set(String text){
        content.removeAllViews();
        content.addView(new MyText(text));
    }

    private void v(boolean s){
        container.setVisibility(s ? View.VISIBLE : View.GONE);
        pager.setVisibility(!s ? View.VISIBLE : View.GONE);
        tabs.setVisibility(!s ? View.VISIBLE : View.GONE);
    }

    private String c;
    public void search(final String text){
        if(!text.isEmpty()){
            c = text;
            ergebnisse.setVisibility(View.GONE);
            Internet.benutzerSuchen(text, false, new Internet.SuchListener(){
                public void ok(Internet.UserInfo[] infos){
                    if(!c.equals(text)) return;
                    v(true);
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
                                BildManager.get(i.id, r.findViewById(R.id.angeboteitem_bild), ScreenHome.this);
                                ((TextView) r.findViewById(R.id.angeboteitem_detail)).setText(i.schulInfo);
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
        } else{
            v(false);
        }
    }
}
