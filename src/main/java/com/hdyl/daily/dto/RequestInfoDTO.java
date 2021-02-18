package com.hdyl.daily.dto;

import lombok.Data;

/**
 * @author zhuangruitao
 * @Title: RequestInfoDTO
 * @ProjectName hmr-daily-sdk
 * @date 2021/2/18 17:58
 */

@Data
public class RequestInfoDTO {

    //请求头的token
    private String reqToken;
    //请求链接
    private String reqUrl;
    //请求方法
    private String reqMethod;
    //请求Ip
    private String reqIp;
    //请求类名
    private String reqTargetClassName;
    //请求参数
    private String reqArgs;
    //请求响应结果
    private String reqResultJson;
    //方法执行时间
    private Long cosTime;
    //请求方法描述
    private String reqMethodDescription;
    //请求是否成功
    private Byte isSuccess;
    //异常信息
    private String exception;
    //链路Id
    private String traceId;
}
