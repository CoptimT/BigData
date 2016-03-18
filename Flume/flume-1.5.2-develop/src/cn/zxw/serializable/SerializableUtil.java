package cn.zxw.serializable;

import java.io.ByteArrayInputStream;  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.ObjectInputStream;   
import java.io.ObjectOutputStream;  

import cn.zxw.bean.User;
  
public class SerializableUtil {  
      
    public static byte[] toBytes(Object object) {   
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        ObjectOutputStream oos = null;  
        try {  
            oos = new ObjectOutputStream(baos);  
            oos.writeObject(object);  
            byte[] bytes = baos.toByteArray();  
            return bytes;  
        } catch(IOException ex) {  
            throw new RuntimeException(ex.getMessage(), ex);  
        } finally {   
            try {  
                oos.close();  
            } catch (Exception e) {}  
        }  
    }  
      
    public static Object toObject(byte[] bytes) {  
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);  
        ObjectInputStream ois = null;  
        try {  
            ois = new ObjectInputStream(bais);  
            Object object = ois.readObject();  
            return object;  
        } catch(IOException ex) {  
            throw new RuntimeException(ex.getMessage(), ex);  
        } catch(ClassNotFoundException ex) {  
            throw new RuntimeException(ex.getMessage(), ex);  
        } finally {   
            try {  
                ois.close();  
            } catch (Exception e) {}  
        }  
    }
    
    public static void main(String[] args) {
		User user=new User(123, "lililily");
		byte[] objBytes=SerializableUtil.toBytes(user);
		Object obj=SerializableUtil.toObject(objBytes);
		User user1=(User) obj;
		System.out.println(user1.getId());
		System.out.println(user1.getName());
	}
}  
