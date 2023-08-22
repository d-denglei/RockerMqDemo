package transaction;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

/**
 *  消费者 推模式-等待broker推送消息过来
 * @author DengLei
 * @date 2023/06/28 13:15
 */

public class TranscationConsumer {

  public static void main(String[] args) throws MQClientException {

    //定义消费组 推模式()
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("TransactionConsumer");
    consumer.setNamesrvAddr("172.20.12.150:9876");

    //获取指定Topic的 subExpression *或者null代表订阅所有tags
    consumer.subscribe("Transaction","*");

    //并发消费
    consumer.setMessageListener(new MessageListenerConcurrently() {
      //返回状态
      @Override
      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
          ConsumeConcurrentlyContext context) {
//        list.forEach( n-> {
//          System.out.println("消息消费成功");
//        });
        for (int i = 0; i < list.size(); i++) {
          System.out.println(i+"_消息消费成功"+ new String(list.get(i).getBody()));
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
      }
    });

    consumer.start();
    System.out.printf("consumer started%n");
  }
}
