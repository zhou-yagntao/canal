package com.zyt.canal;


import com.zyt.canal.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;



@SpringBootTest
@RunWith(SpringRunner.class)
class CanalApplicationTests {

    @Autowired
    private ProductService productService;

    @Test
    public void insertDataToDB(){

    }

    @Test
    public void updateDataTODB(){

    }

    @Test
    public void deleteDataFromDB(){

    }

    @Test
    public void selectDataFromRedis(){

    }

}
