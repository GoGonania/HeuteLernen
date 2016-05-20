package de.keplerware.heutelernen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Dialog{
	public static interface ConfirmListener{
		public void ok();
	}
	
	public static void confirm(String text, ConfirmListener li){
		confirm(-1, text, li);
	}
	
	public static void confirm(int icon, String text, final ConfirmListener l){
		AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.a);
		b.setTitle(Util.appname);
		b.setMessage(text);
		b.setIcon(icon == -1 ? R.drawable.help : icon);
		b.setPositiveButton("Ja", new OnClickListener(){
			public void onClick(DialogInterface dialog, int which){
				l.ok();
			}
		});
		b.setNegativeButton("Nein", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		b.create().show();
	}
	
	public static void alert(String titel, String text){
		AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.a);
		b.setTitle(titel);
		b.setMessage(text);
		b.setIcon(R.drawable.logo);
		b.setPositiveButton("OK", null);
		b.create().show();
	}
}
