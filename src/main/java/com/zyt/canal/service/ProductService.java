package com.zyt.canal.service;

import com.zyt.canal.bean.po.Product;

/**
 * @ProjectName: canal
 * @Package: com.zyt.canal.service.impl
 * @ClassName: ProductService
 * @Author: zhou_yangtao@yeah.net
 * @Description: 商品信息服务层
 * @Date: 19:12 2022/4/10
 * @Version: 1.0
 */
public interface ProductService {

    /**
     * 录入商品信息
     * */
    String insert(Product product);

    /**
     * 更新商品信息
     * */
    String update(Product product);

    /**
     * 删除商品信息
     * */
    String delete(Integer id);
}
