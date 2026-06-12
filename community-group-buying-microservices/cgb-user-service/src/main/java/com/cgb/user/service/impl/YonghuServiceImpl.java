package com.cgb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.R;
import com.cgb.common.utils.*;
import com.cgb.user.dao.YonghuDao;
import com.cgb.user.entity.YonghuEntity;
import com.cgb.user.entity.vo.YonghuVO;
import com.cgb.user.service.YonghuService;
import com.cgb.user.service.RedisTokenService;
import com.cgb.user.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class YonghuServiceImpl implements YonghuService {

    private final YonghuDao yonghuDao;
    private final RedisTokenService redisTokenService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void save(YonghuEntity entity) {
        YonghuEntity exist = getByAccount(entity.getAccount());
        if (exist != null) throw new EIException(ErrorCode.USER_ALREADY_EXISTS);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        if (entity.getPoints() == null) entity.setPoints(0.0);
        if (entity.getBalance() == null) entity.setBalance(0.0);
        if (entity.getStatus() == null) entity.setStatus(0);
        yonghuDao.insert(entity);
    }

    @Override
    public void update(YonghuEntity entity) {
        if (entity.getId() == null) throw new EIException("用户ID不能为空");
        YonghuEntity old = getById(entity.getId());
        if (old == null) throw new EIException(ErrorCode.USER_NOT_FOUND);
        if (entity.getPassword() != null && !entity.getPassword().equals(old.getPassword())) {
            entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        } else {
            entity.setPassword(null);
        }
        yonghuDao.updateById(entity);
    }

    @Override
    public void delete(Long id) { yonghuDao.deleteById(id); }

    @Override
    public YonghuEntity getById(Long id) {
        YonghuEntity entity = yonghuDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.USER_NOT_FOUND);
        return entity;
    }

    @Override
    public YonghuEntity getByAccount(String account) {
        return yonghuDao.selectOne(new LambdaQueryWrapper<YonghuEntity>()
                .eq(YonghuEntity::getAccount, account));
    }

    @Override
    public IPage<YonghuEntity> queryPage(YonghuEntity params) {
        IPage<YonghuEntity> page = new Query<YonghuEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return yonghuDao.selectPage(page,
                new LambdaQueryWrapper<YonghuEntity>()
                        .like(CommonUtil.isNotEmpty(params.getAccount()),
                                YonghuEntity::getAccount, params.getAccount())
                        .like(CommonUtil.isNotEmpty(params.getRealName()),
                                YonghuEntity::getRealName, params.getRealName())
                        .orderByDesc(YonghuEntity::getId));
    }

    @Override
    public R<?> login(String account, String password) {
        YonghuEntity user = getByAccount(account);
        if (user == null) return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        if (user.getStatus() != null && user.getStatus() == 1) return R.fail(ErrorCode.USER_DISABLED);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        }
        String token = jwtUtils.generateToken(user.getId(), "user", null);
        redisTokenService.saveToken(token, String.valueOf(user.getId()), "user", "yonghu");
        YonghuVO vo = new YonghuVO();
        BeanUtils.copyProperties(user, vo);
        return R.ok("登录成功", vo, token);
    }

    @Override
    public R<?> register(YonghuEntity params) {
        params.setPassword(passwordEncoder.encode(params.getPassword()));
        save(params);
        return R.ok("注册成功");
    }

    @Override
    public R<?> changePassword(String token, String oldPassword, String newPassword) {
        if (token == null) return R.fail("未登录");
        Long userId = jwtUtils.getUserId(token);
        if (userId == null) return R.fail("Token无效");
        YonghuEntity user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) return R.fail("旧密码错误");
        YonghuEntity updateEntity = new YonghuEntity();
        updateEntity.setId(userId);
        updateEntity.setPassword(passwordEncoder.encode(newPassword));
        yonghuDao.updateById(updateEntity);
        return R.ok("密码修改成功");
    }

    @Override
    public void addPoints(Long userId, Double points) {
        if (userId == null || points == null || points <= 0) throw new EIException("积分参数无效");
        int rows = yonghuDao.addPoints(userId, points);
        if (rows == 0) throw new EIException(ErrorCode.USER_NOT_FOUND);
        log.info("用户积分增加(原子操作): userId={}, points={}", userId, points);
    }
}
