package br.com.litecode.service.push;

import br.com.litecode.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PushAspect {
	@Autowired
	private PushService pushService;

	@After(value = "execution(* br.com.litecode..controller..*(..)) && @annotation(push))")
	public void pushRefresh(JoinPoint joinPoint, PushRefresh push) {
		User loggedUser = Faces.getSessionAttribute("loggedUser");
		pushService.publish(PushChannel.REFRESH, "", loggedUser);

		log.info("[{}] {}", loggedUser.getUsername(), joinPoint.getSignature().getName());
	}
}
