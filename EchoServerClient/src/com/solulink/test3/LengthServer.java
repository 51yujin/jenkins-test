package com.solulink.test3;

import java.net.ServerSocket;
import java.net.Socket;

public class LengthServer {
    private int port = -1;
    public LengthServer(int port) {
        this.port = port;
    }
    
    public void execute() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("execute server");
            while(true) {
                Socket client = server.accept(); 
                System.out.println("client Á¢±Ù: " + client.toString());
                Runnable executor = new LengthExecutor(client);
                Thread t = new Thread(executor);
                t.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(server != null) {
                try {
                    server.close();
                } catch (Exception e2) {
                }
            }
        }
    }
    
    public static void main(String[] args) {
        int port = 7777;
        if(args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        LengthServer server = new LengthServer(port);
        server.execute();
    }
}


