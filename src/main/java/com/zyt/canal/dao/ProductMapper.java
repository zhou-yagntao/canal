package com.zyt.canal.dao;

import com.zyt.canal.bean.po.Product;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * @ProjectName: canal
 * @Package: com.zyt.canal.dao
 * @ClassName: ProductMapper
 * @Author: zhou_yangtao@yeah.net
 * @Description: 商品信息处理持久层信息
 * @Date: 13:21 2022/4/10
 * @Version: 1.0
 */
@Mapper
@Repository(value = "ProductMapper")
public interface ProductMapper {

    /**
     * insert product to db
     *
     * @param product 商品信息
     */
    @Insert("insert into product(goodsId,goodsSn,productNo,goodsName,goodsImg,shopPrice,warnStock,goodsStock,goodsUnit,isSale,isBest,isHot,isNew,isRecom,goodsDesc,saleNum,saleTime)VALUES(0,#{goodsSn},#{productNo},#{goodsName},#{goodsImg},#{shopPrice},#{warnStock},#{goodsStock},#{goodsUnit},#{isSale},#{isBest},#{isHot},#{isNew},#{isRecom},#{goodsDesc},#{saleNum},#{saleTime})")
    void insertProductInfo(Product product);

    /**
     * update product to db by id
     *
     * @param product 商品信息
     */
    @Update("update product set goodsName = #{goodsName},shopPrice = #{shopPrice} where goodsId = #{prodId}")
    void updateProductInfo(Product product);

    /**
     * update product to db by id
     *
     * @param prodId 商品信息id
     */
    @Delete("delete from product where goodsId = #{prodId}")
    void deleteProductInfoByProductId(int prodId);

    /**
     * update product to db by id
     *
     */
    @Delete("delete from product")
    void deleteProductInfo();

}
