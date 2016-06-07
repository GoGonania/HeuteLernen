package de.keplerware.heutelernen.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.*;
import de.keplerware.heutelernen.*;

public class MySpinner extends Spinner{
	public MySpinner(Context c, AttributeSet attribs){
		super(c, attribs);
	}

    public MySpinner(int contents){
        super(Util.screen);
        fill(contents);
    }

	public void fill(int contents){
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Util.screen, contents, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		setAdapter(adapter);
	}
}
