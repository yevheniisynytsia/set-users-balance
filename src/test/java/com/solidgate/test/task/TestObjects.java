package com.solidgate.test.task;

import java.util.HashMap;
import java.util.Map;

public class TestObjects {

    public static Map<Integer, Integer> mapWithTwentyUsers() {
        Map<Integer, Integer> userBalancesRequest = new HashMap<>();
        for (int i = 1; i <= 20; i++) {
            userBalancesRequest.put(i, i * 100);
        }
        return userBalancesRequest;
    }
}
