package com.zyt.canal.controller;

import com.zyt.canal.bean.po.Product;
import com.zyt.canal.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ProjectName: canal
 * @Package: com.zyt.canal.controller
 * @ClassName: ProductController
 * @Author: zhou_yangtao@yeah.net
 * @Description:
 * @Date: 15:53 2022/4/10
 * @Version: 1.0
 */
@Controller
@RequestMapping(value = "/product")
public class ProductController {

    @Resource
    private ProductService productService;

    @PostMapping(value = "/insert")
    @ResponseBody
    public String insert(@RequestBody() Product product){
       return productService.insert(product);
    }

    @PostMapping(value = "/update")
    public String update(@RequestBody Product product){
        return productService.update(product);
    }

    @PostMapping(value = "/delete")
    public String delete(@RequestParam Integer id){
        return productService.delete(id);
    }

}
