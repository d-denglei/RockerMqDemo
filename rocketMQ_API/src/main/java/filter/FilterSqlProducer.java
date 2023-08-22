package filter;

import java.nio.charset.StandardCharsets;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 *  过滤消息生产者 sql方式
 * @author DengLei
 * @date 2023/06/30 10:18
 */

public class FilterSqlProducer {
    public static void main(String[] args)
        throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        //定义生产者
        DefaultMQProducer producer = new DefaultMQProducer("FilterProducer");
        //之前部署过的三台 集群的某一个就行
        producer.setNamesrvAddr("172.20.12.150:9876");
        producer.start();

        String[] tags = new String[]{"TagA","TagB","TagC"};
        for (int i = 0; i < 10; i++) {
            Message message = new Message("Filter",tags[i % tags.length],(tags[i % tags.length]+"_FilterProducer").getBytes(
                StandardCharsets.UTF_8));
            message.putUserProperty("dengLei",String.valueOf(i));
            SendResult sendResult = producer.send(message);
            System.out.printf(tags[i % tags.length] + "_dengLei_" + i +"_消息发送成功%s%n", sendResult);
        }

        producer.shutdown();

    }
}
