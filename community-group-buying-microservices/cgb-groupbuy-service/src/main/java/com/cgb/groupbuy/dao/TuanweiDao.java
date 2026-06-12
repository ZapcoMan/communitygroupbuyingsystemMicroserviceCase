package com.cgb.groupbuy.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.groupbuy.entity.TuanweiEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface TuanweiDao extends BaseMapper<TuanweiEntity> {

    /** 原子增加参团人数（并发安全） */
    @Update("UPDATE tuanwei SET xianxiarenshu = xianxiarenshu + #{count} WHERE id = #{id} AND xianxiarenshu < lirenjia AND zhuangtai = 0")
    int increaseMember(@Param("id") Long id, @Param("count") Integer count);

    /** 原子更新团购状态为已成团 */
    @Update("UPDATE tuanwei SET zhuangtai = 1 WHERE id = #{id} AND xianxiarenshu >= lirenjia AND zhuangtai = 0")
    int completeGroupBuy(@Param("id") Long id);

    /** 原子更新过期团购状态 */
    @Update("UPDATE tuanwei SET zhuangtai = 2 WHERE id = #{id} AND jieshushijian < NOW() AND zhuangtai = 0")
    int expireGroupBuy(@Param("id") Long id);
}
