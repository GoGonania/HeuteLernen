package de.keplerware.heutelernen;

import android.content.Context;
import android.content.SharedPreferences;

public class Save{
	private static SharedPreferences p;
	private static SharedPreferences.Editor pe;

	public static String mail;
	public static String passwort;
	public static int id;
	public static boolean tutorial;
	
	public static void init(Context c){
		if(p != null) return;
		p = c.getSharedPreferences("save", Context.MODE_PRIVATE);
		pe = p.edit();
		mail = p.getString("m", null);
		passwort = p.getString("p", null);
		id = p.getInt("i", -1);
		tutorial = p.getBoolean("t", false);
	}

	public static void updatePasswort(String p){
		setData(mail, p, id);
	}
    public static void updateTutorial(){pe.putBoolean("t", tutorial); pe.commit();}
	
	public static void setData(String mail, String passwort, int id){
		Save.mail = mail;
		Save.passwort = passwort;
		Save.id = id;
		pe.putString("m", mail);
		pe.putString("p", passwort);
		pe.putInt("i", id);
		pe.commit();
	}
}
