package com.speakingpractice.partner.vocabulary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.speakingpractice.partner.vocabulary.domain.entity.Vocabulary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 词汇 Mapper
 */
@Mapper
public interface VocabularyMapper extends BaseMapper<Vocabulary> {

}