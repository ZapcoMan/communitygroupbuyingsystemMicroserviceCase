package com.cgb.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.user.entity.MemberEntity;

public interface MemberService {
    void save(MemberEntity entity);
    void update(MemberEntity entity);
    void delete(Long id);
    MemberEntity getById(Long id);
    MemberEntity getByAccount(String account);
    IPage<MemberEntity> queryPage(MemberEntity params);
    R<?> login(String account, String password);
    R<?> register(MemberEntity params);
    R<?> changePassword(String token, String oldPassword, String newPassword);
    void addPoints(Long userId, Double points);
}
