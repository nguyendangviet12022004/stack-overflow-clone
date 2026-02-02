package com.sukhoi.mail;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
	"spring.rabbitmq.host=localhost",
	"spring.rabbitmq.port=5672",
	"spring.mail.host=localhost",
	"spring.mail.port=1025",
	"eureka.client.enabled=false",
	"spring.cloud.config.enabled=false"
})
class MailApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		assertNotNull(applicationContext);
	}

	@Test
	void mailApplication_ShouldHaveEnableRabbitAnnotation() {
		assertThat(MailApplication.class.isAnnotationPresent(EnableRabbit.class)).isTrue();
	}

	@Test
	void mailApplication_ShouldHaveSpringBootApplicationAnnotation() {
		assertThat(MailApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class)).isTrue();
	}

	@Test
	void applicationContext_ShouldContainMailServiceBean() {
		assertThat(applicationContext.containsBean("mailServiceImp")).isTrue();
	}

	@Test
	void applicationContext_ShouldContainAmqpConfigBean() {
		assertThat(applicationContext.containsBean("amqpConfig")).isTrue();
	}

	@Test
	void applicationContext_ShouldContainMessageReceiverBean() {
		assertThat(applicationContext.containsBean("messageReceiver")).isTrue();
	}

	@Test
	void applicationContext_ShouldContainTestControllerBean() {
		assertThat(applicationContext.containsBean("testController")).isTrue();
	}

	@Test
	void main_ShouldNotThrowException() {
		// This test ensures the main method exists and can be called
		// We're not actually running it to avoid starting the application
		assertThat(MailApplication.class.getDeclaredMethods())
			.anyMatch(method ->
				method.getName().equals("main") &&
				method.getParameterCount() == 1 &&
				method.getParameterTypes()[0].equals(String[].class)
			);
	}

	@Test
	void applicationContext_ShouldHaveRabbitConnectionFactoryBean() {
		assertThat(applicationContext.containsBean("rabbitConnectionFactory") ||
				   applicationContext.containsBean("connectionFactory")).isTrue();
	}

	@Test
	void applicationContext_ShouldHaveJavaMailSenderBean() {
		assertThat(applicationContext.containsBean("mailSender") ||
				   applicationContext.getBeansOfType(org.springframework.mail.javamail.JavaMailSender.class).size() > 0)
			.isTrue();
	}

	@Test
	void applicationContext_ShouldLoadSuccessfully() {
		assertThat(applicationContext).isNotNull();
		assertThat(applicationContext.getId()).isNotNull();
	}

	@Test
	void mailApplication_ShouldHavePublicMainMethod() throws NoSuchMethodException {
		var mainMethod = MailApplication.class.getMethod("main", String[].class);
		assertThat(mainMethod).isNotNull();
		assertThat(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers())).isTrue();
		assertThat(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers())).isTrue();
	}

	@Test
	void applicationContext_ShouldContainAmqpExchangeBean() {
		assertThat(applicationContext.containsBean("userTopicExchange")).isTrue();
	}

	@Test
	void applicationContext_ShouldContainAmqpQueueBean() {
		assertThat(applicationContext.containsBean("mailActiveAccountQueue")).isTrue();
	}

	@Test
	void applicationContext_ShouldContainAmqpBindingBean() {
		assertThat(applicationContext.containsBean("mailActivateAccountBinding")).isTrue();
	}
}