package egovframework.iChat.common.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
public class LoggerAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerAspect.class);
	static String name = "";
	static String type = "";
//	egovframework.iChat.web.dao
	@Around("execution(* egovframework.iChat..*Controller.*(..)) or execution(* egovframework.iChat..service.*Service.*(..)) or execution(* egovframework.iChat..dao.*Dao.*(..))")
//	@Around("execution(* egovframework.iChat..*(..))")
	public Object logPrint(ProceedingJoinPoint joinPoint) throws Throwable {
		
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
		
		type = joinPoint.getSignature().getDeclaringTypeName();
//		logger.info("===========> type : " + type);
		
		if(type.indexOf("Controller")  > -1) {
			LOGGER.info("★★★★★[Log]___uri[" + request.getRequestURI() +"]" + " ip:["+ request.getRemoteAddr() + "]");
			name = "[Log]___Controller \t: ";
		}
		else if(type.indexOf("Service")  > -1) {
			name = "[Log]___Service \t: ";
		}
		else if(type.indexOf("Dao")  > -1) {
			name = "[Log]___Dao \t\t: ";
		}
		LOGGER.info("★★★★★"+name + type + "." + joinPoint.getSignature().getName()+"()");
		return joinPoint.proceed();
	}
	
}
