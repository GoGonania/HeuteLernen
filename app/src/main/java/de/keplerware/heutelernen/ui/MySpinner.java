package de.keplerware.heutelernen.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.*;
import de.keplerware.heutelernen.*;

public class MySpinner extends Spinner{
	public MySpinner(Context c, AttributeSet attribs){
		super(c, attribs);
	}

    public MySpinner(String[] contents){
        super(Util.screen);
        fill(contents);
    }

	public void fill(String[] contents){
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(Util.screen, android.R.layout.simple_list_item_1, android.R.id.text1, contents);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		setAdapter(adapter);
	}
}
