package grisu.control.util;

import grisu.control.GrisuUserDetails;

import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditAdvice implements MethodInterceptor {

	static final Logger myLogger = Logger
			.getLogger(AuditAdvice.class.getName());

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {

		String method = methodInvocation.getMethod().getName();
		String dn = null;

		final SecurityContext securityContext = SecurityContextHolder
				.getContext();
		final Authentication authentication = securityContext
				.getAuthentication();

		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			if (principal instanceof GrisuUserDetails) {
				GrisuUserDetails gud = (GrisuUserDetails) principal;
				dn = gud.getProxyCredential().getDn();
			}
		}

		Date start = new Date();

		if (dn == null) {
			myLogger.debug("Entering method: " + method + " time: "
					+ start.getTime());
		} else {
			myLogger.debug("Entering method: " + method + " user: " + dn
					+ " time: " + start.getTime());
		}

		Object result = methodInvocation.proceed();

		Date end = new Date();

		long duration = end.getTime() - start.getTime();

		if (dn == null) {
			myLogger.debug("Finished method: " + method + " time: "
					+ end.getTime() + " duration: " + duration);
		} else {
			myLogger.debug("Finished method: " + method + " user: " + dn
					+ " time: " + end.getTime() + " duration: " + duration);
		}

		return result;
	}

}
