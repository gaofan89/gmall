package com.gaofan.gmall.payment.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class ProducerBossTopics {

    public static void main(String[] args) {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.91.135:61616");
        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Topic topic = session.createTopic("gaofan");

            MessageProducer producer = session.createProducer(topic);
            TextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText("你好吗，我来自中国，可以跟你认识一下吗");
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);

            session.commit();
            connection.close();




        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
