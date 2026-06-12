package com.cgb.groupbuy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.groupbuy.entity.GroupBuyCommentEntity;

public interface GroupBuyCommentService {
    void save(GroupBuyCommentEntity entity);
    void update(GroupBuyCommentEntity entity);
    void delete(Long id);
    IPage<GroupBuyCommentEntity> queryPage(GroupBuyCommentEntity params);
}
