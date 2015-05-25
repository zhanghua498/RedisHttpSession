package zh.redis.httpsession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Request事件主题，主要是为了在请求结束后同步本地Session到Redis
 * @author zhanghua
 *
 */
public class RequestEventSubject {
	private RequestEventObserver listener;
	
	public void attach(RequestEventObserver eventObserver) {
		this.listener = eventObserver;
	}

	public void detach() {
		this.listener = null;
	}

	public void completed(HttpServletRequest req,HttpServletResponse res) {
		if (this.listener != null)
			this.listener.completed(req, res);
	}
}
