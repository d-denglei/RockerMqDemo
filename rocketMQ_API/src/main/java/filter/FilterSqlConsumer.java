package filter;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 过滤消息消费者sql方式
 * @author DengLei
 * @date 2023/06/30 10:13
 */

public class FilterSqlConsumer {

    public static void main(String[] args) throws MQClientException {

        //定义消费组 推模式()
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("FilterConsumer");
        consumer.setNamesrvAddr("172.20.12.150:9876");

        //获取指定Topic的 subExpression *或者null代表订阅所有tags
        consumer.subscribe("Filter", MessageSelector.bySql("TAGS is not null and TAGS in ('TagA','TagC')"
            + "and (dengLei is not null and dengLei between 0 and 3)"));

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
