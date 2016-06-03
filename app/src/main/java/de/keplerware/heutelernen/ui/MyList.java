package de.keplerware.heutelernen.ui;

import android.view.View;
import android.widget.ListView;

import de.keplerware.heutelernen.Util;

public abstract class MyList<T> extends ListView{
	public abstract View view(T t);
	
	public MyList(T[] t){
		super(Util.screen);
		setAdapter(new MyAdapter<T>(t) {
			public View view(T item) {
				return MyList.this.view(item);
			}
		});
	}
}
