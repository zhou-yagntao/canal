package com.zyt.canal;

import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.client.CanalConnector;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;

import java.util.List;

/**
 * @ProjectName: canal
 * @Package: com.zyt.canal
 * @ClassName: CanalClientTestBaseTcp
 * @Author: zhou_yangtao@yeah.net
 * @Description: Canal初识
 * @Date: 20:44 2022/4/9
 * @Version: 1.0
 */
public class CanalClientTestBaseTcp {

    private static  final Logger logger = LoggerFactory.getLogger(CanalClientTestBaseTcp.class);
    public static void main(String[] args) {
        //获取连接
        //  hostname 安装canal服务的地址  port  canal服务端口号  destination canal默认实例  账号 密码(canal 客户端与服务端之间不需要账号密码)
        CanalConnector canalConnector = CanalConnectors.newSingleConnector(new InetSocketAddress("192.168.10.132", 11111),
                "example", "", "");

        while(true){

            //连接
            canalConnector.connect();

            //订阅数据库
            //canalConnector.subscribe("canal_demo\\*");
            canalConnector.subscribe(".*\\..*");

            //获取数据
            Message message = canalConnector.get(500);

            //获取Entry集合
            List<CanalEntry.Entry> entries = message.getEntries();

            if (CollectionUtils.isEmpty(entries)){
                  try {
                      logger.info("当前处于等待中。。。。。。。。。。。。");
                      Thread.sleep(1000);
                  }catch (Exception e){
                      logger.error("entry is null ",e);
                  }
            }else{
                printEntry((entries));
            }

        }
    }

    private static void printEntry(List<Entry> entrys) {
        for (Entry entry : entrys) {
            //开启/关闭事务的实体类型，跳过
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            //RowChange对象，包含了一行数据变化的所有特征
            //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
            RowChange rowChage = null;
            try {
                //反序列化
                rowChage = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),
                        e);
            }
            //获取操作类型
            EventType eventType = rowChage.getEventType();
            System.out.println(String.format("================&gt; binlog[%s:%s] , name[%s,%s] , eventType : %s",
                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),
                    eventType));

            for (RowData rowData : rowChage.getRowDatasList()) {
                if (eventType == EventType.DELETE) {
                    printColumn(rowData.getBeforeColumnsList());
                } else if (eventType == EventType.INSERT) {
                    printColumn(rowData.getAfterColumnsList());
                } else {
                    System.out.println("-------&gt; before");
                    printColumn(rowData.getBeforeColumnsList());
                    System.out.println("-------&gt; after");
                    printColumn(rowData.getAfterColumnsList());
                }
            }
        }
    }

    private static void printColumn(List<Column> columns) {
        for (Column column : columns) {
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());
        }
    }
}




























