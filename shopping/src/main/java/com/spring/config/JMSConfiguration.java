package com.spring.config;

import javax.jms.ConnectionFactory;

import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JMSConfiguration {

	// JMS Listener factory
	@Bean
	public JmsListenerContainerFactory<?> jmsFactory(
			ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {

		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	// JSON message converter
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter convert = new MappingJackson2MessageConverter();
		convert.setTargetType(MessageType.TEXT);
		convert.setTypeIdPropertyName("_type");
		return convert;
	}

	// JmsTemplate for producing messages
	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setMessageConverter(converter);
		return template;
	}
}
