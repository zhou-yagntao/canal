package com.zyt.canal.bean.po;

import lombok.Data;

import java.util.Date;

/**
 * @ProjectName: canal
 * @Package: com.zyt.canal.bean
 * @ClassName: Product
 * @Author: zhou_yangtao@yeah.net
 * @Description: 商品信息实体层
 * @Date: 12:54 2022/4/10
 * @Version: 1.0
 */
@Data
public class Product {
    /**
     * 自增id
     * */
    private Integer goodsId;
    /**
     * 商品编号
     * */
    private String goodsSn;
    /**
     * 商品货号
     */
    private String productNo;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 商品图片
     */
    private String goodsImg;
    /**
     * 商品价格
     */
    private Integer shopPrice;
    /**
     * 预警库存
     */
    private Integer warnStock;
    /**
     * 商品总库存
     */
    private Integer goodsStock;
    /**
     * 单位
     */
    private String goodsUnit;
    /**
     * 是否上架
     */
    private Integer isSale;
    /**
     * 是否精品
    */
    private Integer isBest;
    /**
     * 是否热销商品
     */
    private Integer isHot;
    /**
     * 是否新品
     */
    private Integer isNew;
    /**
     * 是否推荐
     */
    private Integer isRecom;
    /**
     * 商品描述
     * */
    private String goodsDesc;
    /**
     * 销售数量
     * */
    private Integer saleNum;
    /**
     * 上架时间
     * */
    private Date saleTime;
}
