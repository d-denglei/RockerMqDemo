package simple;

import java.nio.charset.StandardCharsets;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 单向发送
 * 适用场景 日志收集
 * 安全性比较低 无法知道是否发送成功
 *
 * @author DengLei
 * @date 2023/06/28 11:04
 */

public class OnewayProducer {

  public static void main(String[] args)
      throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
    //定义生产者
    DefaultMQProducer producer = new DefaultMQProducer("SyncProducer");
    //之前部署过的三台 集群的某一个就行
    producer.setNamesrvAddr("172.20.12.150:9876");
    producer.start();

    for (int i = 0; i < 2; i++) {
      Message message = new Message("Simple","Tags",(i+"_SyncProducer").getBytes(StandardCharsets.UTF_8));
      //单向发送无法拿到返回内容
      producer.sendOneway(message);
      System.out.printf(i+"_消息发送成功%n");
    }

    producer.shutdown();

  }

}
