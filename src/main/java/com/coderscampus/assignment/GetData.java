package com.coderscampus.assignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class GetData {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        Assignment8 assignment8 = new Assignment8();

        int numberOfCores = Runtime.getRuntime().availableProcessors();
        int poolSize = numberOfCores * 100;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        List<CompletableFuture<List<Integer>>> futures = new ArrayList<>();

        for (int count = 0; count < 1000; count++) {
            CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return assignment8.getNumbers();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new ArrayList<>();
                }
            }, executor);
            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<List<Integer>>> allNumbersFuture = allFutures.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );

        List<List<Integer>> allNumbers = allNumbersFuture.get();

        ConcurrentHashMap<Integer, Integer> numberFrequencies = new ConcurrentHashMap<>();
        for (List<Integer> numberList : allNumbers) {
            for (Integer number : numberList) {
                numberFrequencies.merge(number, 1, Integer::sum);
            }
        }

        numberFrequencies.forEach((number, frequency) -> {
            System.out.println(number + " : " + frequency);
        });

        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        long endTime = System.currentTimeMillis(); // * STOP TIMER

        // * CALCULATE SECONDS
        long totalTimeMillis = endTime - startTime;
        long totalTimeSeconds = totalTimeMillis / 1_000;
        long remainingMillis = totalTimeMillis % 1_000;

        System.out.println("Total execution time: " + totalTimeSeconds + " seconds and " + remainingMillis + " milliseconds");

    }

}
