package de.keplerware.heutelernen.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MyAdapter<T> extends BaseAdapter{
	private T[] items;
	
	public abstract View view(T item);
	
	public MyAdapter(T[] t){
		items = t;
	}
	
	public int getCount(){
		return items.length;
	}

	public Object getItem(int arg0){
		return items[arg0];
	}

	public long getItemId(int arg0){
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2){
		return view(items[arg0]);
	}
}