package de.keplerware.heutelernen.ui;

import android.widget.TextView;

import de.keplerware.heutelernen.Util;

public class MyText extends TextView{
	public MyText(String t){
		super(Util.screen);
		setText(t);
	}
}
