package com.cgb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.R;
import com.cgb.common.utils.*;
import com.cgb.user.dao.UserDao;
import com.cgb.user.entity.UserEntity;
import com.cgb.user.service.UserService;
import com.cgb.user.service.RedisTokenService;
import com.cgb.user.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final RedisTokenService redisTokenService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void save(UserEntity entity) {
        UserEntity exist = getByUsername(entity.getUsername());
        if (exist != null) throw new EIException(ErrorCode.USER_ALREADY_EXISTS);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        if (entity.getRole() == null) entity.setRole("admin");
        userDao.insert(entity);
    }

    @Override
    public void update(UserEntity entity) {
        if (entity.getId() == null) throw new EIException("用户ID不能为空");
        UserEntity old = getById(entity.getId());
        if (old == null) throw new EIException(ErrorCode.USER_NOT_FOUND);
        if (entity.getPassword() != null && !entity.getPassword().equals(old.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        } else {
            entity.setPassword(null);
        }
        userDao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public UserEntity getById(Long id) {
        return userDao.selectById(id);
    }

    @Override
    public UserEntity getByUsername(String username) {
        return userDao.selectOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUsername, username));
    }

    @Override
    public IPage<UserEntity> queryPage(UserEntity params) {
        IPage<UserEntity> page = new Query<UserEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return userDao.selectPage(page,
                new LambdaQueryWrapper<UserEntity>()
                        .like(CommonUtil.isNotEmpty(params.getUsername()),
                                UserEntity::getUsername, params.getUsername())
                        .orderByDesc(UserEntity::getId));
    }

    /**
     * 管理员登录
     */
    public R<?> login(UserEntity params, String clientIP) {
        UserEntity user = getByUsername(params.getUsername());
        if (user == null) return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        if (!passwordEncoder.matches(params.getPassword(), user.getPassword())) {
            return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        }
        String token = jwtUtils.generateToken(user.getId(), user.getRole(), clientIP);
        redisTokenService.saveToken(token, String.valueOf(user.getId()), user.getRole(), "users");
        user.setPassword(null);
        return R.ok("登录成功", user, token);
    }
}