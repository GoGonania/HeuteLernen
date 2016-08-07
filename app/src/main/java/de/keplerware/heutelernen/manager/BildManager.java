package de.keplerware.heutelernen.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.IOException;

import de.keplerware.heutelernen.Client;

public class BildManager{
    public static interface Listener{
        void ok(Bitmap b);
        void fail();
    }
    public static void loadBild(final int id, final Listener l){
        new Thread(new Runnable() {
            public void run() {
                try{
                    Client c = new Client();
                    File f = c.download(id);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
                    l.ok(bitmap);
                    c.close();
                }catch(IOException e){
                    e.printStackTrace();
                    l.fail();
                }
            }
        }).start();
    }
}
