package com.btea.auroratimerserver.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.btea.auroratimerserver.dao.entity.NotificationsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 03:00
 * @Description: 通知数据库访问层接口
 */
@Mapper
public interface NotificationsMapper extends BaseMapper<NotificationsDO> {
}
