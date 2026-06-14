# 数据库设计

系统采用**每服务独立数据库**设计，共 5 个数据库。Flyway 迁移后所有表名已从拼音重命名为英文，Entity 使用英文字段名通过 `@TableField` 映射中文列名。

---

## cgb_user（用户服务）— 2 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `member` | MemberEntity | 用户表（V2 从 yonghu 重命名） | zhanghao(账号), mima(密码/BCrypt), xingming(姓名), xingbie(性别), shouji(手机), youxiang(邮箱), touxiang(头像), jifen(积分), yue(余额), status(状态) |
| `users` | UserEntity | 管理员表 | username(用户名), password(密码), role(角色) |

---

## cgb_product（商品服务）— 5 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `product` | ProductEntity | 商品表（V3 从 shangpin 重命名） | shangpinming(名称), shangpinleixing(分类), shangpintupian(图片), shangpinmiaoshu(描述), quhuofangshi(取货方式), kucun(库存), jiage(价格), yuanjia(原价), status(状态), shangjaid(商户ID) |
| `product_category` | ProductCategoryEntity | 商品分类表（V3 从 shangpinleixing 重命名） | leixingmingcheng(分类名称) |
| `product_collection` | ProductCollectionEntity | 商品收藏表（V3 从 shangpin_collection 重命名） | userid(用户ID), shangpinid(商品ID), addtime(收藏时间) |
| `product_comment` | ProductCommentEntity | 商品评论表（V3 从 shangpin_comment 重命名） | lianbiaoid(关联ID), userid(用户ID), pinglunneirong(评论内容), rating(评分), reply(回复) |
| `product_inquiry` | ProductInquiryEntity | 商品咨询表（V3 从 shangpin_liuyan 重命名） | lianbiaoid(关联ID), userid(用户ID), liuyanneirong(留言内容) |

---

## cgb_groupbuy（团购服务）— 3 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `group_slot` | GroupSlotEntity | 团购槽位表（V3 从 tuanwei 重命名） | tuanhao(团号), tuanchangming(团长名), tuanchangtupian(封面), tuanchangmiaoshu(描述), shangpinid(商品ID), zhuangtai(状态), mubiaorenshu(目标人数), dangqianrenshu(当前人数), yuanjia(原价), tuangoujia(团购价), jieshushijian(结束时间), tuanchangid(团长ID) |
| `group_info` | GroupBuyEntity | 参团记录表（V3 从 tuanxinxi 重命名） | tuanid(团ID), userid(用户ID), cantuanshijian(参团时间), zhuangtai(状态), goumaishuliang(购买数量), jiage(价格) |
| `group_comment` | GroupBuyCommentEntity | 团购评论表（V3 从 tuan_comment 重命名） | lianbiaoid(关联ID), userid(用户ID), pinglunneirong(评论内容), reply(回复) |

---

## cgb_order（订单服务）— 3 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `orders` | OrdersEntity | 订单表 | orderid(订单编号), userid(用户ID), shangpinid(商品ID), shangpinming(商品名), shangpintupian(商品图), shuliang(数量), jiage(单价), zongjia(总价), lianxidianhua(联系电话), shouhuodizhi(收货地址), zhuangtai(状态:0待支付/1已支付/2已取消/3已发货/4已完成), fukuanfangshi(付款方式), beizhu(备注), tuanduiid(团购ID) |
| `cart` | CartEntity | 购物车表 | userid(用户ID), shangpinid(商品ID), shuliang(数量), jiage(单价), huiyuanjia(会员价) |
| `address` | AddressEntity | 收货地址表 | userid(用户ID), dizhibiaoqian(地址标签), shouhuoren(收货人), diqu(地区), sheng(省), shi(市), qu(区), xiangxidizhi(详细地址), isdefault(是否默认) |

---

## cgb_content（内容服务）— 5 张表

| 表名 | Entity | 说明 | 核心字段（数据库列名） |
|------|--------|------|---------|
| `news` | NewsEntity | 社区公告表 | title(标题), introduction(简介), coverImage(封面图), content(内容), type(类型) |
| `forum` | ForumEntity | 论坛帖子表 | title(标题), content(内容), userid(用户ID), coverImage(封面图), thumbsUp(点赞数), status(状态), isHot(是否热门) |
| `message_board` | MessageBoardEntity | 留言板表（V3 从 messages 重命名） | userid(用户ID), content(留言内容), reply(回复), parentId(父级ID) |
| `information` | InformationEntity | 资讯表（V3 从 zixun 重命名） | title(标题), content(内容), coverImage(封面图) |
| `config` | ConfigEntity | 系统配置表（V2 新增） | key(配置键), value(配置值) |

---

[← 返回主页](../README.md)
