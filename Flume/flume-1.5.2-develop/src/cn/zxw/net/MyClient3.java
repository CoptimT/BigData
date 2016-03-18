package cn.zxw.net;

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
  
public class MyClient3 {  
  
    private final static Logger logger = Logger.getLogger(MyClient3.class.getName());  
      
    public static void main(String[] args) throws Exception {  
        for (int i = 0; i < 100; i++) {  
            final int idx = i;  
            new Thread(new MyRunnable(idx)).start();  
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
                SocketAddress socketAddress =  new InetSocketAddress("localhost", 10000);  
                socketChannel.connect(socketAddress);
  
                MyRequestObject myRequestObject =  new MyRequestObject("request_" + idx, "request_" + idx);  
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
