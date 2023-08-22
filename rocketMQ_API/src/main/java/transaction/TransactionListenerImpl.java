package transaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 本地事物监听器
 * @author DengLei
 * @date 2023/06/30 11:02
 */

public class TransactionListenerImpl implements TransactionListener {

  @Override
  public LocalTransactionState executeLocalTransaction(Message message, Object o) {
    String tags = message.getTags();
    if (StringUtils.contains("TagA", tags)) {
      //可以提交
      return LocalTransactionState.COMMIT_MESSAGE;
    }
    if (StringUtils.contains("TagB", tags)) {
      //异常回滚
      return LocalTransactionState.ROLLBACK_MESSAGE;
    } else {
      //当为无状态 会定时去回查 checkLocalTransaction
      return LocalTransactionState.UNKNOW;
    }
  }

  @Override
  public LocalTransactionState checkLocalTransaction(MessageExt message) {
    String tags = message.getTags();
    if (StringUtils.contains("TagC", tags)) {
      //可以提交
      return LocalTransactionState.COMMIT_MESSAGE;
    }
    if (StringUtils.contains("TagD", tags)) {
      //异常回滚
      return LocalTransactionState.ROLLBACK_MESSAGE;
    } else {
      //当为无状态 会定时去回查 checkLocalTransaction
      return LocalTransactionState.UNKNOW;
    }
  }
}
