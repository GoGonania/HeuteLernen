package de.keplerware.heutelernen;

import android.view.View;
import de.keplerware.heutelernen.Dialog.ConfirmListener;
import android.view.*;

public abstract class Screen{
	public Screen parent;
	
	public Screen(){
		parent = getParentScreen();
	}
	
	protected abstract Screen getParentScreen();
	protected abstract int getLayout();
	public abstract String getTitle();
	public abstract void show();
	
	public void onBack(){
		if(parent != null){
			Util.setScreen(parent);
		} else{
			Dialog.confirm("Willst du die App schließen?", new ConfirmListener() {
				public void ok(){
					System.exit(0);
				}
			});
		}
	}
	
	public boolean event(int t, Object... d){return false;}
	public void menu(Menu m){}
	
	public View find(int i){return MainActivity.a.findViewById(i);}
}
