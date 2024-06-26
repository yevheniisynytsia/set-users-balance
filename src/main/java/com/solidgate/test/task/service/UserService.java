package com.solidgate.test.task.service;

import com.solidgate.test.task.model.User;
import com.solidgate.test.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserBalances(Map<Integer, Integer> userBalances) {
        int batchSize = 1000;

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
