package homework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Client {
    public static int PORT;
    public static InetAddress ADDR;
    public Client() {
        try {
            ADDR = InetAddress.getByName("localhost");
            PORT = 8848;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Sender sender = new Sender(Client.PORT, Client.ADDR);
        Thread thread = new Thread(sender);
        thread.start();
    }
    public static void main(String[] args) {
        new Client();
    }
}

class Sender implements Runnable {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private DatagramPacket received;

    private static int PORT;
    private static InetAddress ADDR;
    private byte[] outBuff;
    private byte[] inBuff;

    public Sender(int PORT, InetAddress ADDR) {
        Sender.PORT = PORT;
        Sender.ADDR = ADDR;
        inBuff = new byte[1024 * 1024];
        received = new DatagramPacket(inBuff, inBuff.length);
    }

    private String makeString(DatagramPacket packet) {
        return new String(packet.getData(), 0, packet.getLength());
    }

    @Override
    public void run() {

        while (true) {
            try {
                socket = new DatagramSocket();
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                String path = reader.readLine();
                outBuff = path.getBytes();
                packet = new DatagramPacket(outBuff, outBuff.length, Sender.ADDR, Sender.PORT);
                socket.send(packet);
                socket.receive(received);
                String output = makeString(received);
                System.out.println(output);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}