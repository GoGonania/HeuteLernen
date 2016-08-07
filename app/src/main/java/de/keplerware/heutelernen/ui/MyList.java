package de.keplerware.heutelernen.ui;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Util;

public abstract class MyList<T> extends ListView{
	public abstract View view(T t);
	
	public MyList(T[] t){
		super(Util.screen);
		setDivider(ContextCompat.getDrawable(Util.screen, R.drawable.divider));
		setDividerHeight(1);
		setAdapter(new MyAdapter<T>(t) {
			public View view(T item) {
				return MyList.this.view(item);
			}
		});
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		setLayoutParams(p);
	}
}
