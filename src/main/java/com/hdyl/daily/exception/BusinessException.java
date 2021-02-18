package com.hdyl.daily.exception;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author zhuangruitao
 * @Title: BusinessException
 * @ProjectName hmr-daily-sdk
 * @date 2021/2/18 19:05
 */
public class BusinessException extends  RuntimeException{
    private static final long serialVersionUID = 1884511028503884702L;
    private String code;
    private String description;
    private String classId;
    /**
     * 是否入库记录
     */
    private boolean isRecord = false;

    public BusinessException(String code, String description, boolean isRecord, Throwable cause) {
        super(description, cause);
        this.code = code;
        this.description = description;
        this.isRecord = isRecord;
        if (null != cause) {
            this.classId = getClassIdName(cause.getStackTrace()[0].getClassName());
        }
    }

    public BusinessException(String code, String description, boolean isRecord) {
        this(code, description, isRecord, null);
    }

    public BusinessException(String code, String description) {
        this(code, description, false);
    }

    public BusinessException(String description, boolean isRecord) {
        this(null, description, isRecord);
    }

    public BusinessException(String description) {
        this(description, false);
    }

    public BusinessException(String description, boolean isRecord, Throwable cause) {
        this(null, description, isRecord, cause);
    }

    public BusinessException(String description, Throwable cause) {
        this(description, false, cause);
    }

    public BusinessException(Throwable cause) {
        this(null, cause);
    }

    /**
     * 根据完整类名(含包名)截取类名
     *
     * @param className
     * @return
     */
    private static String getClassIdName(String className) {
        int index = className.lastIndexOf(".");
        if (index < 0) {
            return className;
        }
        return className.substring(index + 1);
    }


    public boolean getIsRecord() {
        return this.isRecord;
    }

    public void setIsRecord(boolean isRecord) {
        this.isRecord = isRecord;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
