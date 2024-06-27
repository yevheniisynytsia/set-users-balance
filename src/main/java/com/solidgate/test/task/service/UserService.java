package com.solidgate.test.task.service;

import com.solidgate.test.task.model.User;
import com.solidgate.test.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class UserService {
    private final int batchSize = 5000;
    private final int chunkSize = 10000;
    private final String UPDATE_USER_BALANCE_QUERY = """
        UPDATE users
        SET balance = ?
        WHERE id = ?
        """;

    private final ExecutorService executorService = Executors.newFixedThreadPool(20);
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Transactional
    public void updateUserBalances(Map<Integer, Integer> userBalanceUpdateRequest) {
        long start = System.currentTimeMillis();
        System.out.println("Start time: " + LocalDateTime.now());

        List<Integer> userIds = userBalanceUpdateRequest.keySet().stream().toList();

        System.out.println(userIds.size() + " users to update");
        List<List<Integer>> userChunks = IntStream.range(0, userIds.size())
            .boxed()
            .collect(Collectors.groupingBy(i -> i / chunkSize))
            .values().stream()
            .map(l -> l.stream()
                .map(userIds::get)
                .collect(Collectors.toList()))
            .toList();

        for (List<Integer> chunk : userChunks) {
            System.out.println("Fetching users chunk starting at: " + chunk.get(0) + " at " + LocalDateTime.now());
            List<User> users = userRepository.findByIds(chunk);
            System.out.println("User count: " + users.size() + " at " + LocalDateTime.now());

            List<CompletableFuture<Void>> futures = IntStream.range(0, users.size())
                .boxed()
                .collect(Collectors.groupingBy(i -> i / batchSize))
                .values().stream()
                .map(indices -> CompletableFuture.runAsync(() -> {
                    List<User> batchUsers = indices.stream().map(users::get).collect(Collectors.toList());
                    jdbcTemplate.batchUpdate(UPDATE_USER_BALANCE_QUERY, batchUsers, batchUsers.size(), (ps, user) -> {
                        ps.setInt(1, userBalanceUpdateRequest.get(user.getId()));
                        ps.setInt(2, user.getId());
                    });
                }, executorService))
                .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        System.out.println("Finish time: " + LocalDateTime.now());
        executorService.shutdown();
        long end = System.currentTimeMillis();
        System.out.println("Time taken in seconds: " + (end - start) / 1000);
    }
}