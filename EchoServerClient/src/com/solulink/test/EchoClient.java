package com.solulink.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class EchoClient {

    private String host;
    private int port;
    
    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void execute() {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader reader = null;
        try {
            socket = new Socket(host, port);
            os = socket.getOutputStream();
            is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                System.out.print("input: ");
                String input = reader.readLine();
                if (input == null) {
                    System.err.println("## error"); 
                    break;
                }
                if("quit".equalsIgnoreCase(input.trim())) {
                    System.out.println("exit success");
                    break;
                }
                os.write(input.getBytes());
                byte[] buffer = new byte[1024];
                int readLen = is.read(buffer);
                byte[] readData = new byte[readLen];
                System.arraycopy(buffer, 0, readData, 0, readLen);
                System.out.println("response data  [" + new String(readData) + "]");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) { try {is.close();} catch (Exception e1) {} }
            if(os != null) { try {os.close();} catch (Exception e1) {} }
            if(socket != null) { try {socket.close();} catch (Exception e1) {} } 
        }
    }
    
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Using EchoClient <ip address> <port>");
            System.exit(1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        EchoClient client = new EchoClient(host, port);
        client.execute();
    }
}
