package com.solulink.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoExecutor implements Runnable{
    private Socket client;
    
    public EchoExecutor(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = client.getInputStream();
            os = client.getOutputStream();
            while(true) {
                byte[] buffer = new byte[1024];
                int readLen = is.read(buffer); //실제 데이타 길이
                if(readLen <0) {
                    System.out.println("client 접근이 종료되었습니다.");
                    break;
                }
                byte[] readData = new byte[readLen];
                /*for(int i=0; i<readLen; i++) {
                    readData[i] = buffer[i];
                }*/
                System.arraycopy(buffer, 0, readData, 0, readLen);
                System.out.println("READ DATA [" + new String(readData) +"]");
                os.write(readData);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os!= null) {
                try { os.close(); } catch (Exception e2) { }
            }
            if(is!= null) {
                try { is.close(); } catch (Exception e2) { }
            }
            if(client!= null) {
                try { client.close(); } catch (Exception e2) { }
            }
        }
    }
}
