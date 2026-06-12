package com.cgb.groupbuy.service.impl;

import com.cgb.common.EIException;
import com.cgb.groupbuy.dao.TuanweiDao;
import com.cgb.groupbuy.entity.TuanweiEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("团长团购服务测试")
class TuanweiServiceImplTest {

    @Mock
    private TuanweiDao tuanweiDao;

    @InjectMocks
    private TuanweiServiceImpl tuanweiService;

    // ========== 辅助方法 ==========

    private TuanweiEntity buildTuanwei(Long id, Integer status, Integer currentPeople) {
        TuanweiEntity entity = new TuanweiEntity();
        entity.setId(id);
        entity.setMingcheng("新鲜水果团购");
        entity.setTupian("/images/tuanwei/1.jpg");
        entity.setJieshao("优质新鲜水果，产地直发");
        entity.setShangpinid(1L);
        entity.setZhuangtai(status);
        entity.setLirenjia(10);
        entity.setXianxiarenshu(currentPeople);
        entity.setYuanjia(new BigDecimal("99.00"));
        entity.setTejia(new BigDecimal("59.90"));
        entity.setJieshushijian(LocalDateTime.now().plusDays(7));
        entity.setUserId(100L);
        return entity;
    }

    // ========== save 测试 ==========

    @Nested
    @DisplayName("创建团购")
    class SaveTests {

        @Test
        @DisplayName("创建团购 - 未指定状态和人数时设默认值")
        void save_noDefaults_setDefaults() {
            TuanweiEntity entity = buildTuanwei(null, null, null);
            when(tuanweiDao.insert(any(TuanweiEntity.class))).thenReturn(1);

            tuanweiService.save(entity);

            assertEquals(0, entity.getZhuangtai(), "默认状态应为0(进行中)");
            assertEquals(1, entity.getXianxiarenshu(), "默认当前人数应为1");
            verify(tuanweiDao).insert(entity);
        }

        @Test
        @DisplayName("创建团购 - 已有状态和人数时保留原值")
        void save_withValues_keepOriginal() {
            TuanweiEntity entity = buildTuanwei(null, 1, 5);
            when(tuanweiDao.insert(any(TuanweiEntity.class))).thenReturn(1);

            tuanweiService.save(entity);

            assertEquals(1, entity.getZhuangtai());
            assertEquals(5, entity.getXianxiarenshu());
            verify(tuanweiDao).insert(entity);
        }
    }

    // ========== update 测试 ==========

    @Nested
    @DisplayName("更新团购")
    class UpdateTests {

        @Test
        @DisplayName("更新团购 - 成功")
        void update_success() {
            TuanweiEntity entity = buildTuanwei(1L, 0, 3);
            when(tuanweiDao.updateById(any(TuanweiEntity.class))).thenReturn(1);

            tuanweiService.update(entity);

            verify(tuanweiDao).updateById(entity);
        }

        @Test
        @DisplayName("更新团购 - ID为空抛出异常")
        void update_nullId_throwsException() {
            TuanweiEntity entity = buildTuanwei(null, 0, 3);

            EIException ex = assertThrows(EIException.class,
                    () -> tuanweiService.update(entity));
            assertEquals("团购ID不能为空", ex.getMessage());
        }
    }

    // ========== delete 测试 ==========

    @Nested
    @DisplayName("删除团购")
    class DeleteTests {

        @Test
        @DisplayName("删除团购 - 成功")
        void delete_success() {
            when(tuanweiDao.deleteById(1L)).thenReturn(1);

            tuanweiService.delete(1L);

            verify(tuanweiDao).deleteById(1L);
        }
    }

    // ========== getById 测试 ==========

    @Nested
    @DisplayName("根据ID查询团购")
    class GetByIdTests {

        @Test
        @DisplayName("根据ID查询 - 存在")
        void getById_exists() {
            TuanweiEntity expected = buildTuanwei(1L, 0, 5);
            when(tuanweiDao.selectById(1L)).thenReturn(expected);

            TuanweiEntity result = tuanweiService.getById(1L);

            assertNotNull(result);
            assertEquals("新鲜水果团购", result.getMingcheng());
            assertEquals(5, result.getXianxiarenshu());
        }

        @Test
        @DisplayName("根据ID查询 - 不存在抛出异常")
        void getById_notFound_throwsException() {
            when(tuanweiDao.selectById(999L)).thenReturn(null);

            assertThrows(EIException.class, () -> tuanweiService.getById(999L));
        }
    }
}
