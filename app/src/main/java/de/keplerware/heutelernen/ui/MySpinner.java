package de.keplerware.heutelernen.ui;

import android.widget.*;
import de.keplerware.heutelernen.*;

public class MySpinner extends Spinner{
	public MySpinner(int array){
		super(MainActivity.a);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.a, array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		setAdapter(adapter);
	}
}
