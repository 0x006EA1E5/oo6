package org.otherobjects.cms.monitoring;

import javax.annotation.Resource;

import org.apache.commons.lang.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PerformanceInterceptor
{
    private final Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);

    @Resource
    private PerformanceInfo performanceInfo;

    public Object log(ProceedingJoinPoint call) throws Throwable
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try
        {
            Object result = call.proceed();
            return result;
        }
        finally
        {
            try
            {
                stopWatch.stop();
                String methodName = call.getSignature().getDeclaringTypeName() + "." + call.getSignature().getName();
                this.logger.debug("Method: {} took: {}ms.", new Object[]{methodName, stopWatch.getTime()});
                StringBuffer args = new StringBuffer("");
                for (Object arg : call.getArgs())
                {
                    args.append(arg + ", ");
                }
                String a = "";
                if (args.length() > 0)
                    a = args.toString().substring(0, args.length()-2);
                performanceInfo.registerEvent(methodName + "(" + a + ")", stopWatch.getTime());
            }
            catch (RuntimeException e)
            {
                /*
                 * FIXME This is a dirty hack to stop errors when no request is in progress.
                 * Need a better way of only intercepting during requests.
                 */
            }
        }
    }
}
