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
		public int schule;
		public String schuleText;

		public boolean owner(){
			return id == Sitzung.info.id;
		}
	}
	
	public static class Nachricht{
        public String typ;
		public String text;
		public int id;
		public UserInfo info;
	}
	
	public static class Angebot{
		public UserInfo info;
		public String fach;
	}
	
	private static void internet(String file, String text, final boolean hidden, final Listener l, String[] pk, String[] pv){
		final Runnable r = Util.pause()?null:Dialog.progress(text);
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

	public static void beschreibung(int id, String t, Listener l){
		internet("beschreibung", "Beschreibung wird geändert...", false, l, new String[]{"id", "b"}, new String[]{""+id, t});
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
                        n.typ = s[0];
						n.id = Integer.parseInt(s[1]);
						n.text = s[2].replace("euro", "€");
						ns[i] = n;
					}
					li.ok(ns);
				}
			}
			
			public void fail(Exception e){
				li.fail();
			}
		}, new String[]{"id", "a"}, new String[]{""+id, ""+(!Util.pause())});
	}

	public static void deleteUser(UserInfo info){
		internet("delete", "Lösche Benutzer...", false, new Listener() {
			public void ok(String data){
				Util.toast("Benutzer wurde gelöscht!");
			}

			public void fail(Exception e) {}
		}, new String[]{"id"}, new String[]{""+info.id});
	}
	
	public static void register(String vn, String nn, int jahrgang, String mail, String ort, String p, int schule, final RegisterListener l){
		internet("register", "Registriere...", false, new Listener() {
            public void ok(String data){
                if(data.equals("X")){
                    l.fail(false);
                } else{
                    l.ok();
                }
            }

            public void fail(Exception e){
                l.fail(true);
            }
        }, new String[]{"vname", "nname", "jahrgang", "mail", "ort", "p", "schule"}, new String[]{vn, nn, "" + jahrgang, mail, ort, p, ""+schule});
	}

	public static void angebotEntfernen(String fach, int id, Listener l){
		internet("angebot_entfernen", "Lösche '" + fach + "' als dein Nachhilfefach...", false, l, new String[]{"id", "f"}, new String[]{""+id, fach});
	}
	
	public static void info(final int id, boolean dialog, final InfoListener l){
		internet("info", dialog ? "Lade Daten..." : null, false, new Listener(){
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
                try{i.beschreibung = s[6];}catch(Exception e){i.beschreibung = "";}
				i.schule = Integer.parseInt(s[7]);
				try{i.schuleText = Util.schulen[i.schule];}catch(Exception e){i.schuleText = "Unbekannte Schule!";}
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
						ProfilManager.get(Integer.parseInt(data), true, new InfoListener() {
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

	public static void nachrichtSystem(final UserInfo zu, final String text, Listener l){
		internet("nachricht_senden", "Nachricht wird gesendet...", false, l, new String[]{"typ", "von", "zu", "text"}, new String[]{"system", ""+Sitzung.info.id, ""+zu.id, text.replace("€", "euro")});
	}
	
	public static void nachricht(final UserInfo zu, final String text, final Listener l){
		internet("nachricht_senden", null, false, new Listener(){
			public void ok(String data){
				NachrichtenManager.add(Sitzung.info.id, zu.id, true, text, zu);
				l.ok(data);
			}

			public void fail(Exception e){
				l.fail(e);
			}}, new String[]{"typ", "von", "zu", "text"}, new String[]{"chat", ""+Sitzung.info.id, ""+zu.id, text.replace("€", "euro")});
	}
	
	public static void angebote(final UserInfo info, final AngebotListener li){
		internet("auflistenID", null, false, new Listener(){
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
		internet("auflisten", "Suche nach Nachhilfe...", false, new Listener(){
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
						ProfilManager.get(id, true, new InfoListener() {
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
