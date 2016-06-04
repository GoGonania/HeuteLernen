package de.keplerware.heutelernen;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import de.keplerware.heutelernen.Internet.InfoListener;
import de.keplerware.heutelernen.Internet.LoginListener;
import de.keplerware.heutelernen.Internet.Nachricht;
import de.keplerware.heutelernen.Internet.NachrichtenListener;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.manager.NachrichtenManager;
import de.keplerware.heutelernen.manager.NachrichtenManager.Chat;
import de.keplerware.heutelernen.manager.ProfilManager;

public class MyService extends Service{
	private static final long loginTimeout = 2000;
	private static final long checkTimeout = 1000;
	private static final long checkTimeoutNI = 5000;
	
	public static boolean login;
	public static boolean running;
	public static int id = -1;
	public static int aktivID = -1;
	public static NotificationManager manager;
	
	private static Bitmap logo;
	private static boolean internet;
	private static Context c;
	private static String m;
	private static String p;
	private static Handler h;
	
	public IBinder onBind(Intent intent){return null;}
	public int onStartCommand(Intent intent, int flags, int startId){return Service.START_STICKY;}
	public void onDestroy(){running = false; System.out.println("SERVICE: destroy");}
	
	public void onCreate(){
		Util.init(this);
		
		c = this;
		running = true;
		
		if(manager == null){
			manager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
			logo = BitmapFactory.decodeResource(c.getResources(), R.drawable.logo_notif);
		}
		
		System.out.println("SERVICE: create boot="+login+"");
		
		if(login){
			System.out.println("SERVICE: try to login after boot");
			login = false;
			m = Save.mail;
			p = Save.passwort;
			if(m == null || p == null){
				System.out.println("SERVICE: account-daten nicht vorhanden");
				stopSelf();
			} else{
				h = new Handler(){
					public void handleMessage(Message msg){
						login();
					}
				};
				login();
			}
		} else{
			checkLoop();
		}
	}
	
	private void checkLoop(){
		new Handler(Looper.getMainLooper()){
			public void handleMessage(Message msg){
				if(!running) return;
				check();
				sendEmptyMessageDelayed(0, internet?checkTimeout:checkTimeoutNI);
			}
		}.sendEmptyMessage(0);
	}
	
	private void login(){
		System.out.println("SERVICE: login");
		
		Sitzung.login(m, p, true, new LoginListener(){
			public void ok(UserInfo info){
				checkLoop();
			}

			public void fail(int e){
				System.out.println("SERVICE: login fail ("+c+")");

				if(e != LoginError.Passwort){
					System.out.println("SERVICE: try to login in "+loginTimeout+" ms");
					h.sendEmptyMessageDelayed(0, loginTimeout);
				} else{
					stopSelf();
				}
			}
		});
	}
	
	private static int nid;
	private static Nachricht[] nachrichten;
	
	private static void work(){
		final Nachricht n = nachrichten[nid];
		if(n.id == -1){
			nachricht(n);
			workF();
		} else{
			ProfilManager.get(n.id, new InfoListener(){
				public void ok(UserInfo info){
					n.info = info;
					nachricht(n);
					workF();
				}

				public void fail(){
					workF();
				}
			});
		}
	}
	
	private static void workF(){
		nid++;
		if(nid < nachrichten.length) work();
	}
	
	private static void nachricht(Nachricht n){
        switch (n.typ){
            case "chat":
                Chat chat = NachrichtenManager.add(id, n.id, false, n.text, n.info);

                if(!Util.event(Event.MESSAGE, n)){
                    int unread = chat.unread();

                    NotificationCompat.Builder b = new NotificationCompat.Builder(c);
                    b.setSmallIcon(R.drawable.logo_form);
                    b.setLargeIcon(logo);
                    b.setContentTitle(n.info.name);
                    b.setDefaults(NotificationCompat.DEFAULT_ALL);
                    b.setAutoCancel(true);
                    b.setOnlyAlertOnce(true);
                    b.setPriority(NotificationCompat.PRIORITY_HIGH);

                    if(unread == 1){
                        b.setContentText(n.text);
                    } else{
                        String t = unread+" neue Nachrichten";
                        b.setContentText(t);

                        String aus = "";
                        int f = 0;

                        for(int i = 0; f < unread; i++){
                            NachrichtenManager.Message m = chat.ms.get(i);

                            if(!m.owner){
                                f++;
                                aus += "\n"+m.text+"";
                            }
                        }

                        b.setStyle(new NotificationCompat.BigTextStyle()
                                .setSummaryText(t)
                                .bigText(aus.substring(1))
                        );
                    }

                    Intent i = new Intent(c, NotificationClick.class);
                    i.putExtras(ProfilManager.create(n.info));
                    PendingIntent pi = PendingIntent.getBroadcast(c, n.id, i, PendingIntent.FLAG_CANCEL_CURRENT);
                    b.setContentIntent(pi);

                    manager.notify(n.id, b.build());
                }
                break;
            case "system":
                NotificationCompat.Builder b = new NotificationCompat.Builder(c);
                b.setSmallIcon(R.drawable.logo_form);
                b.setLargeIcon(logo);
                b.setContentTitle(Util.appname);
                b.setContentText(n.text);
                b.setDefaults(NotificationCompat.DEFAULT_ALL);
                b.setAutoCancel(true);
                b.setOnlyAlertOnce(true);
                b.setPriority(NotificationCompat.PRIORITY_HIGH);
                manager.notify(Integer.MAX_VALUE, b.build());
                break;
        }
	}
	
	private static void check(){
		if(id == -1){
			System.out.println("SERVICE: get id from save");
			id = Save.id;
		}
		if(aktivID != -1){
			Internet.last(aktivID);
			aktivID = -1;
		} 
		Internet.nachrichten(id, new NachrichtenListener(){
			public void ok(Nachricht[] ns){
				internet = true;
				if(ns == null || ns.length == 0) return;
				nid = 0;
				nachrichten = ns;
				work();
			}
			public void fail(){
				internet = false;
			}
		});
	}
}
