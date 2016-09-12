package de.keplerware.heutelernen;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Util{
	private static final String host = "http://www.heutelernen.de/app/v0/";
	private static Context c;
	private static WakeLock wakelock;
	private static Toast t;
	private static Intent serviceIntent;
	
	public static String fileDir;
	public static String appname;
	public static Screen screen;

	public interface Listener{
		void ok(String data);
		void fail(Exception e);
	}

    public static boolean pause(){
        return Util.screen == null || !Util.screen.aktiv;
    }

	public static void init(Context c){
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
	
	public static void wakeLock(boolean a){
		if(a){
			if(!wakelock.isHeld()) wakelock.acquire();
		} else{
			if(wakelock.isHeld()) wakelock.release();
		}
	}
	
	public static void toast(final String text){
		if(pause()) return;
		Toast tt = Toast.makeText(c, text, Toast.LENGTH_SHORT);
		TextView v = (TextView) tt.getView().findViewById(android.R.id.message);
		if(v != null) v.setGravity(Gravity.CENTER);
		if(t != null ) t.cancel();
		tt.show();
		t = tt;
	}

	public static void toastUI(final String text){
		screen.runOnUiThread(new Runnable(){
			public void run(){
				toast(text);
			}
		});
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

    public static String hash(String text){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte[] digest = md.digest();
            return String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

	public static void internet(final String name, final String p, final Listener l){
		new Thread(new Runnable(){
				public void run(){
					try{
                        String url = ""+host+""+name+".php?"+p+"";
						InputStream in = new URL(url).openStream();
						String s = "";
						int b;
						while((b = in.read()) != -1){
							s += (char) b;
						}
                        in.close();
						l.ok(s);
					}catch(final Exception e){
                        e.printStackTrace();
						l.fail(e);
					}
				}
		}).start();
	}

    public static void setBackground(View v, Bitmap b){
        if (android.os.Build.VERSION.SDK_INT >= 16){
            v.setBackground(new BitmapDrawable(screen.getResources(), b));
        }
        else{
            v.setBackground(new BitmapDrawable(b));
        }
    }
}
