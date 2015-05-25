package zh.redis.httpsession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Requst事件观察者，在每次请求开始时注入到RequestWrapper中
 * @author 张华
 *
 */
public interface RequestEventObserver{
  public void completed(HttpServletRequest req, HttpServletResponse res);
}