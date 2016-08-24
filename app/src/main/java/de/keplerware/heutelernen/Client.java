package de.keplerware.heutelernen;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.keplerware.heutelernen.io.Datei;
import de.keplerware.heutelernen.manager.BildManager;

public class Client extends FTPClient{
    private static final String host = "s67.goserver.host";
    private static final int port = 21;
    private static final String user = "web62f2";
    private static final String p = "demo_passw";

    public Client() throws IOException{
        connect(host, port);
        enterLocalPassiveMode();
        login(user, p);
        setFileType(FTP.BINARY_FILE_TYPE);
    }

    public File download(int id) throws IOException{
        String n = build(id);
        Datei d = BildManager.root.create(n);
        d.f.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(d.f);
        boolean b = retrieveFile(n, out);
        out.close();
        return b ? d.f : null;
    }

    private String build(int id){
        return id+".jpg";
    }

    public void delete(int id) throws IOException{
        deleteFile(build(id));
    }

    public void upload(int id, InputStream in) throws IOException{
        storeFile(build(id), in);
    }

    public void close() throws IOException{
        logout();
        disconnect();
    }
}
