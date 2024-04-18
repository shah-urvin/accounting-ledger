package com.cg.account.posting.command.rest;

import com.cg.account.dto.PostingDTO;
import com.cg.account.posting.command.service.PostingCommandService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class PostingCommandController {

    @Autowired
    private PostingCommandService postingCommandService;

    @PostMapping("/{accountId}/posting")
    public ResponseEntity createPosting(@PathVariable(value = "accountId") String accountId, @Valid @RequestBody List<PostingDTO> lstPostingDTO) {
        lstPostingDTO.stream().forEach(postingDTO -> postingDTO.setAccountId(accountId));
        postingCommandService.createPosting(lstPostingDTO);
        return ResponseEntity.accepted().build();
    }

    @PutMapping("/{accountId}/posting")
    public ResponseEntity updatePostingStatus(@PathVariable(value = "accountId") String accountId, @Valid @RequestBody List<PostingDTO> lstPostingDTO)   {
        lstPostingDTO.stream().forEach(postingDTO -> postingDTO.setAccountId(accountId));
        return ResponseEntity.accepted().build();
    }
}
