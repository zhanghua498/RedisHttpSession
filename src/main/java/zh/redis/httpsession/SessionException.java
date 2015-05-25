package zh.redis.httpsession;

public class SessionException extends RuntimeException {

	private static final long serialVersionUID = -4742795005659710148L;

	public SessionException() {
	}

	public SessionException(String message) {
		super(message);
	}

	public SessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SessionException(Throwable cause) {
		super(cause);
	}
}
