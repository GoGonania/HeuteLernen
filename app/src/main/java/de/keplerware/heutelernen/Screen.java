package de.keplerware.heutelernen;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import de.keplerware.heutelernen.Dialog.ConfirmListener;
import android.view.*;

public abstract class Screen extends Fragment {
	public Screen parent;
    private View root;
    public int tab;

    public Screen(){this(-1);}
	
	public Screen(int tab){
		parent = getParentScreen();
        this.tab = tab;
	}
	
	protected abstract Screen getParentScreen();
	protected abstract int getLayout();
	public abstract String getTitle();
	public abstract void show();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = inflater.inflate(getLayout(), container, false);
        show();
        return root;
    }

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
	
	public View find(int i){return root.findViewById(i);}
}
