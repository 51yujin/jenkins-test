package com.solulink.test3;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class LengthExecutor implements Runnable{
    private Socket client;
    
    public LengthExecutor(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = client.getInputStream();
            os = client.getOutputStream();
            PROCESS:
            while(true) {
                byte[] lenBuffer = new byte[8];
                int readOffset = 0;
                while(true) {
                    int tmp = is.read(lenBuffer, readOffset, lenBuffer.length - readOffset);
                    if(tmp < 0) {
                        System.err.println("client 접속  종료");
                        break PROCESS;
                    }
                    readOffset += tmp;
                    if(readOffset == lenBuffer.length) {
                        break;
                    }
                }
                /*if(readOffset != lenBuffer.length) {
                    System.err.println("길이 필드 read 실패");
                    break;
                }*/
                System.out.println("길이 필드 [" + new String(lenBuffer) + "]");
                int totalLength = Integer.parseInt(new String(lenBuffer));
                
                byte[] data = new byte[totalLength];
                System.arraycopy(lenBuffer, 0, data, 0, lenBuffer.length);
                while(true) {
                    int tmp = is.read(data, readOffset, data.length - readOffset);
                    if(tmp < 0) {
                        System.err.println("client 접속  종료");
                        break;
                    }
                    readOffset += tmp;
                    if(readOffset == data.length) {
                        break;
                    }
                }
                System.out.println("IN [" + new String(data) + "]");
                os.write(data);
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
