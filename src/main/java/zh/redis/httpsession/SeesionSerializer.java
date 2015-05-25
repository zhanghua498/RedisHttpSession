package zh.redis.httpsession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Session序列化工具类
 * @author zhanghua
 *
 */
public class SeesionSerializer {
	
	/**
	 * Session序列化
	 * @param session
	 * @return
	 */
	public static byte[] serialize(Serializable session){
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = null;
	    try{
	    	oos = new ObjectOutputStream(new BufferedOutputStream(bos));
		    oos.writeObject(session);	
		    oos.close();
		    return bos.toByteArray();
	    }
	    catch (IOException e){
	    	e.printStackTrace();
	    }
	    finally{
	    	if(oos != null){	    		
	    		try {
	    			bos.close();
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    return null;
	}
	
	/**
	 * Session反序列化
	 * @param session
	 * @return
	 */
	public static RedisHttpSession deserialize(byte[] data){
		RedisHttpSession session = null;
		if(data!=null && data.length>0){
		    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(data));
		    ObjectInputStream ois = null;		    
			try {
				ois = new ObjectInputStream(bis);
				session = (RedisHttpSession)ois.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally{
				if(ois != null){	    		
		    		try {
		    			bis.close();
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		    	}
			}
		}
	    return session;
	}
}
