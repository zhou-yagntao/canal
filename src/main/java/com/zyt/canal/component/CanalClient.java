package com.zyt.canal.component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.zyt.canal.config.ElasticSearchConfig;
import com.zyt.canal.constant.EsConstant;
import com.zyt.canal.util.ChangeJsonTools;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: canal
 * @Package: com.zyt.canal.component
 * @ClassName: CanalClient
 * @Author: zhou_yangtao@yeah.net
 * @Description: canal客户端组件
 * @Date: 13:48 2022/4/10
 * @Version: 1.0
 */
@Component
@PropertySource("classpath:application.properties")
public class CanalClient {

   private final static Logger logger = LoggerFactory.getLogger(CanalClient.class);

    //主机
    @Value(value = "${canal.client.instances.host}")
    private String canalMonitorHost;

    //端口号
    @Value(value = "${canal.client.instances.port}")
    private Integer canalMonitorPort;

    private final static Integer BATCH_SIZE = 1000;

    private final static String PRODUCT_INFO_CACHE_PREFIX = "product_info_id:";

    @Autowired
    private RedisTemplate redisTemplate;

    //引入es服务
    @Resource
    private RestHighLevelClient restHighLevelClient;

    public void run(){
       CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress(canalMonitorHost, canalMonitorPort),
               "example", "", "");
       try {
           //连接canal服务端
           canalConnector.connect();
           logger.info("connet canal service successfully");
           //订阅数据库 .*\..*(全部数据库) canal_demo\\..* 某个db下所有的数据库表
           canalConnector.subscribe(".*\\..*");
           //回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
           canalConnector.rollback();
           while (true){
               //获取指定数量的消息
               Message message = canalConnector.getWithoutAck(BATCH_SIZE);
               long batchId = message.getId();
               int size = message.getEntries().size();
               logger.info("batchId = {}",batchId);
               logger.info("size = {}",size);
               if (-1 == batchId || 0 == size){
                   try {
                       Thread.sleep(1000);
                       logger.info("当前处于等待中获取mysql数据库binlog日志中的数据。。。。。。。。。。。。。");
                   }catch (Exception e){
                       logger.error("entry is null ",e);
                   }
               }else{
                   printEntry(message.getEntries());
               }

           }
        }catch (Exception e){
            logger.error("connet canal service failure",e);
        }finally {
           canalConnector.disconnect();
           //防止频繁访问数据库链接: 线程睡眠 10秒
           try {
               Thread.sleep(10 * 1000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
        }
   }

    private  void printEntry(List<CanalEntry.Entry> entries) {
       for(CanalEntry.Entry entry : entries){
           //开启/关闭事务的实体类型，跳过
           if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN
                   || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
               continue;
           }
           //RowChange对象，包含了一行数据变化的所有特征
           //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
           CanalEntry.RowChange rowChage = null;
           try {
               //反序列化
               rowChage = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
           } catch (Exception e) {
               logger.error("error ## parser of eromanga-event has an error , data: {}",entry.toString(),e);
               throw new RuntimeException("error ## parser of eromanga-event has an error n, data:" + entry.toString(),
                       e);
           }
           CanalEntry.EventType eventType = rowChage.getEventType();
           logger.info(String.format("Canal监测到更新================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
                   entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                   entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                   eventType));

//           for (CanalEntry.RowData rowData : rowChage.getRowDatasList()) {
//               if (eventType == CanalEntry.EventType.DELETE) {
//                   logger.info("准备删除缓存中的数据了。。。。。。。。。。。。。");
//                   logger.info("======================》 (DELETE)操作数据库表之前");
//                   printColumn(rowData.getBeforeColumnsList());
//                   logger.info("======================》 (DELETE)操作数据库表结构之后");
//                   printColumn(rowData.getAfterColumnsList());
//                   redisDelete(rowData.getBeforeColumnsList());
//               } else if (eventType == CanalEntry.EventType.INSERT) {
//                   logger.info("准备同步数据到缓存中了。。。。。。。。。。。。。");
//                   logger.info("======================》 (INSERT)操作数据库表之前");
//                   printColumn(rowData.getBeforeColumnsList());
//                   logger.info("======================》 (INSERT)操作数据库表结构之后");
//                   redisInsert(rowData.getAfterColumnsList());
//               } else {
//                   logger.info("======================》 (UPDATE)操作数据库表之前");
//                   printColumn(rowData.getBeforeColumnsList());
//                   logger.info("======================》 (UPDATE)操作数据库表结构之后");
//                   printColumn(rowData.getAfterColumnsList());
//                   redisUpdate(rowData.getAfterColumnsList());
//               }
//           }
           List<CanalEntry.RowData> rowDatasList = rowChage.getRowDatasList();
           synchronizationDataFromDBToElasticsearchByCanal(rowDatasList);

       }

    }

    /**
     * 打印binlog日志
     * */
    private  void printColumn(List<CanalEntry.Column> columns) {
        for (CanalEntry.Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }

    /**
     * 录入数据库数据同步存入缓存中
     * */
    private  void redisInsert(List<CanalEntry.Column> columns) {
        JSONObject json = new JSONObject();
        for (CanalEntry.Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
        if (columns.size() > 0) {
            redisTemplate.opsForValue().set(PRODUCT_INFO_CACHE_PREFIX+columns.get(0).getValue(),json.toJSONString());
        }
    }

    /**
     * 更新数据库数据同步更新缓存
     * */
    private  void redisUpdate(List<CanalEntry.Column> columns) {
        JSONObject json = new JSONObject();
        for (CanalEntry.Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
        if (columns.size() > 0) {

            redisTemplate.opsForValue().set(PRODUCT_INFO_CACHE_PREFIX+columns.get(0).getValue(),json.toJSONString());
        }
    }

    /***
     * 删除redis缓存中的
     * */
    private  void redisDelete(List<CanalEntry.Column> columns) {
        JSONObject json = new JSONObject();
        for (CanalEntry.Column column : columns) {
            json.put(column.getName(), column.getValue());
        }
        if (columns.size() > 0) {
            redisTemplate.delete(PRODUCT_INFO_CACHE_PREFIX+columns.get(0).getValue());
        }
    }

    /**
     * canal同步数据到elasticsearch
     */
    private void synchronizationDataFromDBToElasticsearchByCanal(List<CanalEntry.RowData> rowDatasList){
        BulkResponse bulkResponse = null;
        //给es建立索引,建立好映射关系
        //向es中保存数据
        BulkRequest bulkRequest = new BulkRequest();
        for (CanalEntry.RowData rowData:rowDatasList){
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(rowData.getAfterColumnsList().get(0).getValue());
            //将数据保存为json格式再进行存储
            String value  = ChangeJsonTools.objToStringPretty(rowData);
            indexRequest.source(value, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        try {
            bulkResponse = this.restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean flag = bulkResponse.hasFailures();
        List<String> collect = Arrays.stream(bulkResponse.getItems()).map(item ->{
            return  item.getId();
        }).collect(Collectors.toList());

    }
}
