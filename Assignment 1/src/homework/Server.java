package homework;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    public static int PORT;
    public Server() {
        Server.PORT = 8848;
        Processor processor = new Processor(Server.PORT);
        Thread thread = new Thread(processor);
        thread.start();
    }
    public static void main(String[] args) {
        new Server();
    }
}

class Processor implements Runnable {
    private DatagramSocket server;
    private DatagramPacket packet;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private byte[] inBuff;

    private String makeString(DatagramPacket packet) {
        return new String(packet.getData(), 0, packet.getLength());
    }

    private DatagramPacket makePacket(String string, DatagramPacket queryPacket) {
        byte[] buf = string.getBytes();
        return new DatagramPacket(buf, buf.length, queryPacket.getAddress(), queryPacket.getPort());
    }

    public Processor(int port) {
        try {
            inBuff = new byte[1024 * 1024];
            server = new DatagramSocket(port);
            packet = new DatagramPacket(inBuff, inBuff.length);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void getDate(StringBuilder info) {
        info.append("Local date of server: \n\t");
        info.append(dateFormat.format(new Date()));
        info.append("\n");
    }


    private void getTime(StringBuilder info) {
        info.append("Local time of server: \n\t");
        info.append(timeFormat.format(new Date()));
        info.append("\n");
    }

    private void getFiles(StringBuilder info, String path) {
        File dir = new File(path);
        info.append("Let's see who's here:\n");
        if (dir.exists()) {
            if (dir.isDirectory()) {
                try {
                    if (dir.list().length == 0) {
                        info.append("\tIt's a blank folder.\n");
                    }
                    for (String fileName : dir.list()) {
                        info.append("\t");
                        info.append(fileName);
                        info.append("\n");
                    }
                } catch (NullPointerException e) {
                    info.append("\tUnknown Error.\n");
                }
            } else {
                info.append("\tIt's not a directory.\n");
            }
        } else {
            info.append("\tNot exist.\n");
        }
    }

    private String makeInfo(String path) {
        StringBuilder info = new StringBuilder();
        if (path.equalsIgnoreCase("time")) {
            getTime(info);
        }
        else if (path.equalsIgnoreCase("date")) {
            getDate(info);
        }
        else if (path.equals("ls")) {
            getFiles(info, ".");
        }
        else if (path.startsWith("ls ")) {
            getFiles(info, path.replaceFirst("ls ", ""));
        }
        else {
            info.append("Error:\n\t");
            info.append("command not found: ");
            info.append(path);
            info.append("\n");
        }
        return info.toString();
    }

    @Override
    public void run() {
        while (true) {
            try {
                server.receive(packet);
                String path = makeString(packet);
                server.send(makePacket(makeInfo(path), packet));
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        server.close();
    }
}