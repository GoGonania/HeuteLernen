package de.keplerware.heutelernen.manager;

import android.os.Bundle;

import java.util.ArrayList;

import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.Internet.InfoListener;
import de.keplerware.heutelernen.Internet.UserInfo;

public class ProfilManager{
	private final static ArrayList<UserInfo> infos = new ArrayList<>();
	
	public static void get(int id, boolean dialog, final InfoListener l){
		for(int i = 0; i < infos.size(); i++){
			if(infos.get(i).id == id){
				l.ok(infos.get(i));
				return;
			}
		}
		Internet.info(id, dialog, new InfoListener(){
			public void ok(UserInfo info){
				infos.add(info);
				l.ok(info);
			}
			
			public void fail() {
				l.fail();
			}
		});
	}

	public static Bundle create(UserInfo info){
        Bundle b = new Bundle();
        int index = infos.indexOf(info);
        b.putInt("user", index);
        return b;
	}

    public static UserInfo get(Bundle b){
        return infos.get(b.getInt("user"));
    }
}
