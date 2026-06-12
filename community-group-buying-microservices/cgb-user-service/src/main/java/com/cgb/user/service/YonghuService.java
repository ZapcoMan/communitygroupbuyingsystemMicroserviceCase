package com.cgb.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.user.entity.YonghuEntity;

public interface YonghuService {
    void save(YonghuEntity entity);
    void update(YonghuEntity entity);
    void delete(Long id);
    YonghuEntity getById(Long id);
    YonghuEntity getByAccount(String account);
    IPage<YonghuEntity> queryPage(YonghuEntity params);
    R<?> login(String account, String password);
    R<?> register(YonghuEntity params);
    R<?> changePassword(String token, String oldPassword, String newPassword);
    void addPoints(Long userId, Double points);
}
