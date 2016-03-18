package cn.zxw.net;

import java.io.*;
import java.net.*;

class UDPServer1{
    public static void main(String[] args)throws IOException{
    	while(true){
        DatagramSocket  server = new DatagramSocket(5050);

        byte[] recvBuf = new byte[1024];
        DatagramPacket recvPacket= new DatagramPacket(recvBuf , recvBuf.length);

        server.receive(recvPacket);

        String recvStr = new String(recvPacket.getData() , 0 , recvPacket.getLength());
        System.out.println("接收:" + recvStr);

        int port = recvPacket.getPort();
        InetAddress addr = recvPacket.getAddress();
        
        String sendStr1="{\"msg_type\":\"09\",\"err_code\":\"0\",\"status\":\"1\",\"timestamp_s\":\"1423535765\",\"locs\":[{\"mac\": \"0023cd03350a\",\"x\": \"15797\",\"y\": \"7231\"},{\"mac\": \"0071cc6396e1\", \"x\": \"11866\",\"y\": \"8222\"}]}";
        String sendStr2="{\"msg_type\":\"09\",\"err_code\":\"0\",\"status\":\"0\",\"timestamp_s\":\"1423535765\",\"locs\":[{\"mac\": \"0023cd03350a\",\"x\": \"15797\",\"y\": \"7231\"},{\"mac\": \"0071cc6396e1\", \"x\": \"11866\",\"y\": \"8222\"}]}";
        byte[] sendBuf1 = sendStr1.getBytes();
        DatagramPacket sendPacket1 = new DatagramPacket(sendBuf1 , sendBuf1.length , addr , port );
        server.send(sendPacket1);
        System.out.println("发送:sendStr1");
        
        byte[] sendBuf2 = sendStr2.getBytes();
        DatagramPacket sendPacket2 = new DatagramPacket(sendBuf2 , sendBuf2.length , addr , port );
        server.send(sendPacket2);
        System.out.println("发送:sendStr2");
        
        server.close();
    	}
    }
}
