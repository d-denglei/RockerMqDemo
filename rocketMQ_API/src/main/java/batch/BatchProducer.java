package batch;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * 批量消息发送
 *
 * @author DengLei
 * @date 2023/06/28 16:57
 */

public class BatchProducer {

  public static void main(String[] args)
      throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
    //定义生产者
    DefaultMQProducer producer = new DefaultMQProducer("BatchProducer");
    //之前部署过的三台 集群的某一个就行
    producer.setNamesrvAddr("172.20.12.150:9876");
    producer.start();

    List<Message> messageList = new ArrayList<>();

    for (int i = 0; i < 100000; i++) {
      Message message = new Message("Simple", "Tags",
          (i + "_BatchProducer").getBytes(StandardCharsets.UTF_8));

      messageList.add(message);
    }

    ListSplitter listSplitter = new ListSplitter(messageList);
    int i = 0;
    while (listSplitter.hasNext()){
      SendResult sendResult = producer.send( listSplitter.next());
      System.out.printf(i++ + "消息发送成功%s%n", sendResult);
    }


    producer.shutdown();

  }
}

//当发送体过大 如何分批发送？
class ListSplitter implements Iterator<List<Message>> {

  private static final int SIZE_LIMIT = 10 * 1024;

  private final List<Message> messages;

  private int currentIndex;

  ListSplitter(List<Message> messages) {
    this.messages = messages;
  }

  @Override
  public boolean hasNext() {
    return currentIndex < messages.size();
  }

  @Override
  public List<Message> next() {

    //需要获取 message的body topic 还有一些自定义的属性参数
    int nextIndex = currentIndex;
    int totalSize = 0;
    for (; nextIndex < messages.size(); nextIndex++) {
      Message message = messages.get(nextIndex);
      int messageSize = message.getBody().length + message.getTopic().length();
      Map<String, String> properties = message.getProperties();
      Iterator<Entry<String, String>> iterator = properties.entrySet().iterator();
      while (iterator.hasNext()) {
        messageSize += iterator.next().getKey().length() + iterator.next().getValue().length();
      }
      //发送消息会有一些默认的日志大小 20用来存储这些
      messageSize = messageSize + 20;
      if (messageSize > SIZE_LIMIT) {
        //第一次发送就超出限制，直接跳过这条消息继续扫描
        if (nextIndex - currentIndex == 0) {
          nextIndex++;
        }
        break;
      }
      //如果当前发送列表已经超出了最大限制 暂停添加消息
      if (messageSize + totalSize > SIZE_LIMIT) {
        break;
      } else {
        totalSize += messageSize;
      }
    }
    List<Message> messages = this.messages.subList(currentIndex, nextIndex);
    //当前消息发送完毕以后 下标替换当前下标
    currentIndex = nextIndex;
    return messages;
  }

}