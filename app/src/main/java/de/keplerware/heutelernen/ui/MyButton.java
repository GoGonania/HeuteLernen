package de.keplerware.heutelernen.ui;

import android.view.View;
import android.widget.Button;

import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Util;

public abstract class MyButton extends Button{
	public abstract void klick();
	
	public MyButton(String text, boolean green){
		super(Util.screen);
		setText(text);
		setBackgroundResource(green?R.drawable.button_green:R.drawable.button_gray);
		setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				klick();
			}
		});
	}
}
