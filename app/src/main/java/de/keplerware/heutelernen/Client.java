package de.keplerware.heutelernen;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

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

    private FTPFile search(int id) throws IOException{
        FTPFile[] fs = listFiles();

        for(int i = 0; i < fs.length; i++){
            FTPFile d = fs[i];
            if(d.getName().startsWith(id+".")) return d;
        }
        return null;
    }

    public File download(int id) throws IOException {
       FTPFile f = search(id);

        if(f != null) {
            Datei d = BildManager.root.create(f.getName());
            d.f.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(d.f);
            retrieveFile(f.getName(), out);
            return d.f;
        }

        return null;
    }

    public void delete(int id) throws IOException{
        FTPFile f = search(id);

        if(f != null){
            deleteFile(f.getName());
        }
    }

    public void upload(int id, String to, InputStream in) throws IOException{
        delete(id);
        storeFile(to, in);
    }

    public void close() throws IOException{
        logout();
        disconnect();
    }
}
