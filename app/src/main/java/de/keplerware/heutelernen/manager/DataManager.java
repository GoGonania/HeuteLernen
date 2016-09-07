package de.keplerware.heutelernen.manager;

import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.Util;

public class DataManager{
    public static String[] schulen;
    public static String[] faecher;

    public static void load(final Util.Listener l){
        if(schulen != null) {
            l.ok(null);
            return;
        }
        final Runnable r = Dialog.progress("Lade Schulen & Fächer");
        Util.internet("daten", "", new Util.Listener(){
            public void ok(String data){
                String[] parts = data.split("\t\t");
                schulen = parts[0].split("\t");
                faecher = parts[1].substring(1).split("\t");
                Util.screen.runOnUiThread(new Runnable(){
                    public void run(){
                        r.run();
                        l.ok(null);
                    }
                });
            }

            public void fail(Exception e){
                r.run();
                l.fail(e);
            }
        });
    }

    public static String schule(int id){
        if(id >= schulen.length) return "Unbekannte Schule";
        return schulen[id];
    }

    public static String fach(int id){
        if(id >= faecher.length) return "Unbekanntes Fach";
        return faecher[id];
    }
}
