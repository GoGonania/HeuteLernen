package de.keplerware.heutelernen.manager;

import java.util.ArrayList;
import java.util.Collections;

import de.keplerware.heutelernen.Internet.InfoListener;
import de.keplerware.heutelernen.Internet.UserInfo;
import de.keplerware.heutelernen.Dialog;
import de.keplerware.heutelernen.MyService;
import de.keplerware.heutelernen.R;
import de.keplerware.heutelernen.Sitzung;
import de.keplerware.heutelernen.Util;
import de.keplerware.heutelernen.Dialog.ConfirmListener;
import de.keplerware.heutelernen.Util.Listener;
import de.keplerware.heutelernen.io.Datei;
import de.keplerware.heutelernen.screens.ScreenHome;

public class NachrichtenManager{
	public static class Chat implements Comparable<Chat>{
		public UserInfo info;
		public Datei dir;
		public Datei read;
		public long readTime;
		public int id;
		public int partner;
		public final ArrayList<Message> ms = new ArrayList<>();
		
		public void add(String g, boolean o, long time){
			Message m = new Message();
			m.text = g;
			m.owner = o;
			m.time = time;
			ms.add(m);
		}
		
		public Message last(){
			return ms.get(ms.size()-1);
		}
		
		public void read(){
			readTime = System.currentTimeMillis();
			read.write(readTime+"");
			MyService.manager.cancel(partner);
		}
		
		public int unread(){
			int u = 0;
			for(int i = 0; i < ms.size(); i++){
				if(!ms.get(i).owner && ms.get(i).time >= readTime) u++;
			}
			return u;
		}
		
		public void delete(){
			dir.delete();
			chats.remove(this);
		}
		
		public void deleteConfirm(){
			Dialog.confirm(R.drawable.delete, "Willst du den Chat mit "+info.name+" wirklich löschen?", new ConfirmListener(){
				public void ok() {
                    delete();
					ScreenHome.chats.resume();
				}
			});
		}

		public int compareTo(Chat another){
			int u = another.unread();
			int uu = unread();
			if(u == uu){
				long t = another.last().time;
				long tt = last().time;

				if(t == tt){
					return 0;
				} else{
					return t > tt ? 1 : -1;
				}
			} else{
				return u > uu ? 1 : -1;
			}
		}
	}
	
	public static class Message{
		public boolean owner;
		public String text;
		public long time;
	}
	
	private static final Datei main = Datei.root("chat");
	private static final ArrayList<Chat> chats = new ArrayList<>();
	private static final ArrayList<Integer> cache = new ArrayList<>();
	
	private static boolean f;
	private static int p;
	private static Listener l;

	public static int unread(){
		int u = 0;
		for(Chat c : chats){
			u += c.unread();
		}
		return u;
	}

    private static Runnable r;
	public static void load(int id, Util.Listener li){
		f = false;
		l = li;
		Datei[] n = main.createF(""+id).list();
		p = n.length;
        r = Dialog.progress("Chats werden geladen...");
		System.out.println("NachrichtenManager: "+p+" Chats gefunden");
		if(p == 0){
			loadC();
		} else{
			for(Datei d : n){
				final Chat c = get(id, Integer.parseInt(""+d.name()));
				if(c.ms.isEmpty()){
					loadC();
					c.delete();
				} else{
					if(c.info == null){
						ProfilManager.get(c.partner, false, new InfoListener(){
							public void ok(UserInfo info){
								c.info = info;
								loadC();
							}
							
							public void fail(){
								f = true;
								loadC();
							}
						});
					} else{
						loadC();
					}
				}
			}
            Collections.sort(chats);
		}
	}
	
	private static void loadC(){
		p--;
		if(p <= 0){
            r.run();
			if(f){
                System.out.println("NachrichtenManager: Fehler beim Laden der Chats!");
				l.fail(null);
			} else{
                System.out.println("NachrichtenManager: "+chats.size()+" Chats wurden geladen!");
				l.ok(null);
			}
		}
	}
	
	public static Chat[] get(){
		for(int i = 0; i < chats.size(); i++){
			if(chats.get(i).id == Sitzung.info.id && !chats.get(i).ms.isEmpty()) cache.add(i);
		}
		Chat[] aus = new Chat[cache.size()];
		for(int i = 0; i < cache.size(); i++){
			aus[i] = chats.get(i);
		}
		cache.clear();
		return aus;
	}
	
	public static Chat get(int id, int partner){
		for(int i = 0; i < chats.size(); i++){
			if(chats.get(i).id == id && chats.get(i).partner == partner) return chats.get(i);
		}
		
		Chat c = new Chat();
		c.id = id;
		c.partner = partner;
		c.dir = main.createF(id+"/"+partner);
		c.read = c.dir.create("r");
		try{
			c.readTime = Long.parseLong(c.read.read());
		}catch(Exception e){
			System.out.println("NachrichtenManager: Chat wird erstellt mit "+partner+"");
		}
		
		for(Datei d : c.dir.list()){
			String n = d.name();
			if(n.equals("r")) continue;
			boolean o = n.endsWith("X");
			if(o) n = n.substring(0, n.length()-1);
			c.add(d.read(), o, Long.parseLong(n));
		}
		
		chats.add(c);
		return c;
	}
	
	public static Chat add(int id, int other, boolean owner, String text, UserInfo info){
		Chat c = get(id, other);
		Collections.swap(chats, chats.indexOf(c), 0);
		long t = System.currentTimeMillis();
		c.add(text, owner, t);
		c.dir.create(t+""+(owner?"X":"")+"").write(text);
		c.info = info;
		return c;
	}
}
