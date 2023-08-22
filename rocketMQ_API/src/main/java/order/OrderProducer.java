package order;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 顺序消费的生产者
 * @author DengLei
 * @date 2023/06/28 15:35
 */

public class OrderProducer {

    public static void main(String[] args)
        throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        DefaultMQProducer producer = new DefaultMQProducer("OrderProducer");
        //之前部署过的三台 集群的某一个就行
        producer.setNamesrvAddr("172.20.12.150:9876");
        producer.start();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                Message message = new Message("Order",
                    "TagA",("order_" + i + "_step_" + j).getBytes(StandardCharsets.UTF_8));
                SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                    //Ojbect o 这个参数跟 send方法第二个参数的值是一个，
                    //作用是 当Object o的参数跟send 第二个参数i的值一样时就确定为同一个消息
                    @Override
                    public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                        Integer id = (Integer) o;
                        int index = id % list.size();
                        return list.get(index);
                    }
                    //第二个
                }, i);
                System.out.println("消息发送成功_" + sendResult);
            }
        }

        producer.shutdown();
    }
}
