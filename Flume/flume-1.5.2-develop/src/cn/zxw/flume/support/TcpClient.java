package cn.zxw.flume.support;

import java.io.ByteArrayOutputStream;  
import java.io.IOException;   
import java.net.InetSocketAddress;  
import java.net.SocketAddress;   
import java.nio.ByteBuffer;  
import java.nio.channels.SocketChannel;  
import java.util.logging.Level;  
import java.util.logging.Logger;   

import cn.zxw.net.test1.MyRequestObject;
import cn.zxw.net.test1.MyResponseObject;
import cn.zxw.serializable.SerializableUtil;
  
public class TcpClient {  
    private final static Logger logger = Logger.getLogger(TcpClient.class.getName());  
    public static String host = "192.168.73.128";
    public static Integer port = 5158;
    
    
    public static void main(String[] args) throws Exception {  
        for (int i = 1; i <= 5; i++) {    
            new Thread(new MyRunnable(i)).start(); 
            Thread.sleep(3000);
        }  
    }  
      
    private static final class MyRunnable implements Runnable {  
        private final int idx;  
        private MyRunnable(int idx) {  
            this.idx = idx;   
        }
        public void run() {
            SocketChannel socketChannel = null;  
            try {   
                socketChannel = SocketChannel.open();  
                SocketAddress socketAddress =  new InetSocketAddress(host, 10000);  
                socketChannel.connect(socketAddress);
  
                MyRequestObject myRequestObject =  new MyRequestObject("request_name_" + idx, "request_value_" + idx);  
                logger.log(Level.INFO, myRequestObject.toString());  
                sendData(socketChannel, myRequestObject);  
                  
                MyResponseObject myResponseObject = receiveData(socketChannel);  
                logger.log(Level.INFO, myResponseObject.toString());
            } catch (Exception ex) {
                logger.log(Level.SEVERE,  null, ex);  
            } finally {
                try {   
                    socketChannel.close();  
                } catch(Exception ex) {}  
            }  
        }  
  
        private void sendData(SocketChannel socketChannel, MyRequestObject myRequestObject) throws IOException {  
            byte[] bytes = SerializableUtil.toBytes(myRequestObject);  
            ByteBuffer buffer = ByteBuffer.wrap(bytes);   
            socketChannel.write(buffer);  
            socketChannel.socket().shutdownOutput();   
        }  
  
        private MyResponseObject receiveData(SocketChannel socketChannel) throws IOException {  
            MyResponseObject myResponseObject =  null;  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
              
            try {
                ByteBuffer buffer = ByteBuffer.allocateDirect(1024);  
                byte[] bytes;   
                int count = 0;  
                while ((count = socketChannel.read(buffer)) >= 0) {
                    buffer.flip();  
                    bytes = new  byte[count];  
                    buffer.get(bytes);  
                    baos.write(bytes);  
                    buffer.clear();  
                }
                bytes = baos.toByteArray();  
                Object obj = SerializableUtil.toObject(bytes);   
                myResponseObject = (MyResponseObject) obj;  
                socketChannel.socket().shutdownInput();  
            } finally {
                try {  
                    baos.close();  
                } catch(Exception ex) {}  
            }
            return myResponseObject;  
        }  
    }  
}  
