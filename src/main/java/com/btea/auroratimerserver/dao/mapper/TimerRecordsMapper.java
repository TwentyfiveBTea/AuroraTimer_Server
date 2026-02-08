package com.btea.auroratimerserver.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.btea.auroratimerserver.dao.entity.TimerRecordsDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 03:01
 * @Description: 时间记录数据库访问层接口
 */
@Mapper
public interface TimerRecordsMapper extends BaseMapper<TimerRecordsDO> {
}
