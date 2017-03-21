package com.solulink.test3;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class LengthClient {

    private String host;
    private int port;
    
    private static int SEQ = 0;
    
    public LengthClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void execute(byte[] data) {
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            socket = new Socket(host, port);
            os = socket.getOutputStream();
            is = socket.getInputStream();
            byte[] sendData = createMessage(data);
            os.write(sendData);
            
            byte[] lenBuffer = new byte[8];
            int readOffset = 0;
            while(true) {
                int tmp = is.read(lenBuffer, readOffset, lenBuffer.length - readOffset);
                if(tmp < 0) {
                    System.err.println("server와 접속  종료");
                    break;
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
            
            byte[] readData = new byte[totalLength];
            System.arraycopy(lenBuffer, 0, readData, 0, lenBuffer.length);
            while(true) {
                int tmp = is.read(readData, readOffset, readData.length - readOffset);
                if(tmp < 0) {
                    System.err.println("server와 접속  종료");
                    break;
                }
                readOffset += tmp;
                if(readOffset == readData.length) {
                    break;
                }
            }
            System.out.println("IN [" + new String(readData) + "]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) { try {is.close();} catch (Exception e1) {} }
            if(os != null) { try {os.close();} catch (Exception e1) {} }
            if(socket != null) { try {socket.close();} catch (Exception e1) {} } 
        }
    }
    
    public byte[] createMessage(byte[] data) {
        int offset = 0;
        byte[] rtnValue = new byte[70 + data.length];
        //Length   8  0
        String lengthStr = String.format("%08d", rtnValue.length);
        System.arraycopy(lengthStr.getBytes(), 0, rtnValue, offset, 8); offset += 8;
        //GID      32 8
        //host 8
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = "localhos";
        }
        byte[] hostNameBytes = new byte[8];
        Arrays.fill(hostNameBytes, (byte)' ');
        for(int i=0; i<hostName.length() && i < 8; i++) {
            hostNameBytes[i] = hostName.getBytes()[i];
        }
        System.arraycopy(lengthStr.getBytes(), 0, rtnValue, offset, 8); offset += 8;
        //time 17 yyyyMMddHHmmssSSS
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String time = sdf.format(new Date());
        System.arraycopy(time.getBytes(), 0, rtnValue, offset, 17); offset += 17;
        //seq 5
        String seq = null;
        synchronized (LengthClient.class) {//동기화처리를 위함.
            SEQ++;
            if(SEQ > 99999) {
                SEQ = 0;
            }
            seq = String.format("%05d", SEQ); 
        }
        System.arraycopy(seq.getBytes(), 0, rtnValue, offset, 5); offset += 5;
        //00
        System.arraycopy("00".getBytes(), 0, rtnValue, offset, 2); offset += 2;
        //sync     1  40
        rtnValue[40] = 'S';  offset += 1;
        //req/res  1  41
        rtnValue[41] = 'S'; offset += 1;
        //result   1  42
        rtnValue[42] = '0'; offset += 1;
        //ruleid   12 43 : AAAAAAAAAAAA
        System.arraycopy("AAAAAAAAAAAA".getBytes(), 0, rtnValue, offset, 12); offset += 12;
        //node     2  55
        System.arraycopy("00".getBytes(), 0, rtnValue, offset, 2); offset += 2;
        //여유 필드    13 57
        System.arraycopy("             ".getBytes(), 0, rtnValue, offset, 13); offset += 13;
        //데이타
        System.arraycopy(data, 0, rtnValue, offset, data.length);
        System.out.println("Make DATA [" + new String(rtnValue) + "]");
        return rtnValue;
    }
    
    public static void main(String[] args) {
       /* if(args.length != 2) {
            System.out.println("Using EchoClient <ip address> <port>");
            System.exit(1);
        }*/
        String host = "localhost";
        int port = 7777;
        byte[] data = "request data xxxxxxx".getBytes();
        LengthClient client = new LengthClient(host, port);
        client.execute(data);
    }
}
