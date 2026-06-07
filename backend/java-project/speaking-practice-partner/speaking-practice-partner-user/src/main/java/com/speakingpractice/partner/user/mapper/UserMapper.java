package com.speakingpractice.partner.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.speakingpractice.partner.user.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}