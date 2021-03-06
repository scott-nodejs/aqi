package com.aqi.service.imp;

import com.aqi.amqp.RabbitMqConfig;
import com.aqi.entity.ConsumerAqi;
import com.aqi.entity.UrlEntity;
import com.aqi.service.SendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SendServiceImpl implements SendService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RabbitTemplate template;

    /**
     * 发送消息
     *
     */
    @Override
    public boolean send(UrlEntity urlEntity, long times){
        template.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTINGKEY, urlEntity,new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(String.valueOf(times*1000));
                return message;
            }
        });
//        template.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                if (!ack) {
//                    logger.info("send message failed: " + cause); //+ correlationData.toString());
//                    throw new RuntimeException("send error " + cause);
//                } else {
//                    logger.info("send to broke ok" + correlationData.getId());
//                }
//            }
//        });

        return true;
    }

    @Override
    public boolean sendCity(String key, UrlEntity urlEntity, long times){
        template.convertAndSend(RabbitMqConfig.EXCHANGE, key, urlEntity, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration(String.valueOf(times*1000));
                return message;
            }
        });
        return true;
    }

    @Override
    public boolean sendPriorityMessage(String key, UrlEntity urlEntity, long times, int priority) {
        template.convertAndSend(RabbitMqConfig.EXCHANGE, key, urlEntity, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setPriority(priority);
                message.getMessageProperties().setExpiration(String.valueOf(times*1000));
                return message;
            }
        });
        return true;
    }

    @Override
    public boolean sendAqiConsumer(String key, ConsumerAqi consumerAqi) {
        template.convertAndSend(RabbitMqConfig.EXCHANGE, key, consumerAqi);
        return true;
    }

    private Message buildMessage(UrlEntity urlEntity) throws Exception {
        Message message = MessageBuilder.withBody(urlEntity.toString().getBytes())
                .setMessageId(UUID.randomUUID().toString()).setContentType("application/json").build();
        return message;
    }
}
