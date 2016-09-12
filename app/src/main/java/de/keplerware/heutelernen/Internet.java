package de.keplerware.heutelernen;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.keplerware.heutelernen.Util.Listener;
import de.keplerware.heutelernen.manager.DataManager;
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

    public interface SuchListener{
        void ok(UserInfo[] infos);
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
        public int typ;
		public String text;
		public int id;
		public UserInfo info;
	}
	
	public static class Angebot{
		public UserInfo info;
		public int fachID;
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
                        n.typ = Integer.parseInt(s[0]);
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
		}, new String[]{"id", "a"}, new String[]{""+id, ""+(Util.pause()?0:1)});
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
		internet("registerHash", "Registriere...", false, new Listener() {
            public void ok(String data){
                if(!data.isEmpty()){
                    l.fail(false);
                } else{
                    l.ok();
                }
            }

            public void fail(Exception e){
                l.fail(true);
            }
        }, new String[]{"vname", "nname", "jahrgang", "mail", "ort", "p", "schule"}, new String[]{vn, nn, "" + jahrgang, mail, ort, Util.hash(p), ""+schule});
	}

	public static void angebotEntfernen(int fach, int id, Listener l){
		internet("angebot_entfernen", "Entferne '" + fach + "' als Nachhilfefach...", false, l, new String[]{"id", "f"}, new String[]{""+id, ""+fach});
	}
	
	public static void info(final int id, boolean dialog, final InfoListener l){
		internet("info", dialog ? "Lade Daten..." : null, false, new Listener(){
			public void ok(String data){
                try{
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
                    i.schuleText = DataManager.schule(i.schule);
                    l.ok(i);
                }catch (Exception e){
                    l.ok(null);
                }
			}
			
			public void fail(Exception e) {
				l.fail();
			}
		}, new String[]{"id"}, new String[]{""+id});
	}
	
	public static void angebotAufgeben(int fach, Listener l){
		internet("angebot_aufgeben", "Nachhilfefach wird hinzugefügt...", false, l, new String[]{"f", "id", }, new String[]{""+fach, ""+Sitzung.info.id});
	}
	
	public static void login(String m, String p, final LoginListener info){
		internet("loginHash", "Logge ein...", false, new Listener(){
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
		}, new String[]{"mail", "p"}, new String[]{m, Util.hash(p)});
	}

    public static void benutzerSuchen(String query, final boolean dialog, final SuchListener li){
        internet("suchen", dialog ? "Suche Benutzer '" + query + "'" : null, false, new Listener(){
            public void ok(String data){
                if(data.isEmpty()){
                    li.ok(null);
                } else{
                    String[] ps = data.split("\t");
                    final UserInfo[] infos = new UserInfo[ps.length];
                    InfoPool pool = new InfoPool(ps.length);
                    for(int i = 0; i < ps.length; i++){
                        pool.add(ps[i]);
                    }
                    pool.start(new InfoPool.Listener(){
                        public void ok(){
                            li.ok(infos);
                        }

                        public void add(UserInfo info, int id){
                            infos[id] = info;
                        }

                        public void fail(){
                            li.fail();
                        }
                    }, dialog);
                }
            }

            public void fail(Exception e){
                li.fail();
            }
        }, new String[]{"q"}, new String[]{query});
    }

	public static void nachrichtSystem(final UserInfo zu, final String text, Listener l){
		internet("nachricht_senden", "Nachricht wird gesendet...", false, l,
				new String[]{"typ", "von", "zu", "text"},
				new String[]{"1", ""+Sitzung.info.id, ""+zu.id, text.replace("€", "euro")}
		);
	}
	
	public static void nachricht(final UserInfo zu, final String text, final Listener l){
		internet("nachricht_senden", null, false, new Listener(){
			public void ok(String data){
				NachrichtenManager.add(Sitzung.info.id, zu.id, true, text, zu);
				l.ok(data);
			}

			public void fail(Exception e){
				l.fail(e);
			}}, new String[]{"typ", "von", "zu", "text"}, new String[]{"0", ""+Sitzung.info.id, ""+zu.id, text.replace("€", "euro")});
	}
	
	public static void angebote(final UserInfo info, final AngebotListener li){
		internet("auflistenID", null, false, new Listener(){
			public void ok(String data){
				if(data.isEmpty()){
					li.ok(null);
				} else{
					String[] p = data.split("\t");
					final Angebot[] as = new Angebot[p.length];
					for(int i = 0; i < p.length; i++){
						Angebot a = new Angebot();
						a.fachID = Integer.parseInt(p[i]);
                        a.fach = DataManager.fach(a.fachID);
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
	
	public static void angebote(final int fach, int klasse, int schule, final AngebotListener li){
		internet("auflisten", null, false, new Listener(){
			public void ok(String data){
				if(data.isEmpty()){
					li.ok(null);
				} else{
					String[] p = data.split("\t");
					final Angebot[] as = new Angebot[p.length];
					InfoPool pool = new InfoPool(p.length);
					for(int i = 0; i < p.length; i++){
						pool.add(p[i]);
						Angebot a = new Angebot();
						as[i] = a;
					}
                    pool.start(new InfoPool.Listener(){
                        public void ok(){
                            li.ok(as);
                        }

                        public void add(UserInfo info, int id){
                            as[id].info = info;
                        }

                        public void fail() {
                            li.fail();
                        }
                    }, false);
				}
			}

			public void fail(Exception e){
				li.fail();
			}
		}, new String[]{"f", "k", "s"}, new String[]{""+fach, ""+klasse, ""+schule});
	}

	public static void passwort(String p, Listener l){
		internet("passwortHash", "Passwort wird geändert...", false, l, new String[]{"p", "id"}, new String[]{Util.hash(p), ""+Sitzung.info.id});
	}
}
