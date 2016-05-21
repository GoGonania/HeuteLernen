package de.keplerware.heutelernen;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.keplerware.heutelernen.Util.Listener;
import de.keplerware.heutelernen.manager.NachrichtenManager;
import de.keplerware.heutelernen.manager.ProfilManager;

public class Internet{
	public interface InfoListener{
		void ok(UserInfo info);
		void fail();
	}
	
	public interface AngebotListener{
		void ok(Angebot[] as);
		void fail();
	}
	
	public interface NachrichtenListener{
		void ok(Nachricht[] ns);
		void fail();
	}
	
	public interface LoginListener{
		void ok(UserInfo info);
		void fail(int error);
	}

    public interface RegisterListener{
        void ok();
        void fail(boolean c);
    }
	
	public static class UserInfo{
		public int id;
		public String name;
		public String vname;
		public String nname;
		public String ort;
		public int klasseZahl;
		public String klasse;
		public String mail;
		public int rang;
        public String beschreibung;
	}
	
	public static class Nachricht{
		public String text;
		public int id;
		public UserInfo info;
	}
	
	public static class Angebot{
		public UserInfo info;
		public String fach;
	}
	
	private static void internet(String file, String text, final boolean hidden, final Listener l, String[] pk, String[] pv){
		final Runnable r = MainActivity.pause?null:Util.progress(text);
		String p = "";
		for(int i = 0; i < pk.length; i++){
			try{p += "&"+pk[i]+"="+URLEncoder.encode(pv[i], "UTF-8")+"";}catch (UnsupportedEncodingException e){e.printStackTrace();}
		}
		Util.internet(file, p.substring(1), new Util.Listener() {
			public void ok(final String data){
				Util.run(new Runnable(){
					public void run() {
						if(r != null) r.run();
						l.ok(data);
					}
				});
			}
			
			public void fail(final Exception ee){
				Util.run(new Runnable(){
					public void run(){
						if(!hidden) Util.toast("Keine Internet-Verbindung!");
						if(r != null) r.run();
						l.fail(ee);
					}
				});
			}
		});
	}
	
	public static void last(final int id){
		internet("last", null, true, new Listener() {
			public void ok(String data){
				Util.event(Event.LAST, id, data);
			}
			
			public void fail(Exception e){
				Util.event(Event.LAST, id, null);
			}
		}, new String[]{"id"}, new String[]{""+id});
	}
	
	public static void nachrichten(int id, final NachrichtenListener li){
		internet("nachrichten", null, true, new Listener() {
			public void ok(String data){
				if(data.isEmpty()){
					li.ok(null);
				} else{
					String[] p = data.split("\t\t");
					Nachricht[] ns = new Nachricht[p.length];
					for(int i = 0; i < p.length; i++){
						String[] s = p[i].split("\t");
						Nachricht n = new Nachricht();
						n.id = Integer.parseInt(s[0]);
						n.text = s[1];
						ns[i] = n;
					}
					li.ok(ns);
				}
			}
			
			public void fail(Exception e){
				li.fail();
			}
		}, new String[]{"id"}, new String[]{""+id});
	}
	
	public static void register(String vn, String nn, int jahrgang, String mail, String ort, String p, final RegisterListener l){
		internet("register", "Registriere...", false, new Listener() {
            public void ok(String data){
                if(data.equals("X")){
                    l.fail(false);
                } else{
                    l.fail(true);
                }
            }

            public void fail(Exception e){
                l.fail(true);
            }
        }, new String[]{"vname", "nname", "jahrgang", "mail", "ort", "p"}, new String[]{vn, nn, "" + jahrgang, mail, ort, p});
	}
	
	public static void sql(String code, Listener l){
		internet("run", "Anfrage wird bearbeitet!", false, l, new String[]{"c"}, new String[]{code});
	}
	
	public static void info(final int id, final InfoListener l){
		internet("info", "Lade Daten...", false, new Listener(){
			public void ok(String data){
				UserInfo i = new UserInfo();
				i.id = id;
				String[] s = data.split("\t");
				i.vname = s[0];
				i.nname = s[1];
				i.name = ""+i.vname+" "+i.nname+"";
				i.klasseZahl = Integer.parseInt(s[2]);
				i.klasse = (i.klasseZahl == 13 ? "Schule geschafft" : i.klasseZahl+". Klasse");
				i.mail = s[3];
				i.ort = s[4];
				i.rang = Integer.parseInt(s[5]);
                i.beschreibung = s[6];
				l.ok(i);
			}
			
			public void fail(Exception e) {
				l.fail();
			}
		}, new String[]{"id"}, new String[]{""+id});
	}
	
	public static void angebotAufgeben(String fach, int klasse, int id, Listener l){
		internet("angebot_aufgeben", "Angebot wird erstellt...", false, l, new String[]{"f", "k", "id"}, new String[]{fach, ""+klasse, ""+id});
	}
	
	private static int counter;
	private static boolean fail;
	
	public static void login(String m, String p, final LoginListener info){
		internet("login", "Logge ein...", false, new Listener(){
			public void ok(String data){
				if(data.isEmpty()){
					info.fail(LoginError.Passwort);
				} else{
					if(data.equals("X")){
						info.fail(LoginError.Bestaetigen);
					} else {
						ProfilManager.get(Integer.parseInt(data), new InfoListener() {
							public void ok(UserInfo i) {
								info.ok(i);
							}

							public void fail() {
								info.fail(LoginError.Connection);
							}
						});
					}
				}
			}
			
			public void fail(Exception e){
				info.fail(LoginError.Connection);
			}
		}, new String[]{"mail", "p"}, new String[]{m, p});
	}
	
	public static void nachricht(final UserInfo zu, final String text, final Listener l){
		internet("nachricht_senden", null, false, new Listener(){
			public void ok(String data){
				NachrichtenManager.add(Sitzung.info.id, zu.id, true, text, zu);
				l.ok(data);
			}

			public void fail(Exception e){
				l.fail(e);
			}}, new String[]{"von", "zu", "text"}, new String[]{""+Sitzung.info.id, ""+zu.id, text});
	}
	
	public static void angebote(final UserInfo info, final AngebotListener li){
		internet("auflistenID", "Suche Angebote von "+info.name+"...", false, new Listener(){
			public void ok(String data){
				if(data.isEmpty()){
					li.ok(new Angebot[]{});
				} else{
					String[] p = data.split("\t");
					final Angebot[] as = new Angebot[p.length];
					for(int i = 0; i < p.length; i++){
						Angebot a = new Angebot();
						a.fach = p[i];
						a.info = info;
						as[i] = a;
					}
					li.ok(as);
				}
			}
			
			public void fail(Exception e){
				li.fail();
			}
		}, new String[]{"id"}, new String[]{""+info.id});
	}
	
	public static void angebote(String fach, int klasse, final AngebotListener li){
		internet("auflisten", "Suche nach Angebote...", false, new Listener(){
			public void ok(String data){
				if(data.isEmpty()){
					li.ok(new Angebot[]{});
				} else{
					String[] p = data.split("\t\t");
					final Angebot[] as = new Angebot[p.length];
					counter = p.length;
					for(int i = 0; i < p.length; i++){
						String[] ss = p[i].split("\t");
						int id = Integer.parseInt(ss[0]);
						final Angebot a = new Angebot();
						a.fach = ss[1];
						as[i] = a;
						ProfilManager.get(id, new InfoListener() {
							public void ok(UserInfo info){
								if(fail) return;
								counter--;
								a.info = info;
								if(counter == 0) li.ok(as);
							}
							
							public void fail(){
								if(fail) return;
								li.fail();
								fail = true;
							}
						});
					}
				}
			}

			public void fail(Exception e){
				li.fail();
			}
		}, new String[]{"f", "k"}, new String[]{fach, ""+klasse});
	}
}
