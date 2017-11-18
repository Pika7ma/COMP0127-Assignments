package homework;

import java.io.*;
import java.util.Date;

public class Response {

    File file = null;
    String type = "text/html";
    PrintStream ps = null;
    String stateCode = "";

    public Response(String root, String path, OutputStream os) {
        if (path != null) {
            this.file = new File(root, path);
        }
        this.ps = new PrintStream(os, true);
    }

    public void generate() {
        if (file == null || !file.exists() || !file.isFile()) {
//            System.out.println(file);
            this.stateCode = "404";
        } else {
            this.stateCode = "200";
            String fileName = this.file.getName();
            if (fileName.endsWith("jpg")) {
                this.type = "image/jpg";
            } else if (fileName.endsWith("gif")) {
                this.type = "image/gif";
            }
        }
    }

    public void send() {
        if (this.stateCode.equals("404")) {
            ps.println("HTTP/1.0 404 Not found");
            ps.println();
            this.ps.flush();
        } else {
            ps.println("HTTP/1.1 200 OK");
            ps.println("Date: " + new Date());
            ps.println("Content-Type: " + this.type);
            ps.println("Content-Length: " + this.file.length());
            ps.println();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(this.file);
                byte[] buf = new byte[fis.available()];
                fis.read(buf);
                this.ps.write(buf);
                this.ps.flush();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ps.close();
    }

}
