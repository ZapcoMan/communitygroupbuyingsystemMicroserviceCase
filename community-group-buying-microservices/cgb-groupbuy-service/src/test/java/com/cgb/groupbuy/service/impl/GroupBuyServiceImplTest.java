package com.cgb.groupbuy.service.impl;

import com.cgb.groupbuy.dao.GroupBuyDao;
import com.cgb.groupbuy.entity.GroupBuyEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("团购信息服务测试")
class GroupBuyServiceImplTest {

    @Mock
    private GroupBuyDao tuanxinxiDao;

    @InjectMocks
    private GroupBuyServiceImpl tuanxinxiService;

    // ========== 辅助方法 ==========

    private GroupBuyEntity buildTuanxinxi(Long id, Long tuanduiid, Long userId, Integer status) {
        GroupBuyEntity entity = new GroupBuyEntity();
        entity.setId(id);
        entity.setTuanduiid(tuanduiid);
        entity.setUserId(userId);
        entity.setShangpinid(1L);
        entity.setShuliang(2);
        entity.setJiage(new BigDecimal("59.90"));
        entity.setZhuangtai(status);
        return entity;
    }

    // ========== save 测试 ==========

    @Nested
    @DisplayName("保存参团记录")
    class SaveTests {

        @Test
        @DisplayName("保存参团记录 - 未指定状态时默认设为0")
        void save_noDefault_setToZero() {
            GroupBuyEntity entity = buildTuanxinxi(null, 1L, 100L, null);
            when(tuanxinxiDao.insert(any(GroupBuyEntity.class))).thenReturn(1);

            tuanxinxiService.save(entity);

            assertEquals(0, entity.getZhuangtai(), "默认状态应为0(待支付)");
            verify(tuanxinxiDao).insert(entity);
        }

        @Test
        @DisplayName("保存参团记录 - 已有状态时保留原值")
        void save_withStatus_keepValue() {
            GroupBuyEntity entity = buildTuanxinxi(null, 1L, 100L, 1);
            when(tuanxinxiDao.insert(any(GroupBuyEntity.class))).thenReturn(1);

            tuanxinxiService.save(entity);

            assertEquals(1, entity.getZhuangtai());
            verify(tuanxinxiDao).insert(entity);
        }
    }

    // ========== update 测试 ==========

    @Nested
    @DisplayName("更新参团记录")
    class UpdateTests {

        @Test
        @DisplayName("更新参团记录 - 成功")
        void update_success() {
            GroupBuyEntity entity = buildTuanxinxi(1L, 1L, 100L, 1);
            when(tuanxinxiDao.updateById(any(GroupBuyEntity.class))).thenReturn(1);

            tuanxinxiService.update(entity);

            verify(tuanxinxiDao).updateById(entity);
        }
    }

    // ========== delete 测试 ==========

    @Nested
    @DisplayName("删除参团记录")
    class DeleteTests {

        @Test
        @DisplayName("删除参团记录 - 成功")
        void delete_success() {
            when(tuanxinxiDao.deleteById(1L)).thenReturn(1);

            tuanxinxiService.delete(1L);

            verify(tuanxinxiDao).deleteById(1L);
        }
    }

    // ========== countByTuanId 测试 ==========

    @Nested
    @DisplayName("统计团购已支付人数")
    class CountByTuanIdTests {

        @Test
        @DisplayName("统计 - 有多条已支付记录")
        void countByTuanId_multipleRecords() {
            when(tuanxinxiDao.selectCount(any())).thenReturn(5L);

            int count = tuanxinxiService.countByTuanId(1L);

            assertEquals(5, count);
            verify(tuanxinxiDao).selectCount(any());
        }

        @Test
        @DisplayName("统计 - 无记录时返回0")
        void countByTuanId_noRecords() {
            when(tuanxinxiDao.selectCount(any())).thenReturn(0L);

            int count = tuanxinxiService.countByTuanId(999L);

            assertEquals(0, count);
        }
    }
}
