package de.keplerware.heutelernen;

import android.content.Context;
import android.content.SharedPreferences;

public class Save{
	private static SharedPreferences p;
	private static SharedPreferences.Editor pe;
	
	public static void init(Context c){
		if(p != null) return;
		p = c.getSharedPreferences("save", Context.MODE_PRIVATE);
		pe = p.edit();
	}
	
	public static void setData(String mail, String passwort, int id){
		pe.putString("m", mail);
		pe.putString("p", passwort);
		pe.putInt("i", id);
		pe.commit();
	}
	
	public static String mail(){return p.getString("m", null);}
	public static String passwort(){return p.getString("p", null);}
	public static int id(){return p.getInt("i", -1);}
}
