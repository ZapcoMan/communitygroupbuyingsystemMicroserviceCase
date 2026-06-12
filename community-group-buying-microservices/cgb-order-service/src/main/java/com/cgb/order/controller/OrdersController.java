package com.cgb.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.cgb.common.R;
import com.cgb.common.feign.FeignProductService;
import com.cgb.order.entity.OrdersEntity;
import com.cgb.order.entity.dto.CreateOrderDTO;
import com.cgb.order.entity.vo.OrderVO;
import com.cgb.order.service.OrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "订单管理")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;
    private final FeignProductService feignProductService;

    /**
     * 创建订单（分布式事务：下单+扣库存）
     */
    @Operation(summary = "创建订单（分布式事务：下单+扣库存）")
    @PostMapping
    public R<?> create(@Valid @RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));

        // 远程获取商品信息填充订单
        Object productData = feignProductService.getProductDetail(dto.getProductId()).getData();
        @SuppressWarnings("unchecked")
        Map<String, Object> productMap = productData instanceof Map ? (Map<String, Object>) productData : new HashMap<>();

        OrdersEntity entity = new OrdersEntity();
        entity.setUserid(userId);
        entity.setShangpinid(dto.getProductId());
        entity.setShangpinming(productMap.get("mingcheng") != null ? productMap.get("mingcheng").toString() : "");
        entity.setShangpintupian(productMap.get("tupian") != null ? productMap.get("tupian").toString() : "");
        entity.setShuliang(dto.getQuantity());
        entity.setJiage(dto.getQuantity() != null && productMap.get("jiage") != null
                ? new java.math.BigDecimal(productMap.get("jiage").toString()) : null);
        entity.setLianxidianhua(dto.getContactPhone());
        entity.setShouhuodizhi(dto.getShippingAddress());
        entity.setFukuanfangshi(dto.getPaymentMethod());
        entity.setBeizhu(dto.getRemark());
        entity.setTuanduiid(dto.getGroupBuyId());

        ordersService.createOrder(entity);
        return R.ok("下单成功", toVO(entity));
    }

    @Operation(summary = "我的订单列表")
    @GetMapping("/my")
    public R<?> myList(@Parameter(hidden = true) OrdersEntity params,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        IPage<OrdersEntity> result = ordersService.queryPage(params);
        IPage<OrderVO> voPage = result.convert(this::toVO);
        return R.ok(voPage);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public R<?> detail(@PathVariable Long id) {
        OrdersEntity entity = ordersService.getById(id);
        return R.ok(toVO(entity));
    }

    @Operation(summary = "支付订单")
    @PostMapping("/pay/{orderId}")
    public R<?> pay(@PathVariable String orderId) {
        ordersService.pay(orderId);
        return R.ok("支付成功");
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel/{orderId}")
    public R<?> cancel(@PathVariable String orderId, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("X-User-Id"));
        ordersService.cancel(orderId, userId);
        return R.ok("取消成功");
    }

    @Operation(summary = "删除订单")
    @DeleteMapping("/{id}")
    public R<?> delete(@PathVariable Long id) {
        ordersService.delete(id);
        return R.ok("删除成功");
    }

    @Operation(summary = "管理员查询所有订单")
    @GetMapping("/list")
    public R<?> list(@Parameter(hidden = true) OrdersEntity params,
                     @RequestParam(defaultValue = "1") Integer page,
                     @RequestParam(defaultValue = "10") Integer limit) {
        IPage<OrdersEntity> result = ordersService.queryPage(params);
        IPage<OrderVO> voPage = result.convert(this::toVO);
        return R.ok(voPage);
    }

    @Operation(summary = "更新订单")
    @PutMapping
    public R<?> update(@RequestBody OrdersEntity entity) {
        ordersService.update(entity);
        return R.ok("更新成功");
    }

    @Operation(summary = "批量删除订单")
    @DeleteMapping("/batch")
    public R<?> batchDelete(@RequestBody java.util.List<Long> ids) {
        ids.forEach(ordersService::delete);
        return R.ok("批量删除成功");
    }

    /** 内部接口 */
    @GetMapping("/internal/orderDetail")
    public R<?> internalOrderDetail(@RequestParam String orderId) {
        return R.ok(ordersService.getByOrderId(orderId));
    }

    @PostMapping("/internal/cancel")
    public R<?> internalCancel(@RequestParam String orderId, @RequestParam Long userId) {
        ordersService.cancel(orderId, userId);
        return R.ok();
    }

    /**
     * Entity → VO 转换（英文字段对外暴露）
     */
    private OrderVO toVO(OrdersEntity e) {
        if (e == null) return null;
        OrderVO vo = new OrderVO();
        vo.setId(e.getId());
        vo.setOrderId(e.getOrderid());
        vo.setUserId(e.getUserid());
        vo.setProductId(e.getShangpinid());
        vo.setProductName(e.getShangpinming());
        vo.setProductImage(e.getShangpintupian());
        vo.setQuantity(e.getShuliang());
        vo.setUnitPrice(e.getJiage());
        vo.setTotalPrice(e.getZongjia());
        vo.setContactPhone(e.getLianxidianhua());
        vo.setShippingAddress(e.getShouhuodizhi());
        vo.setStatus(e.getZhuangtai());
        vo.setPaymentMethod(e.getFukuanfangshi());
        vo.setRemark(e.getBeizhu());
        vo.setGroupBuyId(e.getTuanduiid());
        vo.setCreateTime(e.getAddtime());
        vo.setUpdateTime(e.getUpdatetime());
        return vo;
    }
}
