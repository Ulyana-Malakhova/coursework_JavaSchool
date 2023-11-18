package org.example.documents.loggingAOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@Aspect
public class LoggingAspect {
    private Logger logger=Logger.getLogger(LoggingAspect.class.getName());
    @Pointcut("within(org.example.documents.controller.DocumentController)")
    public void pointcut(){}

    @After("pointcut()")
    public void logInfoMethod(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.log(Level.INFO,"Название метода: "+methodName+", параметры метода: "+ List.of(args));
    }
}
