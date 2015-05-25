package zh.redis.httpsession;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class SessionEnumeration implements Enumeration<String> {
	
	private Iterator<String> iterator;
	
	public SessionEnumeration(Map<String, Object> data){
		this.iterator = data.keySet().iterator();
	}

	@Override
	public boolean hasMoreElements() {
		return this.iterator.hasNext();
	}

	@Override
	public String nextElement() {
		return this.iterator.next();
	}

}
