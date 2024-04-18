package com.cg.account;

import com.cg.account.command.interceptor.AccountInterceptor;
import com.cg.account.posting.command.interceptor.PostingInterceptor;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.commandhandling.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class,args);
    }

    /**
     * Register the AccountInterceptor and PostingInterceptor
     * @param context
     * @param commandBus
     */
    @Autowired
    public void registerAccountInterceptor(ApplicationContext context,
                                           CommandBus commandBus) {
        commandBus.registerDispatchInterceptor(context.getBean(AccountInterceptor.class));
        commandBus.registerDispatchInterceptor(context.getBean(PostingInterceptor.class));
    }
}
