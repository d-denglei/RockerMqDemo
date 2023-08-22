package transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author DengLei
 * 消息事物生产者
 * @date 2023/06/30 10:46
 */

public class TransactionProducer {

    public static void main(String[] args) throws MQClientException, InterruptedException {
        TransactionMQProducer producer =
            new TransactionMQProducer("TransactionProducer");
        producer.setNamesrvAddr("172.20.12.150:9876");
        //使用本地事物监听器
        producer.setTransactionListener(new TransactionListenerImpl());

        //异步提交事物状态，提升性能
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("ExecutorService-dengLei");
                return thread;
            }
        });

        //异步提交
        producer.setExecutorService(threadPoolExecutor);

        producer.start();
        String[] tags = new String[]{"TagA","TagB","TagC","TagD","TagE"};
        for (int i = 0; i < 10; i++) {
            Message message = new Message("Transaction",
                tags[i % tags.length],
                (tags[i % tags.length]+"_TransactionProducer").getBytes(
                StandardCharsets.UTF_8));
            TransactionSendResult transactionSendResult = producer.sendMessageInTransaction(message,
                null);
            System.out.println("消息发送成功_" + transactionSendResult);

            Thread.sleep(10);
        }
        Thread.sleep(100000);
        producer.shutdown();
    }
}
