package de.keplerware.heutelernen.screens;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Starter;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.ui.MySpinner;

public class ScreenMain extends Screen{
	private MySpinner s;
	private LinearLayout l;

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
        ViewPager pager = (ViewPager) findViewById(R.id.app);
        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()){
            public Fragment getItem(int position){
                switch(position){
                    case 0:
                        return new FragmentChats();
                    case 1:
                        return new FragmentMain();
                    case 2:
                        return new FragmentProfil();
                }
                return null;
            }

            public CharSequence getPageTitle(int position){
                switch(position) {
                    case 0:
                        return "Chats";
                    case 1:
                        return "Startmenu";
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
        ((TabLayout) findViewById(R.id.tab_layout)).setupWithViewPager(pager);

        if(getIntent().hasExtra("tab")){
            int tab = getIntent().getIntExtra("tab", 0);
            pager.setCurrentItem(tab);
        } else{
            pager.setCurrentItem(1);
        }
	}

    public String getTitel(){
        return Util.appname;
    }
}
