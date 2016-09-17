package de.keplerware.heutelernen.screens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Save;
import de.keplerware.heutelernen.Util;

public class ScreenTutorial extends AppIntro {
    public static void show(){
        Util.screen.startActivity(new Intent(Util.screen, ScreenTutorial.class));
    }

    public static void showCheck(){
        if(Save.tutorial) return;
        Save.tutorial = true;
        Save.updateTutorial();
        show();
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Nachhilfelehrer suchen", "Finde Nachhilfelehrer die zu dir passen", R.drawable.tutorial1, ContextCompat.getColor(this, R.color.actionbar)));
        addSlide(AppIntroFragment.newInstance("Stelle dich vor", "Hilf interessierten Schülern, dich besser kennenzulernen", R.drawable.tutorial2, ContextCompat.getColor(this, R.color.actionbar)));
        addSlide(AppIntroFragment.newInstance("Sende Nachrichten", "Vereinbare Treffen mit deinem Nachhilfelehrer", R.drawable.tutorial3, ContextCompat.getColor(this, R.color.actionbar)));

        setBarColor(ContextCompat.getColor(this, R.color.actionbar));
        setNavBarColor(R.color.actionbar);
        setGoBackLock(true);
        setSeparatorColor(Color.WHITE);

        setFlowAnimation();

        showSkipButton(false);
        setDoneText("OK");

        setVibrate(true);
        setVibrateIntensity(30);
    }

    public void onDonePressed(Fragment currentFragment){
        finish();
    }
}
