package com.cgb.groupbuy.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cgb.groupbuy.entity.GroupSlotEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GroupSlotDao extends BaseMapper<GroupSlotEntity> {

    /** 原子增加参团人数（并发安全） */
    @Update("UPDATE group_slot SET current_member_count = current_member_count + #{count} WHERE id = #{id} AND current_member_count < target_member_count AND status = 0")
    int increaseMember(@Param("id") Long id, @Param("count") Integer count);

    /** 原子更新团购状态为已成团 */
    @Update("UPDATE group_slot SET status = 1 WHERE id = #{id} AND current_member_count >= target_member_count AND status = 0")
    int completeGroupBuy(@Param("id") Long id);

    /** 原子更新过期团购状态 */
    @Update("UPDATE group_slot SET status = 2 WHERE id = #{id} AND end_time < NOW() AND status = 0")
    int expireGroupBuy(@Param("id") Long id);
}
