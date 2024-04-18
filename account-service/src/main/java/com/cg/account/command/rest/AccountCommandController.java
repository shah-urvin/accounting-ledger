package com.cg.account.command.rest;

import com.cg.account.dto.AccountDTO;
import com.cg.account.exception.AccountNotFoundException;
import com.cg.account.command.service.AccountCommandService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountCommandController {
    @Autowired
    private AccountCommandService accountCommandService;

    private static final Logger logger = LoggerFactory.getLogger(AccountCommandController.class);

    @PatchMapping("/{accountId}/status")
    public ResponseEntity accountStatusChange(@PathVariable(value = "accountId") String accountId, @Valid @RequestBody AccountDTO accountDTO){
        logger.info("accountStatusChange from controller invoked. {}",accountId);
        accountDTO.setAccountId(accountId);
        try {
            return ResponseEntity.ok(accountCommandService.accountStatusChange(accountDTO));
        } catch(AccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
