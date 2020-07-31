package com.jeesuite.mybatis.test.mapper;

import com.jeesuite.mybatis.core.BaseMapper;
import com.jeesuite.mybatis.plugin.cache.annotation.Cache;
import com.jeesuite.mybatis.test.entity.SnsAccounyBindingEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SnsAccounyBindingEntityMapper extends BaseMapper<SnsAccounyBindingEntity, Integer> {

    @Cache
    SnsAccounyBindingEntity findBySnsOpenId(@Param("snsType") String snsType, @Param("openId") String openId);

    @Cache
    SnsAccounyBindingEntity findByWxUnionIdAndOpenId(@Param("unionId") String unionId, @Param("openId") String openId);

    @Cache
    List<SnsAccounyBindingEntity> findByUnionId(@Param("unionId") String unionId);

    @Cache
    List<SnsAccounyBindingEntity> findByUserId(@Param("userId") int userId);

    @Cache
    String findWxUnionIdByUserId(@Param("userId") int userId);
}