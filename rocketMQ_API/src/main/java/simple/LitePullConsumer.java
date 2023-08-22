package simple;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * DefaultMQPullConsumer 不推荐使用 有新的DefaultLitePullConsumer 去替换
 * 拉模式 随机获取一个queue消息
 * @author DengLei
 * @date 2023/06/28 14:59
 */

public class LitePullConsumer {

  public static void main(String[] args) throws MQClientException {
    DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("LitePullConsumer");
    consumer.setNamesrvAddr("172.20.12.150:9876");
    //获取对应tipic下所有的
    consumer.subscribe("Simple","*");
    consumer.start();

    System.out.println("liteConsumer start");
    while (true) {
      List<MessageExt> poll = consumer.poll();
      System.out.printf("消息拉取成功 %s%n" , poll);
    }

  }
}
