package com.cgb.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.user.entity.UserEntity;

public interface UserService {
    void save(UserEntity entity);
    void update(UserEntity entity);
    void delete(Long id);
    UserEntity getById(Long id);
    UserEntity getByUsername(String username);
    IPage<UserEntity> queryPage(UserEntity params);
}