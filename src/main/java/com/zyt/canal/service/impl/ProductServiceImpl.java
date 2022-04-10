package com.zyt.canal.service.impl;

import com.zyt.canal.bean.po.Product;
import com.zyt.canal.dao.ProductMapper;
import com.zyt.canal.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: canal
 * @Package: com.zyt.canal.service.impl
 * @ClassName: ProductServiceImpl
 * @Author: zhou_yangtao@yeah.net
 * @Description:
 * @Date: 19:13 2022/4/10
 * @Version: 1.0
 */
@Service(value = "productService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public String insert(Product product) {
        productMapper.insertProductInfo(product);
        return "录入商品信息成功";
    }

    @Override
    public String update(Product product) {
        productMapper.updateProductInfo(product);
        return "更新商品信息成功";
    }

    @Override
    public String delete(Integer id) {
        productMapper.deleteProductInfoByProductId(id);
        return "删除商品信息成功";
    }
}
