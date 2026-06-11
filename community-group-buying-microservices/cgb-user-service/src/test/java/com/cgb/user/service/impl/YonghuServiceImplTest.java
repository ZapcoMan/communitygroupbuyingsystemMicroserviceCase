package com.cgb.user.service.impl;

import com.cgb.common.EIException;
import com.cgb.common.R;
import com.cgb.user.dao.YonghuDao;
import com.cgb.user.entity.YonghuEntity;
import com.cgb.user.service.RedisTokenService;
import com.cgb.user.utils.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * YonghuServiceImpl 单元测试
 * 覆盖用户 CRUD、登录、注册业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("YonghuServiceImpl - 用户服务测试")
class YonghuServiceImplTest {

    @Mock
    private YonghuDao yonghuDao;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private YonghuServiceImpl yonghuService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ======================== save ========================

    @Nested
    @DisplayName("save - 新增用户")
    class SaveTests {

        @Test
        @DisplayName("正常保存，密码被加密，默认值被设置")
        void save_newUser_encodesPasswordAndSetsDefaults() {
            when(yonghuDao.selectOne(any())).thenReturn(null);
            when(yonghuDao.insert(any(YonghuEntity.class))).thenReturn(1);

            YonghuEntity entity = new YonghuEntity();
            entity.setZhanghao("testuser");
            entity.setMima("plain123");

            yonghuService.save(entity);

            assertNotEquals("plain123", entity.getMima());
            assertTrue(passwordEncoder.matches("plain123", entity.getMima()));
            assertEquals(0.0, entity.getJifen());
            assertEquals(0.0, entity.getYue());
            assertEquals(0, entity.getStatus());
            verify(yonghuDao).insert(entity);
        }

        @Test
        @DisplayName("账号已存在，抛出 EIException")
        void save_duplicateAccount_throwsException() {
            YonghuEntity existing = new YonghuEntity();
            existing.setZhanghao("testuser");
            when(yonghuDao.selectOne(any())).thenReturn(existing);

            YonghuEntity entity = new YonghuEntity();
            entity.setZhanghao("testuser");
            entity.setMima("pass");

            EIException ex = assertThrows(EIException.class, () -> yonghuService.save(entity));
            assertEquals(409, ex.getCode());
        }

        @Test
        @DisplayName("已设置积分和余额时不覆盖默认值")
        void save_withExistingValues_keepsDefaults() {
            when(yonghuDao.selectOne(any())).thenReturn(null);
            when(yonghuDao.insert(any(YonghuEntity.class))).thenReturn(1);

            YonghuEntity entity = new YonghuEntity();
            entity.setZhanghao("testuser");
            entity.setMima("pass");
            entity.setJifen(100.0);
            entity.setYue(500.0);
            entity.setStatus(1);

            yonghuService.save(entity);

            assertEquals(100.0, entity.getJifen());
            assertEquals(500.0, entity.getYue());
            assertEquals(1, entity.getStatus());
        }
    }

    // ======================== update ========================

    @Nested
    @DisplayName("update - 更新用户")
    class UpdateTests {

        @Test
        @DisplayName("ID 为空时抛出异常")
        void update_nullId_throwsException() {
            YonghuEntity entity = new YonghuEntity();
            assertThrows(EIException.class, () -> yonghuService.update(entity));
        }

        @Test
        @DisplayName("用户不存在时抛出异常")
        void update_userNotFound_throwsException() {
            when(yonghuDao.selectById(99L)).thenReturn(null);

            YonghuEntity entity = new YonghuEntity();
            entity.setId(99L);

            EIException ex = assertThrows(EIException.class, () -> yonghuService.update(entity));
            assertEquals(404, ex.getCode());
        }

        @Test
        @DisplayName("密码变更时重新加密")
        void update_passwordChanged_reEncodes() {
            YonghuEntity old = new YonghuEntity();
            old.setId(1L);
            old.setMima("oldHashedPassword");
            when(yonghuDao.selectById(1L)).thenReturn(old);
            when(yonghuDao.updateById(any(YonghuEntity.class))).thenReturn(1);

            YonghuEntity entity = new YonghuEntity();
            entity.setId(1L);
            entity.setMima("newPassword123");

            yonghuService.update(entity);

            assertTrue(passwordEncoder.matches("newPassword123", entity.getMima()));
        }

        @Test
        @DisplayName("密码未变更时不更新密码字段")
        void update_passwordUnchanged_setsNull() {
            YonghuEntity old = new YonghuEntity();
            old.setId(1L);
            old.setMima("samePassword");
            when(yonghuDao.selectById(1L)).thenReturn(old);
            when(yonghuDao.updateById(any(YonghuEntity.class))).thenReturn(1);

            YonghuEntity entity = new YonghuEntity();
            entity.setId(1L);
            entity.setMima("samePassword");

            yonghuService.update(entity);

            assertNull(entity.getMima());
        }
    }

    // ======================== delete ========================

    @Nested
    @DisplayName("delete - 删除用户")
    class DeleteTests {

        @Test
        @DisplayName("正常删除")
        void delete_validId_callsDeleteById() {
            when(yonghuDao.deleteById(1L)).thenReturn(1);
            yonghuService.delete(1L);
            verify(yonghuDao).deleteById(1L);
        }
    }

    // ======================== getById ========================

    @Nested
    @DisplayName("getById - 根据ID查询")
    class GetByIdTests {

        @Test
        @DisplayName("正常返回用户实体")
        void getById_exists_returnsEntity() {
            YonghuEntity expected = new YonghuEntity();
            expected.setId(1L);
            when(yonghuDao.selectById(1L)).thenReturn(expected);

            YonghuEntity result = yonghuService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("用户不存在时抛出异常")
        void getById_notExists_throwsException() {
            when(yonghuDao.selectById(999L)).thenReturn(null);

            EIException ex = assertThrows(EIException.class, () -> yonghuService.getById(999L));
            assertEquals(404, ex.getCode());
        }
    }

    // ======================== login ========================

    @Nested
    @DisplayName("login - 用户登录")
    class LoginTests {

        @Test
        @DisplayName("账号不存在，返回错误")
        void login_accountNotFound_returnsFail() {
            when(yonghuDao.selectOne(any())).thenReturn(null);

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("nonexist");
            params.setMima("pass");

            R<?> result = yonghuService.login(params, "127.0.0.1");

            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("账号被禁用，返回错误")
        void login_accountDisabled_returnsFail() {
            YonghuEntity user = new YonghuEntity();
            user.setId(1L);
            user.setZhanghao("testuser");
            user.setMima(passwordEncoder.encode("pass"));
            user.setStatus(1);
            when(yonghuDao.selectOne(any())).thenReturn(user);

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("testuser");
            params.setMima("pass");

            R<?> result = yonghuService.login(params, "127.0.0.1");

            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("密码错误，返回错误")
        void login_wrongPassword_returnsFail() {
            YonghuEntity user = new YonghuEntity();
            user.setId(1L);
            user.setZhanghao("testuser");
            user.setMima(passwordEncoder.encode("correct"));
            user.setStatus(0);
            when(yonghuDao.selectOne(any())).thenReturn(user);

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("testuser");
            params.setMima("wrong");

            R<?> result = yonghuService.login(params, "127.0.0.1");

            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("登录成功，返回 Token 和脱敏用户信息")
        void login_success_returnsTokenAndVO() {
            YonghuEntity user = new YonghuEntity();
            user.setId(1L);
            user.setZhanghao("testuser");
            user.setMima(passwordEncoder.encode("pass123"));
            user.setXingming("张三");
            user.setStatus(0);
            when(yonghuDao.selectOne(any())).thenReturn(user);
            when(jwtUtils.generateToken(eq(1L), eq("user"), eq("10.0.0.1"))).thenReturn("jwt-abc");

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("testuser");
            params.setMima("pass123");

            R<?> result = yonghuService.login(params, "10.0.0.1");

            assertEquals(0, result.getCode());
            assertEquals("登录成功", result.getMsg());
            assertEquals("jwt-abc", result.getToken());
            assertNotNull(result.getData());
        }

        @Test
        @DisplayName("登录成功后 Token 保存到 Redis")
        void login_success_savesTokenToRedis() {
            YonghuEntity user = new YonghuEntity();
            user.setId(1L);
            user.setZhanghao("testuser");
            user.setMima(passwordEncoder.encode("pass"));
            user.setStatus(0);
            when(yonghuDao.selectOne(any())).thenReturn(user);
            when(jwtUtils.generateToken(anyLong(), anyString(), anyString())).thenReturn("token-xyz");

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("testuser");
            params.setMima("pass");

            yonghuService.login(params, "127.0.0.1");

            verify(redisTokenService).saveToken("token-xyz", "1", "user", "yonghu");
        }

        @Test
        @DisplayName("status 为 null 时不影响登录")
        void login_statusNull_allowsLogin() {
            YonghuEntity user = new YonghuEntity();
            user.setId(1L);
            user.setZhanghao("testuser");
            user.setMima(passwordEncoder.encode("pass"));
            user.setStatus(null);
            when(yonghuDao.selectOne(any())).thenReturn(user);
            when(jwtUtils.generateToken(anyLong(), anyString(), anyString())).thenReturn("token");

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("testuser");
            params.setMima("pass");

            R<?> result = yonghuService.login(params, "127.0.0.1");

            assertEquals(0, result.getCode());
        }
    }

    // ======================== register ========================

    @Nested
    @DisplayName("register - 用户注册")
    class RegisterTests {

        @Test
        @DisplayName("注册成功")
        void register_newUser_returnsSuccess() {
            when(yonghuDao.selectOne(any())).thenReturn(null);
            when(yonghuDao.insert(any(YonghuEntity.class))).thenReturn(1);

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("newuser");
            params.setMima("password123");

            R<?> result = yonghuService.register(params);

            assertEquals(0, result.getCode());
            assertEquals("注册成功", result.getMsg());
            verify(yonghuDao).insert(any(YonghuEntity.class));
        }

        @Test
        @DisplayName("注册账号已存在，抛出异常")
        void register_duplicateAccount_throwsException() {
            YonghuEntity existing = new YonghuEntity();
            existing.setZhanghao("existuser");
            when(yonghuDao.selectOne(any())).thenReturn(existing);

            YonghuEntity params = new YonghuEntity();
            params.setZhanghao("existuser");
            params.setMima("pass");

            assertThrows(EIException.class, () -> yonghuService.register(params));
        }
    }
}
