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

public class Client extends FTPClient{
    private static final String host = "s67.goserver.host";
    private static final int port = 21;
    private static final String user = "web62f2";
    private static final String p = "demo_passw";

    public Client() throws IOException{
        connect(host, port);
        enterLocalPassiveMode();
        log();
        login(user, p);
        log();
        setFileType(FTP.BINARY_FILE_TYPE);
    }

    public File download(int id) throws IOException {
        FTPFile[] fs = listFiles();
        log();

        FTPFile f = null;

        for(int i = 0; i < fs.length; i++){
            System.out.println(fs[i].getName());
            FTPFile d = fs[i];
            if(d.getName().startsWith(id+".")){
                System.out.println("FOUND");
                f = d;
                break;
            }
        }

        if(f != null) {
            Datei d = Datei.root("bilder").create(f.getName());
            d.f.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(d.f);
            System.out.println(f.getName());
            retrieveFile(f.getName(), out);
            log();
            return d.f;
        }
        return null;
    }

    public void upload(String to, InputStream in) throws IOException{
        storeFile(to, in);
        log();
    }

    public void close() throws IOException{
        logout();
        disconnect();
    }

    public void log(){
        System.out.println(getReplyString());
    }
}
