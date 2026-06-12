package com.cgb.order.service.impl;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.cgb.order.dao.AddressDao;
import com.cgb.order.entity.AddressEntity;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("收货地址服务测试")
class AddressServiceImplTest {

    @Mock
    private AddressDao addressDao;

    @InjectMocks
    private AddressServiceImpl addressService;

    @BeforeAll
    static void initMyBatisPlusCache() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), ""),
                AddressEntity.class);
    }

    // ========== 辅助方法 ==========

    private AddressEntity buildAddress(Long id, Long userId, Integer isDefault) {
        AddressEntity entity = new AddressEntity();
        entity.setId(id);
        entity.setUserid(userId);
        entity.setDizhimingchen("测试地址");
        entity.setLianxidianhua("13800138000");
        entity.setShouhuoren("张三");
        entity.setProvince("广东�?);
        entity.setCity("深圳�?);
        entity.setDistrict("南山�?);
        entity.setDetailAddress("科技园南�?�?);
        entity.setIsdefault(isDefault);
        return entity;
    }

    // ========== save 测试 ==========

    @Nested
    @DisplayName("保存地址")
    class SaveTests {

        @Test
        @DisplayName("保存地址 - 未指定默认标志时默认设为0")
        void save_noDefault_setToZero() {
            AddressEntity entity = buildAddress(null, 100L, null);
            when(addressDao.insert(any(AddressEntity.class))).thenReturn(1);

            addressService.save(entity);

            assertEquals(0, entity.getIsdefault(), "默认isdefault应为0");
            verify(addressDao).insert(entity);
        }

        @Test
        @DisplayName("保存地址 - 指定为默认地址时保�?)
        void save_withDefault_keepValue() {
            AddressEntity entity = buildAddress(null, 100L, 1);
            when(addressDao.insert(any(AddressEntity.class))).thenReturn(1);

            addressService.save(entity);

            assertEquals(1, entity.getIsdefault());
            verify(addressDao).insert(entity);
        }
    }

    // ========== update 测试 ==========

    @Nested
    @DisplayName("更新地址")
    class UpdateTests {

        @Test
        @DisplayName("更新地址 - 成功")
        void update_success() {
            AddressEntity entity = buildAddress(1L, 100L, 0);
            entity.setDetailAddress("新地址");
            when(addressDao.updateById(any(AddressEntity.class))).thenReturn(1);

            addressService.update(entity);

            verify(addressDao).updateById(entity);
        }
    }

    // ========== delete 测试 ==========

    @Nested
    @DisplayName("删除地址")
    class DeleteTests {

        @Test
        @DisplayName("删除地址 - 成功")
        void delete_success() {
            when(addressDao.deleteById(1L)).thenReturn(1);

            addressService.delete(1L);

            verify(addressDao).deleteById(1L);
        }
    }

    // ========== getById 测试 ==========

    @Nested
    @DisplayName("根据ID查询地址")
    class GetByIdTests {

        @Test
        @DisplayName("根据ID查询 - 存在")
        void getById_exists() {
            AddressEntity expected = buildAddress(1L, 100L, 1);
            when(addressDao.selectById(1L)).thenReturn(expected);

            AddressEntity result = addressService.getById(1L);

            assertNotNull(result);
            assertEquals("测试地址", result.getDizhimingchen());
            assertEquals(1, result.getIsdefault());
        }

        @Test
        @DisplayName("根据ID查询 - 不存在返回null")
        void getById_notFound_returnsNull() {
            when(addressDao.selectById(999L)).thenReturn(null);

            AddressEntity result = addressService.getById(999L);

            assertNull(result);
        }
    }

    // ========== setDefault 测试 ==========

    @Nested
    @DisplayName("设置默认地址")
    class SetDefaultTests {

        @Test
        @DisplayName("设置默认地址 - 先取消所有默认再设置新默�?)
        void setDefault_success() {
            when(addressDao.update(isNull(), any())).thenReturn(1, 1);

            addressService.setDefault(5L, 100L);

            // 应调用两次update: 第一次取消所有默�? 第二次设置新默认
            verify(addressDao, times(2)).update(isNull(), any());
        }
    }
}
