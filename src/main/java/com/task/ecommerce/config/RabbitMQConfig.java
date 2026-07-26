package com.task.ecommerce.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String EMAIL_ROUTING_KEY = "email.send";

    public static final String EMAIL_DLQ = "email.queue.dlq";
    public static final String EMAIL_DLX = "email.exchange.dlx0";

    @Bean
    public Queue emailQueue() {
        return  QueueBuilder.durable(EMAIL_QUEUE)
                .withArgument("x-dead-letter-exchange", EMAIL_DLX)
                .withArgument("x-dead-letter-routing-key", "email.failed")
                .build();
    }

    @Bean
    public DirectExchange emailExchange(){
        return new DirectExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Binding emailBinding(){
        return BindingBuilder.bind(emailQueue()).to(emailExchange()).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Queue emailDlq() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    @Bean
    public DirectExchange emailDlx() {
        return new DirectExchange(EMAIL_DLX);
    }

    @Bean
    public Binding emailDlqBinding() {
        return BindingBuilder.bind(emailDlq()).to(emailDlx()).with("email.failed");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
