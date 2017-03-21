package com.solulink.test2;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoExecutor implements Runnable{
    private Socket client;
    
    public EchoExecutor(Socket client) {
        this.client = client;
    }

    public byte[] getByte(int total) {
        byte[] data = new byte[total];
        for(int i=0; i<data.length ; i++) {
            data[i] = '0';
        }
        return data;
    }
    
    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        boolean check = false;
        try {
            is = client.getInputStream();
            os = client.getOutputStream();
            int totalLen = 0;
            int readLen = 0;
            byte[] buffer = new byte[70];
            while(!check) {
                readLen = is.read(buffer, 0, 8);
                byte[] readData = new byte[readLen];
                if (readLen == 8) {
                    for(int i=1; i<= readLen; i++) {
                        if (readData[i] == 0x31) continue;
                        else {
                            readLen = i-1;
                            break;
                        }
                    }
                    System.out.println(8-readLen);
                    byte[] totalByte = getByte(8-readLen);
                    System.arraycopy(readData, readLen, totalByte, 0, 8-readLen);
                    System.out.println("total Length : "+ new String(totalByte));
                    totalLen = Integer.parseInt(new String(totalByte));
                    
                    check = true;
                }
            }
            check = false;
            while(!check) {
                readLen = is.read(buffer, 8, totalLen-8);
                if (readLen == totalLen-8) {
                    byte[] readData = new byte[readLen];
                    System.arraycopy(buffer, 0, readData, 0, readLen);
                    System.out.println("READ DATA [" + new String(readData) +"]");
                    os.write(readData);
                    os.flush();
                    check = true;
                }
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
