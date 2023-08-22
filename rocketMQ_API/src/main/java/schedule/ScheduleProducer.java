package schedule;

import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 延时消息 预定日程生产者
 *
 * @author DengLei
 * @date 2023/06/28 16:43
 */

public class ScheduleProducer {

  public static void main(String[] args)
      throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
    //定义生产者
    DefaultMQProducer producer = new DefaultMQProducer("ScheduleProducer");
    //之前部署过的三台 集群的某一个就行
    producer.setNamesrvAddr("172.20.12.150:9876");
    producer.start();

    for (int i = 0; i < 2; i++) {
      Message message = new Message("Schedule", "Tags",
          (i + "_ScheduleProducer").getBytes(StandardCharsets.UTF_8));
      //1到18分别对应messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
      //可以在dashboard中broker配置查看。
//      message.setDelayTimeLevel(1);
      //配置毫秒数
      message.setDelayTimeMs(10000L);
      producer.send(message);
      System.out.println("消息发送成功_" + LocalTime.now());
    }

    producer.shutdown();

  }
}
