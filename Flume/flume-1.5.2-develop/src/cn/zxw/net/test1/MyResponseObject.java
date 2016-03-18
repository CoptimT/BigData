package cn.zxw.net.test1;
import java.io.Serializable;  
  
public class MyResponseObject implements Serializable {  
  
    private static final long serialVersionUID = 1L;  
  
    private String name;  
      
    private String value;  
  
    private byte[] bytes;  
      
    public MyResponseObject(String name, String value) {  
        this.name = name;  
        this.value = value;  
        this.bytes = new byte[1024];  
    }  
      
    public String getName() {  
        return name;  
    }  
  
    public void setName(String name) {  
        this.name = name;  
    }  
  
    public String getValue() {  
        return value;  
    }  
  
    public void setValue(String value) {  
        this.value = value;  
    }  
      
    @Override  
    public String toString() {  
        StringBuffer sb = new StringBuffer();  
        sb.append("Response [name: " + name  + ", value: " + value + ", bytes: " + bytes.length+ "]");  
        return sb.toString();  
    }  
}  
