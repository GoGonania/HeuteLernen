package de.keplerware.heutelernen.ui;

import android.widget.*;
import de.keplerware.heutelernen.*;

public class MySpinner extends Spinner{
	public MySpinner(int array){
		super(Util.screen);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Util.screen, array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		setAdapter(adapter);
	}
}
