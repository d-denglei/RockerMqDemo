package simple;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.StandardCharsets;

/**
 * 同步发送 消息生产者
 *
 * 1.对可靠性要求高
 * 2.数据量级比较少 同步发送返回结果是实时等待，并发量大会卡死
 * 3.实时响应
 *
 * @author DengLei
 * @date 2023/06/28 11:04
 */

public class SyncProducer {

  public static void main(String[] args)
      throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
    //定义生产者
    DefaultMQProducer producer = new DefaultMQProducer("SyncProducer");
    //之前部署过的三台 集群的某一个就行
    producer.setNamesrvAddr("172.20.12.150:9876");
    producer.start();

    for (int i = 0; i < 2; i++) {
      Message message = new Message("Simple","Tags",(i+"_SyncProducer").getBytes(StandardCharsets.UTF_8));
      SendResult sendResult = producer.send(message);
      System.out.printf(i+"_消息发送成功%s%n", sendResult);
    }

    producer.shutdown();

  }

}
