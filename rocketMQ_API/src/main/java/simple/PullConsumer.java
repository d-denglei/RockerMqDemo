package simple;

import java.util.HashSet;
import java.util.Set;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.store.ReadOffsetType;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 消费模式 - 拉模式  消费者主动去broker拉去消息
 *
 * @author DengLei
 * @date 2023/06/28 14:19
 */

public class PullConsumer {

  public static void main(String[] args) throws MQClientException {
    DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("PullConsumer");
    consumer.setNamesrvAddr("172.20.12.150:9876");
    Set<String> tops = new HashSet<>();
    tops.add("Simple");
    tops.add("TopicTest");
    consumer.setRegisterTopics(tops);
    consumer.start();

    while (true) {

      consumer.getRegisterTopics().forEach(n -> {
        try {
          Set<MessageQueue> messageQueues = consumer.fetchSubscribeMessageQueues(n);
          messageQueues.forEach(l -> {
            try {
              long offset = consumer.getOffsetStore()
                  .readOffset(l, ReadOffsetType.READ_FROM_MEMORY);
              if (offset < 0) {
                offset = consumer.getOffsetStore().readOffset(l, ReadOffsetType.READ_FROM_STORE);
              }
              if (offset < 0) {
                offset = consumer.maxOffset(l);
              }
              if (offset < 0) {
                offset = 0;
              }
              PullResult pullResult = consumer.pull(l, "*", offset, 32);
              System.out.println("循环消息拉取成功_" + pullResult);
              switch (pullResult.getPullStatus()) {
                //找到以后
                case FOUND:
                  pullResult.getMsgFoundList().forEach(k -> {
                    System.out.println("消息消费成功_" + k);
                  });
                  //获取以后修改偏移量到下一条
                  consumer.updateConsumeOffset(l, pullResult.getNextBeginOffset());
              }

            } catch (MQClientException e) {
              e.printStackTrace();
            } catch (RemotingException e) {
              e.printStackTrace();
            } catch (MQBrokerException e) {
              e.printStackTrace();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });
        } catch (MQClientException e) {
          e.printStackTrace();
        }
      });
    }
  }
}







