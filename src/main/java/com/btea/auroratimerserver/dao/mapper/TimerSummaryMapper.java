package com.btea.auroratimerserver.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.btea.auroratimerserver.dao.entity.TimerSummaryDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 03:04
 * @Description: 时间统计数据库访问层接口
 */
@Mapper
public interface TimerSummaryMapper extends BaseMapper<TimerSummaryDO> {
}
