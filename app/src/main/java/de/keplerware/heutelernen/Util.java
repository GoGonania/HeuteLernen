package de.keplerware.heutelernen;
import java.io.InputStream;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class Util{
	private static final String host = "http://www.heutelernen.de/";
	private static Context c;
	private static LayoutInflater in;
	private static WakeLock wakelock;
	private static InputMethodManager input;
	private static Toast t;
	private static Intent serviceIntent;
    private static TabLayout tabs;
	
	public static String fileDir;
	public static String appname;
	public static Screen screen;
	
	public static interface Listener{
		public void ok(String data);
		public void fail(Exception e);
	}
	
	public static void init(Context c){
		if(c instanceof MainActivity){
			in = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			input = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);

            tabs = new TabLayout(c);
            tabs.setsty
            tabs.addTab(tabs.newTab().setText("Chat"));
            tabs.addTab(tabs.newTab().setText("Profil"));
            tabs.addTab(tabs.newTab().setText("Start"));
		}
		
		if(Util.c == null){
			System.out.println("UTIL: init");
			Util.c = c;
			
			appname = c.getResources().getString(R.string.app_name);
	        wakelock = ((PowerManager) c.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
	        fileDir = c.getFilesDir().getAbsolutePath();
	        
			Save.init(c);
			
			serviceIntent = new Intent(c, MyService.class);
		}
	}
	
	public static void startService(){
		c.startService(serviceIntent);
	}
	
	public static void stopService(){
		c.stopService(serviceIntent);
	}
	
	public static void sleep(long t){
		try{Thread.sleep(t);}catch(InterruptedException e){}
	}
	
	public static void wakeLock(boolean a){
		if(a){
			if(!wakelock.isHeld()) wakelock.acquire();
		} else{
			if(wakelock.isHeld()) wakelock.release();
		}
	}
	
	public static void onBack(){
		screen.onBack();
	}
	
	public static void setScreen(Screen s){
		hideKeyboard();
		screen = s;
		MainActivity.bar.setSubtitle(null);
		ViewGroup v = inflate(s.getLayout());
		v.addView(tabs, 0);
		MainActivity.a.setContentView(v);
		MainActivity.bar.setTitle(s.getTitle());
		MainActivity.bar.setDisplayHomeAsUpEnabled(s.parent != null);
		s.show();
		MainActivity.a.invalidateOptionsMenu();
	}
	
	public static void hideKeyboard(){
		View v = MainActivity.a.getCurrentFocus();
		if(v == null) return;
		input.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	public static void toast(final String text){
		if(MainActivity.pause) return;
		Toast tt = Toast.makeText(c, text, Toast.LENGTH_SHORT);
		if(t != null ) t.cancel();
		tt.show();
		t = tt;
	}
	
	public static void run(Runnable r){
		if(!MainActivity.pause){
			MainActivity.a.runOnUiThread(r);
		} else{
			r.run();
		}
	}
	
	public static void refreshScreen(){
		try {
			Util.setScreen(screen.getClass().newInstance());
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static ViewGroup inflate(int id){return (ViewGroup) in.inflate(id, null);}
	
	public static boolean event(int type, Object... d){
		if(screen == null) return false;
		return screen.event(type, d);
	}
	
	public static void internet(final String name, final String p, final Listener l){
		new Thread(new Runnable(){
				public void run() {
					try{
						URL url = new URL(""+host+""+name+".php?"+p+"");
						InputStream in = url.openStream();
						String s = "";
						int b = 0;
						while((b = in.read()) != -1){
							s += (char) b;
						}
						l.ok(s);
					}catch (final Exception e){
						l.fail(e);
					}
				}}).start();
	}
	
	public static Runnable progress(String text){
		if(text == null || MainActivity.a == null) return null;
		final ProgressDialog d = new ProgressDialog(MainActivity.a);
		d.setMessage(text);
		d.setTitle(null);
		d.setCancelable(false);
		d.setCanceledOnTouchOutside(false);
		d.show();
		return new Runnable(){
			public void run(){
				d.dismiss();
			}
		};
	}
}
