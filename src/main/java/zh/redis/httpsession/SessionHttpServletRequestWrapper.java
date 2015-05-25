package zh.redis.httpsession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionHttpServletRequestWrapper extends HttpServletRequestWrapper {
	private HttpServletResponse response;
	private RedisHttpSession httpSession;
	private RedisSessionManager sessionManager;
	private RequestEventSubject requestEventSubject;

	public SessionHttpServletRequestWrapper(HttpServletRequest request,
			HttpServletResponse response,
			RedisSessionManager sessionManager,
			RequestEventSubject requestEventSubject) {
		super(request);
		this.response = response;
		this.sessionManager = sessionManager;
		this.requestEventSubject = requestEventSubject;
	}

	public HttpSession getSession(boolean create) {
		if ((this.httpSession != null) && (!(this.httpSession.expired)))
			return this.httpSession;
		this.httpSession = this.sessionManager.createSession(this,this.response, this.requestEventSubject, create);
		return this.httpSession;
	}

	public HttpSession getSession() {
		return getSession(true);
	}
}
