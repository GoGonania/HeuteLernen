package de.keplerware.heutelernen;

import java.util.ArrayList;

import de.keplerware.heutelernen.manager.ProfilManager;

public class InfoPool{
    public interface Listener{
        void ok();
        void add(Internet.UserInfo info, int id);
        void fail();
    }
    private ArrayList<Integer> ids;
    private int progress;
    private boolean failed;

    public InfoPool(int size){
        ids = new ArrayList<>(size);
        progress = size;
    }

    public void add(int id){ids.add(id);}
    public void add(String id){add(Integer.parseInt(id));}

    public void start(final Listener li, boolean dialog){
        final Runnable d = Dialog.progress(dialog ? "Lade Daten..." : null);
        for(int i = 0; i < ids.size(); i++){
            final int id = i;
            ProfilManager.get(ids.get(i), false, new Internet.InfoListener(){
                public void ok(Internet.UserInfo info){
                    if(failed) return;
                    progress--;
                    li.add(info, id);
                    if(progress == 0){
                        if(d != null) d.run();
                        li.ok();
                    }
                }

                public void fail(){
                    if(failed) return;
                    failed = true;
                    if(d != null) d.run();
                    li.fail();
                }
            });
        }
    }
}
