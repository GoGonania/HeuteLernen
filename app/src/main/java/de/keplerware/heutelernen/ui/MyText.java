package de.keplerware.heutelernen.ui;

import android.widget.TextView;
import de.keplerware.heutelernen.MainActivity;

public class MyText extends TextView{
	public MyText(String t){
		super(MainActivity.a);
		setText(t);
	}
}
