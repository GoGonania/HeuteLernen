package de.keplerware.heutelernen;

import de.keplerware.heutelernen.Internet.LoginListener;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.Util.Listener;
import de.keplerware.heutelernen.manager.NachrichtenManager;
import de.keplerware.heutelernen.screens.ScreenLogin;

public class Sitzung{
	public static UserInfo info;
	
	public static void login(final String m, final String p, final boolean service, final LoginListener l){
		Internet.login(m, p, new LoginListener(){
			public void ok(final UserInfo info){
				if(service){
					setup(m, p, info, service, l);
				} else{
					NachrichtenManager.load(info.id, new Listener(){
						public void ok(String data){
							setup(m, p, info, service, l);
						}
						
						public void fail(Exception e){
							reset(service);
							l.fail(LoginError.Connection);
						}
					});
				}
			}
			
			public void fail(int e){
				reset(service);
				l.fail(e);
			}
		});
	}
	
	private static void setup(String m, String p, UserInfo info, boolean s, LoginListener l){
		Sitzung.info = info;
		MyService.id = info.id;
		Save.setData(m, p, info.id);
		if(!s) Util.startService();
		Util.wakeLock(true);
		l.ok(info);
	}
	
	private static void reset(boolean s){
		if(!s) Util.stopService();
		Util.wakeLock(false);
		info = null;
	}
	
	public static boolean rang(int level){
		return info.rang >= level;
	}
	
	public static void logout(){
		Util.toast("Du hast dich ausgeloggt!");
		Save.setData(info.mail, null, -1);
		reset(false);
		Util.setScreen(new ScreenLogin());
	}
}