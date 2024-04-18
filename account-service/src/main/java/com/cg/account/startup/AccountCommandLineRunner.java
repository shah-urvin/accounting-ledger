package com.cg.account.startup;

import com.cg.account.command.OpenAccountCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountCommandLineRunner implements CommandLineRunner {

    private final Environment envireonment;
    private final CommandGateway commandGateway;

    public AccountCommandLineRunner(Environment environment,CommandGateway commandGateway) {
        this.envireonment = environment;
        this.commandGateway = commandGateway;
    }

    @Override
    public void run(String... args) throws Exception {
        commandGateway.send(OpenAccountCommand.builder().accountId(UUID.randomUUID().toString()).build());
    }
}
