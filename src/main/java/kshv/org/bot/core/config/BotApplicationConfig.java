package kshv.org.bot.core.config;

import kshv.org.bot.core.BotApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BotApplicationConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    Logger logger() {
        return LoggerFactory.getLogger(BotApplication.class);
    }

}
