package de.keplerware.heutelernen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import de.keplerware.heutelernen.Internet.InfoListener;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.manager.ProfilManager;
import de.keplerware.heutelernen.screens.ScreenChat;
import de.keplerware.heutelernen.screens.ScreenLogin;
import de.keplerware.heutelernen.screens.ScreenMain;

public class MainActivity extends AppCompatActivity {
	public static MainActivity a;
	public static ActionBar bar;
	public static boolean pause = true;
	
	public static int aktivChat = -1;
	public static int aktivID = -1;
	
	public void onBackPressed(){Util.onBack();}
	
	protected void onNewIntent(Intent i){
		int c = i.getIntExtra("chat", -1);
		int id = i.getIntExtra("id", -1);
		if(c >= 0){
			if(Sitzung.info != null && Sitzung.info.id == id){
				ProfilManager.get(c, new InfoListener(){
					public void ok(UserInfo info){
						ScreenChat.show(info, false);
					}
					
					public void fail(){Util.setScreen(new ScreenLogin());}
				});
			} else{
				Util.setScreen(new ScreenLogin());
				aktivID = id;
				aktivChat = c;
			}
		} else{
			Util.setScreen(Sitzung.info == null?new ScreenLogin():new ScreenMain());
		}
	}

	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		a = this;
		Util.init(a);
		bar = getSupportActionBar();
		onNewIntent(getIntent());
	}

	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		Util.screen.menu(menu);
		return true;
	}
	
	protected void onPause(){
		super.onPause();
		pause = true;
	}
	
	protected void onResume(){
		super.onResume();
		pause = false;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
			case android.R.id.home:
				Util.onBack();
			break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
