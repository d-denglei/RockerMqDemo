package simple;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 消息的异步发送
 * 适用于一些流量峰值较高的业务
 *
 * @author DengLei
 * @date 2023/06/28 13:50
 */

public class AsyncProducer {

  public static void main(String[] args)
      throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
    DefaultMQProducer producer = new DefaultMQProducer("SyncProducer");
    //之前部署过的三台 集群的某一个就行
    producer.setNamesrvAddr("172.20.12.150:9876");
    producer.start();
    //计数器
    CountDownLatch countDownLatch = new CountDownLatch(100);
    for (int i = 0; i < 100; i++) {
        final int index = i;
      Message message = new Message("Simple", "TagA",
          (i + "_ASyncProducer").getBytes(StandardCharsets.UTF_8));
      producer.send(message, new SendCallback() {
        @Override
        public void onSuccess(SendResult sendResult) {
            countDownLatch.countDown();
          System.out.println(index+"_消息发送成功_" + sendResult);
        }

        @Override
        public void onException(Throwable e) {
            countDownLatch.countDown();
          System.out.println("消息发送失败_" + e.getStackTrace());
        }
      });
    }

    countDownLatch.await(5, TimeUnit.SECONDS);
    producer.shutdown();
  }
}
