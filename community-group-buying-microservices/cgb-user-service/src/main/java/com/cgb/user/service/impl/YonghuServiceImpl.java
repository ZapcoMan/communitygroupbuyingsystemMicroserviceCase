package com.cgb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
        // 账号查重
        YonghuEntity exist = getByZhanghao(entity.getZhanghao());
        if (exist != null) {
            throw new EIException(ErrorCode.USER_ALREADY_EXISTS);
        }
        // BCrypt 加密密码
        entity.setMima(passwordEncoder.encode(entity.getMima()));
        if (entity.getJifen() == null) entity.setJifen(0.0);
        if (entity.getYue() == null) entity.setYue(0.0);
        if (entity.getStatus() == null) entity.setStatus(0);
        yonghuDao.insert(entity);
    }

    @Override
    public void update(YonghuEntity entity) {
        if (entity.getId() == null) throw new EIException("用户ID不能为空");
        YonghuEntity old = getById(entity.getId());
        if (old == null) throw new EIException(ErrorCode.USER_NOT_FOUND);
        // 如果改了密码则重新加密
        if (entity.getMima() != null && !entity.getMima().equals(old.getMima())) {
            entity.setMima(passwordEncoder.encode(entity.getMima()));
        } else {
            entity.setMima(null); // 不更新密码
        }
        yonghuDao.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        yonghuDao.deleteById(id);
    }

    @Override
    public YonghuEntity getById(Long id) {
        YonghuEntity entity = yonghuDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.USER_NOT_FOUND);
        return entity;
    }

    @Override
    public YonghuEntity getByZhanghao(String zhanghao) {
        return yonghuDao.selectOne(new LambdaQueryWrapper<YonghuEntity>()
                .eq(YonghuEntity::getZhanghao, zhanghao));
    }

    @Override
    public IPage<YonghuEntity> queryPage(YonghuEntity params) {
        IPage<YonghuEntity> page = new Query<YonghuEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return yonghuDao.selectPage(page,
                new LambdaQueryWrapper<YonghuEntity>()
                        .like(CommonUtil.isNotEmpty(params.getZhanghao()),
                                YonghuEntity::getZhanghao, params.getZhanghao())
                        .like(CommonUtil.isNotEmpty(params.getXingming()),
                                YonghuEntity::getXingming, params.getXingming())
                        .orderByDesc(YonghuEntity::getId));
    }

    /**
     * 用户登录
     */
    public R<?> login(YonghuEntity params, String clientIP) {
        YonghuEntity user = getByZhanghao(params.getZhanghao());
        if (user == null) return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        if (user.getStatus() != null && user.getStatus() == 1) return R.fail(ErrorCode.USER_DISABLED);
        if (!passwordEncoder.matches(params.getMima(), user.getMima())) {
            return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        }
        // 生成 JWT
        String token = jwtUtils.generateToken(user.getId(), "user", clientIP);
        // 存储 Redis Session
        redisTokenService.saveToken(token, String.valueOf(user.getId()), "user", "yonghu");
        // 返回脱敏信息
        YonghuVO vo = new YonghuVO();
        BeanUtils.copyProperties(user, vo);
        return R.ok("登录成功", vo, token);
    }

    /**
     * 用户注册
     */
    public R<?> register(YonghuEntity params) {
        params.setMima(passwordEncoder.encode(params.getMima()));
        save(params);
        return R.ok("注册成功");
    }

    /**
     * 修改密码
     */
    @Override
    public R<?> changePassword(String token, String oldPassword, String newPassword) {
        if (token == null) return R.fail("未登录");
        Long userId = jwtUtils.getUserId(token);
        if (userId == null) return R.fail("Token无效");
        YonghuEntity user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getMima())) {
            return R.fail("旧密码错误");
        }
        YonghuEntity updateEntity = new YonghuEntity();
        updateEntity.setId(userId);
        updateEntity.setMima(passwordEncoder.encode(newPassword));
        yonghuDao.updateById(updateEntity);
        return R.ok("密码修改成功");
    }

    /**
     * 增加用户积分（订单支付成功后调用）
     */
    @Override
    public void addPoints(Long userId, Double points) {
        if (userId == null || points == null || points <= 0) {
            throw new EIException("积分参数无效");
        }
        int rows = yonghuDao.addPoints(userId, points);
        if (rows == 0) throw new EIException(ErrorCode.USER_NOT_FOUND);
        log.info("用户积分增加(原子操作): userId={}, points={}", userId, points);
    }
}