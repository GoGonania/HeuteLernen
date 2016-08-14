package de.keplerware.heutelernen.screens;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import de.keplerware.heutelernen.Internet;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Screen;
import de.keplerware.heutelernen.manager.BildManager;
import de.keplerware.heutelernen.ui.MyList;
import de.keplerware.heutelernen.ui.MyText;

public class ScreenSearch{
    private EditText input;


    private String c;

    public int getLayout(){
        return R.layout.search;
    }

    /*public void show(){
        content = (LinearLayout) findViewById(R.id.search_content);
        input = (EditText) findViewById(R.id.search_input);
        ergebnisse = (TextView) findViewById(R.id.search_ergebnisse);

        input.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    search();
                    return true;
                }
                return false;
            }
        });

        input.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2){}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2){
                search();
            }
            public void afterTextChanged(Editable editable){}
        });

        ergebnisse.setVisibility(View.GONE);
    }

    private void set(String text){
        content.removeAllViews();
        content.addView(new MyText(text));
    }*/



    public String getTitel(){
        return "Benutzer suchen";
    }
}
