package com.aqi.amqp;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@EnableRabbit
@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE = "spring.boot.direct";
    public static final String ROUTINGKEY_FAIL = "spring.boot.routingKey.failure";
    public static final String ROUTINGKEY = "spring.boot.routingKey";
    public static final String ROUTINGKEY_HALF_HOUR = "half.hour.routingKey";
    public static final String ROUTINGKEY_CONSUMER_AQI = "routingKey_consumer_aqi";
    public static final String ROUTINGKEY_RAND_AQI = "routingKey_rand_aqi";
    public static final String ROUTINGKEY_AREA_ALL_AQI = "routingKey_area_all_aqi";
    public static final String QUEUE_NAME = "spring.demo";
    public static final String QUEUE_NAME_FAIL = "spring.demo.failure";
    public static final String QUEUE_HALF_HOUR = "aqi.city.or.area.half.hour";
    public static final String QUEUE_CONSUMER_AQI = "queue_consumer_aqi";
    public static final String QUEUE_RAND_AQI = "queue_rand_aqi";
    public static final String QUEUE_AREA_ALL_AQI = "queue_area_all_aqi";

    //RabbitMQ的配置信息
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private Integer port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;


    //建立一个连接容器，类型数据库的连接池
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);// 确认机制
//        connectionFactory.setPublisherReturns(true);
        //发布确认，template要求CachingConnectionFactory的publisherConfirms属性设置为true
        return connectionFactory;
    }

    // RabbitMQ的使用入口
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    //必须是prototype类型
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(this.connectionFactory());
        template.setMessageConverter(this.jsonMessageConverter());
        template.setMandatory(true);
        return template;
    }

    /**
     * 交换机
     * 针对消费者配置
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * DirectExchange:多关键字匹配
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    /**
     * 队列
     *
     * @return
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, true); //队列持久
    }

    @Bean
    public Queue queueFail() {
        return new Queue(QUEUE_NAME_FAIL, true); //队列持久
    }

    @Bean
    public Queue queueConsumer() {
        return new Queue(QUEUE_CONSUMER_AQI, true); //队列持久

    }

    @Bean
    public Queue queueRand() {
        return new Queue(QUEUE_RAND_AQI, true); //队列持久

    }

    @Bean
    public Queue queueHalfHour() {
        return new Queue(QUEUE_HALF_HOUR, true); //队列持久

    }

    @Bean
    public Queue queueAreaAll() {
        return new Queue(QUEUE_AREA_ALL_AQI, true); //队列持久
    }

//    @Bean
//    public Queue queueAreaAll() {
//        Map<String, Object> args= new HashMap<>();
//        args.put("x-max-priority", 5);
//        return new Queue(QUEUE_AREA_ALL_AQI, true, false,false, args); //队列持久
//    }


    /**
     * 绑定
     *
     * @return
     */
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue()).to(exchange()).with(RabbitMqConfig.ROUTINGKEY);
    }

    @Bean
    public Binding bindingAreaAll(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queueAreaAll()).to(exchange()).with(RabbitMqConfig.ROUTINGKEY_AREA_ALL_AQI);
    }

    @Bean
    public Binding bindingFail(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queueFail()).to(exchange()).with(RabbitMqConfig.ROUTINGKEY_FAIL);
    }

    @Bean
    public Binding bindingHalfHour(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queueHalfHour()).to(exchange()).with(RabbitMqConfig.ROUTINGKEY_HALF_HOUR);
    }

    @Bean
    public Binding bindingConsumerAqi(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queueConsumer()).to(exchange()).with(RabbitMqConfig.ROUTINGKEY_CONSUMER_AQI);
    }

    @Bean
    public Binding bindingRandAqi(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queueRand()).to(exchange()).with(RabbitMqConfig.ROUTINGKEY_RAND_AQI);
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

//    @Bean
//    public CharacterEncodingFilter characterEncodingFilter() {
//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        return filter;
//    }

}
