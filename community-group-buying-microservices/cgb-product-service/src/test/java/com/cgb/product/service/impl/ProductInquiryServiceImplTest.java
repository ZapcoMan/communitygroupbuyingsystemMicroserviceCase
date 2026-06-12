package com.cgb.product.service.impl;

import com.cgb.product.dao.ProductInquiryDao;
import com.cgb.product.entity.ProductInquiryEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductInquiryServiceImpl - 商品留言服务测试")
class ProductInquiryServiceImplTest {

    @Mock
    private ProductInquiryDao liuyanDao;

    @InjectMocks
    private ProductInquiryServiceImpl liuyanService;

    @Nested
    @DisplayName("save - 新增留言")
    class SaveTests {
        @Test
        @DisplayName("正常保存留言")
        void save_validEntity_callsInsert() {
            when(liuyanDao.insert(any(ProductInquiryEntity.class))).thenReturn(1);
            ProductInquiryEntity entity = new ProductInquiryEntity();
            entity.setShangpinid(1L);
            liuyanService.save(entity);
            verify(liuyanDao).insert(entity);
        }
    }

    @Nested
    @DisplayName("delete - 删除留言")
    class DeleteTests {
        @Test
        @DisplayName("正常删除留言")
        void delete_validId_callsDeleteById() {
            when(liuyanDao.deleteById(1L)).thenReturn(1);
            liuyanService.delete(1L);
            verify(liuyanDao).deleteById(1L);
        }
    }
}
