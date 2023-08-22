package broadcast;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

/**
 * 广播消息 一条消息会发给所有订阅了对应主题的消费者，不管消费者是不是同一个消费者组
 * @author DengLei
 * @date 2023/06/28 16:13
 */

public class BroadcastConsumer {

  public static void main(String[] args) throws MQClientException {

    //定义消费组 推模式()
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("SimpleConsumer");
    consumer.setNamesrvAddr("172.20.12.150:9876");
    //获取指定Topic的 subExpression *或者null代表订阅所有tags
    consumer.subscribe("Simple","*");

    //消费方式集群模式
    consumer.setMessageModel(MessageModel.CLUSTERING);

    //消费方式 广播模式
 //   consumer.setMessageModel(MessageModel.BROADCASTING);

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
