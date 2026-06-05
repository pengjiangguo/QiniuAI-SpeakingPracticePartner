package com.speakingpractice.partner.infrastructure.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.speakingpractice.partner.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Repository
 */
@Mapper
public interface UserRepository extends BaseMapper<User> {

}
