package de.keplerware.heutelernen.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import de.keplerware.heutelernen.Util;

public class Datei{
	private final File f;
	
	public static Datei root(String name){return new Datei(Util.fileDir+"/"+name);}
	
	private Datei(String path){this(new File(path));}
	private Datei(File f){this.f = f;}
	
	public Datei create(String name){return new Datei(f.getAbsolutePath()+"/"+name);}
	public Datei createF(String name){Datei d = create(name); d.f.mkdirs(); return d;}
	public String name(){return f.getName();}
	
	public Datei[] list(){
		if(!f.exists()) return null;
		
		String[] fs = f.list();
		Datei[] aus = new Datei[fs.length];
		
		for(int i = 0; i < aus.length; i++){
			aus[i] = create(fs[i]);
		}
		
		return aus;
	}
	
	public void delete(){delete(f);}
	
	private static void delete(File r){
		if(r.isDirectory()){
			for(File f : r.listFiles()){
				delete(f);
			}
		}
		r.delete();
	}
	
	public void write(String text){
		f.getParentFile().mkdirs();
		try{
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
			w.write(text);
			w.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public String read(){
		try{
			InputStreamReader in = new InputStreamReader(new FileInputStream(f), "UTF-8");
			String d = "";
			
			int b;
			while((b = in.read()) != -1){
				d += (char) b;
			}
			in.close();
			return d;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
