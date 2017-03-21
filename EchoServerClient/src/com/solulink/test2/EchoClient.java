package com.solulink.test2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class EchoClient {

    private String host;
    private int port;
    private static int seq;
    
    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public byte[] settingData() {
        byte[] data = new byte[70];
        for (int i=0; i<data.length; i++) {
            data[i] = '0';
        }
        byte[] totalByte = getByte(8, 70);
        System.arraycopy(totalByte, 0, data, 0, 8);//total length
        System.arraycopy(host.getBytes(), 0, data, 8, 8);//hostname
        long date = System.currentTimeMillis();
        SimpleDateFormat formatter  = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
        String today = formatter.format(new java.util.Date(date));
        System.arraycopy(today.getBytes(), 0, data, 16, 17);//time
        byte[] seqByte = getByte(5, seq);
        System.arraycopy(seqByte, 0, data, 33, 5);//seq
        seq++;
        System.arraycopy("00".getBytes(), 0, data, 38, 2);//00
        System.arraycopy("S".getBytes(), 0, data, 40, 1);//sync
        System.arraycopy("Q".getBytes(), 0, data, 41, 1);//req/res
        System.arraycopy("0".getBytes(), 0, data, 42, 1);//success/failure
        System.arraycopy("ISSON010000000".getBytes(), 0, data, 43, 12);//ruleID
        return data;
    }
    
    public byte[] getByte(int total, int data) {
        byte[] data2 = new byte[total];
        String dataStr = Integer.toString(data);
        byte[] dataStrByte = dataStr.getBytes();
        for(int i=0; i<total-dataStrByte.length ; i++) {
            data2[i] = '0';
        }
        System.arraycopy(dataStrByte, 0, data2, total-dataStrByte.length, dataStrByte.length);
        System.out.println("data: "+ new String(data2));
        return data2;
    }
    
    public void execute() {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            socket = new Socket(host, port);
            os = socket.getOutputStream();
            is = socket.getInputStream();
            
            byte[] data = settingData();
            os.write(data);;
            os.flush();
            
            int cnt = 0;
            byte[] responseData = new byte[70];
            while((cnt = is.read(responseData)) != 0) {
                if (cnt == 70) break;
            }
            System.out.println("Response Data ] " + new String(responseData) + "]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) { try {is.close();} catch (Exception e1) {} }
            if(os != null) { try {os.close();} catch (Exception e1) {} }
            if(socket != null) { try {socket.close();} catch (Exception e1) {} } 
        }
    }
    
    public static void main(String[] args) {
        /*if(args.length != 2) {
            System.out.println("Using EchoClient <ip address> <port>");
            System.exit(1);
        }*/
        //String host = args[0];
        //int port = Integer.parseInt(args[1]);
        //EchoClient client = new EchoClient(host, port);
        EchoClient client = new EchoClient("localhost", 7777);
        client.execute();
    }
}
