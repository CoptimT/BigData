package cn.zxw.bigdata.zk.client;

import org.apache.zookeeper.*;

/**
 * Created by zhangxw on 2017/5/11.
 */
public class ZkClient {
    public static void main(String[] args){
        final int CLIENT_PORT = 2181;
        try {
            // 创建一个与服务器的连接
            ZooKeeper zk = new ZooKeeper("localhost:" + CLIENT_PORT, 3000, new Watcher() {
                // 监控所有被触发的事件
                public void process(WatchedEvent event) {
                    System.out.println("已经触发了" + event.getType() + "事件！");
                }
            });
            // 创建一个目录节点
            zk.create("/testRootPath", "testRootData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            // 创建一个子目录节点
            zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

            System.out.println(new String(zk.getData("/testRootPath", false, null)));
            // 取出子目录节点列表
            System.out.println(zk.getChildren("/testRootPath", true));

            // 修改子目录节点数据
            zk.setData("/testRootPath/testChildPathOne", "modifyChildDataOne".getBytes(), -1);
            System.out.println("目录节点状态：[" + zk.exists("/testRootPath", true) + "]");

            // 创建另外一个子目录节点
            zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println(new String(zk.getData("/testRootPath/testChildPathTwo", true, null)));

            // 删除子目录节点
            zk.delete("/testRootPath/testChildPathTwo", -1);
            zk.delete("/testRootPath/testChildPathOne", -1);
            // 删除父目录节点
            zk.delete("/testRootPath", -1);
            // 关闭连接
            zk.close();
        }catch(Exception e){

        }
    }
}
