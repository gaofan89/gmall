package com.gaofan.gmall.util;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.*;

/**
 * @param
 * @return
 */
public class ActiveMQUtil {

    PooledConnectionFactory pooledConnectionFactory=null;


    public  void init(String brokerUrl){
        ActiveMQConnectionFactory activeMQConnectionFactory=new ActiveMQConnectionFactory(brokerUrl);
        pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        pooledConnectionFactory.setExpiryTimeout(2000);
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(10);
        pooledConnectionFactory.setMaxConnections(30);
        pooledConnectionFactory.setReconnectOnException(true);
        System.out.println("初始化mq连接池");
    }

    public Connection getConnection(){

        Connection connection = null;
        try {
            connection = pooledConnectionFactory.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connection;
    }


    public void sendMq(String queueName,Message message) throws JMSException {

        Connection connection = getConnection();
        connection.start();

        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

        Queue queue = session.createQueue(queueName);

        MessageProducer producer = session.createProducer(queue);

//        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
//        textMessage.setText("");

        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        producer.send(message);

        session.commit();
        connection.close();
    }


}
