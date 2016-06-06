package de.keplerware.heutelernen;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.Toast;

public class Util{
	private static final String host = "http://www.heutelernen.de/app/";
	private static Context c;
	private static WakeLock wakelock;
	private static Toast t;
	private static Intent serviceIntent;
	
	public static String fileDir;
	public static String appname;
	public static Screen screen;
	public static String[] schulen;

	public interface Listener{
		void ok(String data);
		void fail(Exception e);
	}

	public static void init(Context c){
		if(Util.c == null){
			System.out.println("UTIL: init");
			Util.c = c;

			appname = c.getResources().getString(R.string.app_name);
			schulen = c.getResources().getStringArray(R.array.schulen);
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
	
	public static void wakeLock(boolean a){
		if(a){
			if(!wakelock.isHeld()) wakelock.acquire();
		} else{
			if(wakelock.isHeld()) wakelock.release();
		}
	}
	
	public static void toast(final String text){
		if(HeuteLernen.pause) return;
		Toast tt = Toast.makeText(c, text, Toast.LENGTH_SHORT);
		if(t != null ) t.cancel();
		tt.show();
		t = tt;
	}
	
	public static void run(Runnable r){
		if(screen != null){
			screen.runOnUiThread(r);
		} else{
			r.run();
		}
	}
	
	public static boolean event(int type, Object... d){
		return screen != null && screen.event(type, d);
	}

	public static void internet(final String name, final String p, final Listener l){
		new Thread(new Runnable(){
				public void run(){
					try{
						InputStream in = new URL(""+host+""+name+".php?"+p+"").openStream();

						String s = "";
						int b;
						while((b = in.read()) != -1){
							s += (char) b;
						}
						l.ok(s);
					}catch(final Exception e){
						l.fail(e);
					}
				}
		}).start();
	}
}
