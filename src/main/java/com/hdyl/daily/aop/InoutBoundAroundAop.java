package com.hdyl.daily.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdyl.daily.dto.RequestInfoDTO;
import com.hdyl.daily.enums.ExcutorType;
import com.hdyl.daily.exception.BusinessException;
import com.sun.corba.se.spi.orbutil.threadpool.ThreadPoolManager;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author zhuangruitao
 * @Title: InoutBoundAroundAop
 * @date 2021/2/18 15:43
 */
@Slf4j
@Data
@Component
@ConfigurationProperties(prefix = "aop.init.config")
@Aspect
public class InoutBoundAroundAop {

    //用于保存链路Id
    private static ThreadLocal<String> threadLocal=new ThreadLocal<>();
    @Autowired
    private MongoTemplate mongoTemplate;

    //controller初始化参数
    private String  cInitProperty;

    //server层切面初始化参数
    private String sInitProperty;

    //请求的token
    private String reqToken;

    //拼接切点
    @Pointcut("execution(* getCInitProperty())")
    public void controllerCut(){}

    //拼接切点
    @Pointcut("execution(* getSInitProperty())")
    public void serverCut(){}

    /**
     * 出入站日志环绕处理
     *
     * @param joinPoint	 横向切入点
     * @return java.lang.Object
     * @author zhuangruitao
     * @date 2021/2/18 16:26
     */
    @Around("controllerCut()||serverCut()")
    public Object aroundHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取SpringMVC拦截的请求参数
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //判断当前的请求参数是否为null
        if (ObjectUtil.isNotNull(servletRequestAttributes)) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //请求链接
            String reqUrl = request.getRequestURL().toString();
            //请求方法
            String reqMethod=null;
            //请求Ip
            String reqIp = request.getRemoteHost();
            //请求类名
            String reqClassName=null;
            //请求参数
            String reqArgs=null;
            //请求响应如果
            String reqResultJson=null;
            //请求方法描述
            String reqMethodDescription=null;
            //方法执行时间
            long cosTime = 0L;
            //定义链路Id
            String traceId = RandomUtil.randomString(12);
            //初始化请求开始时间
            Long starTime = System.currentTimeMillis();
            //初始化请求参数
            RequestInfoDTO requestInfoDTO = new RequestInfoDTO();
            try {
                //获取方法签名
                Signature signature = joinPoint.getSignature();
                //保存链路Id
                threadLocal.set(traceId);
                //赋值请求方法
                reqMethod = signature.getName();
                //设置DTO的请求方法
                requestInfoDTO.setReqMethod(reqMethod);
                //赋值请求className
                reqClassName = signature.getDeclaringTypeName();
                //赋值请求参数
                Object[] args = joinPoint.getArgs();
                //请求参数赋值json格式的数据
                reqArgs = JSON.toJSONString(args);
                if (ObjectUtil.isNotNull(reqArgs)) {
                    Class<?>[] argTypes = new Class[args.length];
                    for (int i = 0; i < args.length; i++) {
                        argTypes[i] = args[i].getClass();
                    }
                    //获取请求方法
                    Method method = joinPoint.getTarget().getClass().getMethod(reqMethod, argTypes);
                    //获取方法注解
                    ApiOperation annotation = method.getAnnotation(ApiOperation.class);
                    if (ObjectUtil.isNotNull(annotation)) {
                        reqMethodDescription = annotation.value();
                    }
                }
                log.info(">>>>>>请求入站:{}, " + "请求类名:{}, " + "请求地址:{}, " + "请求方法:{}, " + "请求参数:{}, " + "请求ip:{}, " + "认证Token:{}", threadLocal.get(), reqClassName, reqUrl, reqMethod, reqArgs, reqIp);
                // 方法执行
                Object result = joinPoint.proceed();
                // 初始化请求结束时间
                reqResultJson = JSON.toJSONString(result);
                // 计算方法调用耗时
                cosTime = System.currentTimeMillis() - starTime;
                log.info("<<<<<<请求出站:{}, 请求结果:{}, 请求响应时间:{}",
                        threadLocal.get(),
                        reqResultJson,
                        cosTime
                );
                requestInfoDTO.setIsSuccess(Byte.valueOf("1"));
                // 返回执行结果
                return result;
            } catch (Exception e) {
                String exJson = JSON.toJSONString(e);
                log.error("!!!!!!方法调用异常:{}, " + "请求类名:{}, " + "请求地址:{}, " + "请求方法:{}, " + "请求参数:{}, " + "请求结果:{}, " + "请求响应时间:{}, " + "请求ip:{}, " + "认证Token:{}, " + "异常原因:{}", threadLocal.get(), reqClassName, reqUrl, reqMethod, reqArgs, reqResultJson, cosTime, reqIp, exJson);
                if (e instanceof Exception) {
                    JSONObject exObj = new JSONObject();
                    exObj.put("code", ((BusinessException) e).getCode());
                    exObj.put("msg", ((BusinessException) e).getDescription());
                    requestInfoDTO.setException(exObj.toJSONString());
                }
                throw e;
            } finally {
                // 链路id必须释放,否则后续线程可能出现共享链路id导致日志错乱的情况
                threadLocal.remove();
                requestInfoDTO.setReqToken(reqToken);
                requestInfoDTO.setReqUrl(reqUrl);
                requestInfoDTO.setReqMethod(reqMethod);
                requestInfoDTO.setReqMethodDescription(reqMethodDescription);
                requestInfoDTO.setReqIp(reqIp);
                requestInfoDTO.setReqTargetClassName(reqClassName);
                requestInfoDTO.setReqArgs(reqArgs);
                requestInfoDTO.setTraceId(traceId);
                requestInfoDTO.setCosTime(cosTime);
                requestInfoDTO.setReqResultJson(reqResultJson);
                //将链路信息存储到Mongo中
                mongoTemplate.save(requestInfoDTO,"trace_daily");
            }
        } else {
            return joinPoint.proceed();
        }
    }
}
