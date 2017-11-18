package homework;

import java.io.*;
import java.net.URLDecoder;

public class Request {

    private BufferedReader br;
    private String path;

    public Request(InputStream is) {
        this.br = new BufferedReader(new InputStreamReader(is));
    }

    public void parse() {
        String header;
        try {
            while ((header = this.br.readLine()) != null && !header.equals("\r\n") && header.length() != 0) {
                System.out.println(header);
                if (header.startsWith("GET")) {
                    this.path = this.parsePath(header);
                    if (HttpServer.fos != null) {
                        HttpServer.log(header + "\n");
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String parsePath(String header) {
        String decoded = null;
        String[] segments = header.split(" ");
        if (segments.length < 2) {
            return null;
        }
        try {
            decoded = URLDecoder.decode(segments[1], "UTF-8");
            if (decoded.equals("/")) {
                decoded = "/index.html";
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return decoded;
    }


    public String getPath() {
        return this.path;
    }
}
