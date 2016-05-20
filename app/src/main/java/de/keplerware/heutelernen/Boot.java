package de.keplerware.heutelernen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Boot extends BroadcastReceiver{
	public void onReceive(Context c, Intent arg1){
		System.out.println("BOOT");
		MyService.login = true;
		Util.init(c);
		Util.startService();
	}
}
