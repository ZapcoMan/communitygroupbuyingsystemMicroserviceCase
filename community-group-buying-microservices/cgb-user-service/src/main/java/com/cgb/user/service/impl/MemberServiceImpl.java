package com.cgb.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.EIException;
import com.cgb.common.ErrorCode;
import com.cgb.common.R;
import com.cgb.common.utils.*;
import com.cgb.user.dao.MemberDao;
import com.cgb.user.entity.MemberEntity;
import com.cgb.user.entity.vo.MemberVO;
import com.cgb.user.service.MemberService;
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
public class MemberServiceImpl implements MemberService {

    private final MemberDao yonghuDao;
    private final RedisTokenService redisTokenService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void save(MemberEntity entity) {
        MemberEntity exist = getByAccount(entity.getAccount());
        if (exist != null) throw new EIException(ErrorCode.USER_ALREADY_EXISTS);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        if (entity.getPoints() == null) entity.setPoints(0.0);
        if (entity.getBalance() == null) entity.setBalance(0.0);
        if (entity.getStatus() == null) entity.setStatus(0);
        yonghuDao.insert(entity);
    }

    @Override
    public void update(MemberEntity entity) {
        if (entity.getId() == null) throw new EIException("用户ID不能为空");
        MemberEntity old = getById(entity.getId());
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
    public MemberEntity getById(Long id) {
        MemberEntity entity = yonghuDao.selectById(id);
        if (entity == null) throw new EIException(ErrorCode.USER_NOT_FOUND);
        return entity;
    }

    @Override
    public MemberEntity getByAccount(String account) {
        return yonghuDao.selectOne(new LambdaQueryWrapper<MemberEntity>()
                .eq(MemberEntity::getAccount, account));
    }

    @Override
    public IPage<MemberEntity> queryPage(MemberEntity params) {
        IPage<MemberEntity> page = new Query<MemberEntity>().getPage(
                CommonUtil.convert(params, Map.class));
        return yonghuDao.selectPage(page,
                new LambdaQueryWrapper<MemberEntity>()
                        .like(CommonUtil.isNotEmpty(params.getAccount()),
                                MemberEntity::getAccount, params.getAccount())
                        .like(CommonUtil.isNotEmpty(params.getRealName()),
                                MemberEntity::getRealName, params.getRealName())
                        .orderByDesc(MemberEntity::getId));
    }

    @Override
    public R<?> login(String account, String password) {
        MemberEntity user = getByAccount(account);
        if (user == null) return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        if (user.getStatus() != null && user.getStatus() == 1) return R.fail(ErrorCode.USER_DISABLED);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return R.fail(ErrorCode.USERNAME_PASSWORD_ERROR);
        }
        String token = jwtUtils.generateToken(user.getId(), "user", null);
        redisTokenService.saveToken(token, String.valueOf(user.getId()), "user", "yonghu");
        MemberVO vo = new MemberVO();
        BeanUtils.copyProperties(user, vo);
        return R.ok("登录成功", vo, token);
    }

    @Override
    public R<?> register(MemberEntity params) {
        params.setPassword(passwordEncoder.encode(params.getPassword()));
        save(params);
        return R.ok("注册成功");
    }

    @Override
    public R<?> changePassword(String token, String oldPassword, String newPassword) {
        if (token == null) return R.fail("未登录");
        Long userId = jwtUtils.getUserId(token);
        if (userId == null) return R.fail("Token无效");
        MemberEntity user = getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) return R.fail("旧密码错误");
        MemberEntity updateEntity = new MemberEntity();
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
