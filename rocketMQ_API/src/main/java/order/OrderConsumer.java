package order;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 顺序消息的消费者
 * @author DengLei
 * @date 2023/06/28 15:50
 */

public class OrderConsumer {

    public static void main(String[] args) throws MQClientException {

        //定义消费组 推模式()
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("SimpleConsumer");
        consumer.setNamesrvAddr("172.20.12.150:9876");
        //获取指定Topic的 subExpression *或者null代表订阅所有tags
        consumer.subscribe("Order","*");

        //顺序消费
        consumer.setMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list,
                ConsumeOrderlyContext context) {
                for (int i = 0; i <list.size() ; i++) {
                    System.out.println(i+"_消息消费成功_" + new String(list.get(i).getBody()));
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
//        //并发消费
//        consumer.setMessageListener(new MessageListenerConcurrently() {
//            //返回状态
//            @Override
//            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list,
//                ConsumeConcurrentlyContext context) {
////        list.forEach( n-> {
////          System.out.println("消息消费成功");
////        });
//                for (int i = 0; i < list.size(); i++) {
//                    System.out.println(i+"_消息消费成功"+ new String(list.get(i).getBody()));
//                }
//                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//            }
//        });

        consumer.start();
        System.out.printf("consumer started%n");
    }
}
