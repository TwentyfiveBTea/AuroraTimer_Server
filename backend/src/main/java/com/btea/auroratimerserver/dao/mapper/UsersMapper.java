package com.btea.auroratimerserver.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.btea.auroratimerserver.dao.entity.UsersDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 02:58
 * @Description: 用户数据库访问层接口
 */
@Mapper
public interface UsersMapper extends BaseMapper<UsersDO> {
}
