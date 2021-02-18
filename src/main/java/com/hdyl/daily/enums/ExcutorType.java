package com.hdyl.daily.enums;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Author:zrt
 * @Date:2021/2/18 22:37
 * @version:1.0
 */

@ApiModel("数据源枚举类")
public enum ExcutorType {
    MYSQL,MONGO,ES;
}
