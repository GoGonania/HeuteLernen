package de.keplerware.heutelernen.manager;

import java.util.ArrayList;

import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.Internet.InfoListener;
import de.keplerware.heutelernen.Internet.UserInfo;

public class ProfilManager{
	private static ArrayList<UserInfo> infos = new ArrayList<UserInfo>();
	
	public static void get(int id, final InfoListener l){
		for(int i = 0; i < infos.size(); i++){
			if(infos.get(i).id == id){
				l.ok(infos.get(i));
				return;
			}
		}
		Internet.info(id, new InfoListener(){
			public void ok(UserInfo info){
				infos.add(info);
				l.ok(info);
			}
			
			public void fail() {
				l.fail();
			}
		});
	}
}
