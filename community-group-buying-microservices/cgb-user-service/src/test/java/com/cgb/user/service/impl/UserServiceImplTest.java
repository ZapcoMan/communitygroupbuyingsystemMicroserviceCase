package com.cgb.user.service.impl;

import com.cgb.common.EIException;
import com.cgb.common.R;
import com.cgb.user.dao.UserDao;
import com.cgb.user.entity.UserEntity;
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
 * UserServiceImpl 单元测试
 * 覆盖管理员 CRUD 和登录业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl - 管理员服务测试")
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RedisTokenService redisTokenService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ======================== save ========================

    @Nested
    @DisplayName("save - 新增管理员")
    class SaveTests {

        @Test
        @DisplayName("正常保存，密码被 BCrypt 加密")
        void save_newUser_encodesPassword() {
            when(userDao.selectOne(any())).thenReturn(null);
            when(userDao.insert(any(UserEntity.class))).thenReturn(1);

            UserEntity entity = new UserEntity();
            entity.setUsername("admin1");
            entity.setPassword("plain123");

            userService.save(entity);

            // 密码已被加密，不再是明文
            assertNotEquals("plain123", entity.getPassword());
            assertTrue(passwordEncoder.matches("plain123", entity.getPassword()));
            verify(userDao).insert(entity);
        }

        @Test
        @DisplayName("用户名已存在，抛出 EIException")
        void save_duplicateUsername_throwsException() {
            UserEntity existing = new UserEntity();
            existing.setUsername("admin1");
            when(userDao.selectOne(any())).thenReturn(existing);

            UserEntity entity = new UserEntity();
            entity.setUsername("admin1");
            entity.setPassword("pass");

            EIException ex = assertThrows(EIException.class, () -> userService.save(entity));
            assertEquals(409, ex.getCode());
        }

        @Test
        @DisplayName("未指定角色时默认为 admin")
        void save_noRole_defaultsToAdmin() {
            when(userDao.selectOne(any())).thenReturn(null);
            when(userDao.insert(any(UserEntity.class))).thenReturn(1);

            UserEntity entity = new UserEntity();
            entity.setUsername("newadmin");
            entity.setPassword("pass");

            userService.save(entity);

            assertEquals("admin", entity.getRole());
        }
    }

    // ======================== update ========================

    @Nested
    @DisplayName("update - 更新管理员")
    class UpdateTests {

        @Test
        @DisplayName("ID 为空时抛出异常")
        void update_nullId_throwsException() {
            UserEntity entity = new UserEntity();
            assertThrows(EIException.class, () -> userService.update(entity));
        }

        @Test
        @DisplayName("用户不存在时抛出异常")
        void update_userNotFound_throwsException() {
            when(userDao.selectById(99L)).thenReturn(null);

            UserEntity entity = new UserEntity();
            entity.setId(99L);

            EIException ex = assertThrows(EIException.class, () -> userService.update(entity));
            assertEquals(404, ex.getCode());
        }

        @Test
        @DisplayName("密码变更时重新加密")
        void update_passwordChanged_reEncodes() {
            UserEntity old = new UserEntity();
            old.setId(1L);
            old.setPassword("oldHashedPassword");
            when(userDao.selectById(1L)).thenReturn(old);
            when(userDao.updateById(any(UserEntity.class))).thenReturn(1);

            UserEntity entity = new UserEntity();
            entity.setId(1L);
            entity.setPassword("newPassword123");

            userService.update(entity);

            assertTrue(passwordEncoder.matches("newPassword123", entity.getPassword()));
        }

        @Test
        @DisplayName("密码未变更时不更新密码字段")
        void update_passwordUnchanged_setsNull() {
            UserEntity old = new UserEntity();
            old.setId(1L);
            old.setPassword("samePassword");
            when(userDao.selectById(1L)).thenReturn(old);
            when(userDao.updateById(any(UserEntity.class))).thenReturn(1);

            UserEntity entity = new UserEntity();
            entity.setId(1L);
            entity.setPassword("samePassword");

            userService.update(entity);

            assertNull(entity.getPassword());
        }
    }

    // ======================== delete ========================

    @Nested
    @DisplayName("delete - 删除管理员")
    class DeleteTests {

        @Test
        @DisplayName("正常删除")
        void delete_validId_callsDeleteById() {
            when(userDao.deleteById(1L)).thenReturn(1);
            userService.delete(1L);
            verify(userDao).deleteById(1L);
        }
    }

    // ======================== getById ========================

    @Nested
    @DisplayName("getById - 根据ID查询")
    class GetByIdTests {

        @Test
        @DisplayName("正常返回用户实体")
        void getById_exists_returnsEntity() {
            UserEntity expected = new UserEntity();
            expected.setId(1L);
            when(userDao.selectById(1L)).thenReturn(expected);

            UserEntity result = userService.getById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
        }

        @Test
        @DisplayName("用户不存在返回 null")
        void getById_notExists_returnsNull() {
            when(userDao.selectById(999L)).thenReturn(null);
            assertNull(userService.getById(999L));
        }
    }

    // ======================== login ========================

    @Nested
    @DisplayName("login - 管理员登录")
    class LoginTests {

        @Test
        @DisplayName("用户名不存在，返回错误")
        void login_userNotFound_returnsFail() {
            when(userDao.selectOne(any())).thenReturn(null);

            UserEntity params = new UserEntity();
            params.setUsername("nonexist");
            params.setPassword("pass");

            R<?> result = userService.login(params, "127.0.0.1");

            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("密码错误，返回错误")
        void login_wrongPassword_returnsFail() {
            UserEntity user = new UserEntity();
            user.setId(1L);
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("correctPassword"));
            user.setRole("admin");
            when(userDao.selectOne(any())).thenReturn(user);

            UserEntity params = new UserEntity();
            params.setUsername("admin");
            params.setPassword("wrongPassword");

            R<?> result = userService.login(params, "127.0.0.1");

            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("登录成功，返回 Token 和用户信息")
        void login_success_returnsTokenAndUser() {
            UserEntity user = new UserEntity();
            user.setId(1L);
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setRole("admin");
            when(userDao.selectOne(any())).thenReturn(user);
            when(jwtUtils.generateToken(eq(1L), eq("admin"), eq("10.0.0.1"))).thenReturn("jwt-token-xyz");

            UserEntity params = new UserEntity();
            params.setUsername("admin");
            params.setPassword("admin123");

            R<?> result = userService.login(params, "10.0.0.1");

            assertEquals(0, result.getCode());
            assertEquals("登录成功", result.getMsg());
            assertEquals("jwt-token-xyz", result.getToken());
            assertNotNull(result.getData());
            // 密码应被清空
            UserEntity returnedUser = (UserEntity) result.getData();
            assertNull(returnedUser.getPassword());
        }

        @Test
        @DisplayName("登录成功后 Token 被保存到 Redis")
        void login_success_savesTokenToRedis() {
            UserEntity user = new UserEntity();
            user.setId(1L);
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("pass"));
            user.setRole("admin");
            when(userDao.selectOne(any())).thenReturn(user);
            when(jwtUtils.generateToken(anyLong(), anyString(), anyString())).thenReturn("token-abc");

            UserEntity params = new UserEntity();
            params.setUsername("admin");
            params.setPassword("pass");

            userService.login(params, "127.0.0.1");

            verify(redisTokenService).saveToken("token-abc", "1", "admin", "users");
        }
    }
}
