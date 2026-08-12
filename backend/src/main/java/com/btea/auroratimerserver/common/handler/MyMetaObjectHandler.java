package com.btea.auroratimerserver.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/02/08 03:10
 * @Description: 插入和更新时的填充策略
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    // 插入时的填充策略
    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入新数据时，更新 “创建时间 ”和 “修改时间 ”为当前时间
        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);

    }

    // 更新时的填充策略
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更修数据时，更新 “修改时间 ”为当前时间
        this.setFieldValByName("updateTime", new Date(), metaObject);
    }
}
