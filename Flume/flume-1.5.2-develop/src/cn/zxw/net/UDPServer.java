package cn.zxw.net;

import java.io.*;
import java.net.*;

class UDPServer{
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
        String sendStr = "Hello1234567890abcdef";
        byte[] sendBuf = sendStr.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuf , sendBuf.length , addr , port );
        server.send(sendPacket);
        server.close();
    	}
    }
}
