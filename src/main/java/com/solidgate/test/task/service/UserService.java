package com.solidgate.test.task.service;

import com.solidgate.test.task.model.User;
import com.solidgate.test.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    @Value("${user.balanceUpdate.batchSize}")
    private int batchSize;

    private final UserRepository userRepository;

    @Transactional
    public void updateUserBalances(Map<Integer, Integer> userBalances) {
        List<Integer> userIds = new ArrayList<>(userBalances.keySet());
        int totalUsers = userIds.size();

        for (int i = 0; i < totalUsers; i += batchSize) {
            int endIndex = Math.min(i + batchSize, totalUsers);
            List<Integer> batchUserIds = userIds.subList(i, endIndex);

            List<User> users = userRepository.findByIds(batchUserIds);
            users.forEach(user -> user.setBalance(userBalances.get(user.getId())));

            userRepository.saveAll(users);
        }
    }
}
