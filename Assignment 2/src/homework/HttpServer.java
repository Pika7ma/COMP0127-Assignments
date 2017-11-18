package homework;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer implements Runnable {

    private Socket socket = null;
    private String root = null;
    private Request request = null;
    private Response response = null;
    private String path = null;
    public static FileOutputStream fos = null;

    public HttpServer(Socket socket, String root) {
        this.socket = socket;
        this.root = root;
    }

    synchronized public static void log(String logLine) {
        try {
            fos.write(logLine.getBytes());
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = 8848;
        String root = new File("Web/").getAbsolutePath();
        try {
            HttpServer.fos = new FileOutputStream("logging.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HttpServer.listen(port, root);
    }

    public static void listen(int port, String root) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                HttpServer server = new HttpServer(socket, root);
                Thread thread = new Thread(server);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            this.request = new Request(this.socket.getInputStream());
            this.request.parse();
            this.path = this.request.getPath();
            this.response = new Response(this.root, this.path, this.socket.getOutputStream());
            this.response.generate();
            this.response.send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
