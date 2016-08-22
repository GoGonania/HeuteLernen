package de.keplerware.heutelernen.manager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.keplerware.heutelernen.Client;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.io.Datei;

public class BildManager{
    public static final Datei root = Datei.root("bilder");

    private static Thread t;
    private static Client c;

    public interface Listener{
        void ok(Bitmap b);
        void notfound();
        void fail();
    }

    static class Cache{
        public Bitmap b;
        public int id;
    }

    static class Job{
        public Listener l;
        public int id;
    }

    private static final ArrayList<Cache> cache = new ArrayList<>();
    private static final ArrayList<Job> jobs = new ArrayList<>();

    private static void set(final Bitmap b, final ImageView v, Activity a){
        a.runOnUiThread(new Runnable(){
            public void run() {
                if(b == null){
                    v.setImageResource(R.drawable.portrait);
                } else{
                    v.setImageBitmap(b);
                }
            }
        });
    }

    public static void get(final int id, boolean uc, final View v, final Activity a){
        get(id, uc, new Listener(){
            public void ok(Bitmap b){
                set(b, ((ImageView) v), a);
            }

            public void notfound(){
                set(null, ((ImageView) v), a);
            }
            public void fail(){}
        });
    }

    public static void get(int id, View v, Activity a){
        get(id, true, v, a);
    }

    private static void get(final int id, boolean uc, final Listener l){
        if(uc){
            for(int i = 0; i < cache.size(); i++){
                Cache c = cache.get(i);
                if(c.id == id){
                    Bitmap b = c.b;
                    if(b == null){
                        l.notfound();
                    } else{
                        l.ok(b);
                    }
                    return;
                }
            }
        }

        loadBild(id, new Listener(){
            public void ok(Bitmap b){
                cache(id, b);
                l.ok(b);
            }

            public void notfound(){
                cache(id, null);
                l.notfound();
            }

            public void fail(){
                c = null;
                l.fail();
            }
        });
    }

    private static void cache(int id, Bitmap b){
        for(int i = 0; i < cache.size(); i++){
            if(cache.get(i).id == id){
                cache.get(i).b = b;
                return;
            }
        }
        Cache c = new Cache();
        c.id = id;
        c.b = b;
        cache.add(0, c);
    }

    private static void run(Job j){
        try{
            if(c == null) {
                try{
                    c = new Client();
                }catch (IOException e){
                    j.l.fail();
                    return;
                }
            }
            File f = c.download(j.id);
            if(f == null) {
                j.l.notfound();
                return;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
            j.l.ok(bitmap);
        }catch(IOException e){
            e.printStackTrace();
            j.l.fail();
        }
    }

    private static void loadBild(int id, Listener l){
        final Job j = new Job();
        j.id = id;
        j.l = l;
        jobs.add(j);
        if(t == null){
            t = new Thread(new Runnable() {
                public void run() {
                    while(!jobs.isEmpty()){
                        Job j = jobs.remove(0);
                        BildManager.run(j);
                    }
                    t = null;
                }
            });
            t.start();
        }
    }
}
