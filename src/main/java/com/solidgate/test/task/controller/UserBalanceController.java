package com.solidgate.test.task.controller;

import com.solidgate.test.task.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/balance")
public class UserBalanceController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<String> updateUserBalance(@RequestBody Map<Integer, Integer> userBalanceUpdateRequest) {
        userService.updateUserBalances(userBalanceUpdateRequest);

        return ResponseEntity.ok().build();
    }
}
