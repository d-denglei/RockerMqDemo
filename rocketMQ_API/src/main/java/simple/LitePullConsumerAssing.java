package simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

/**
 * 拉模式 指定获取一个Queue消息
 * @author DengLei
 * @date 2023/06/28 15:11
 */

public class LitePullConsumerAssing {

  public static void main(String[] args) throws MQClientException {
    DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("LitePullConsumer");
    consumer.setNamesrvAddr("172.20.12.150:9876");
    consumer.start();
    Collection<MessageQueue> messageQueues = consumer.fetchMessageQueues("Simple");
    ArrayList<MessageQueue> messageQueues1 = new ArrayList<>(messageQueues);
    consumer.assign(messageQueues1);
    //指定了 当前topic下去获取的消息队列
    consumer.seek(messageQueues1.get(1),10);
    System.out.println("consumer started.");
    while(true){
      List<MessageExt> messageExtList = consumer.poll();
      System.out.printf("消息拉取成功%s %n",messageExtList);


    }



  }
}
