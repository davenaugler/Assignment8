package com.coderscampus.assignment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingByConcurrent;

public class GetData {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long startTime = System.currentTimeMillis();

        Assignment8 assignment8 = new Assignment8();

        int numberOfCores = Runtime.getRuntime().availableProcessors();
        int poolSize = numberOfCores * 8;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        List<CompletableFuture<List<Integer>>> futures = new ArrayList<>();

        for (int count = 0; count < 1000; count++) {
            futures.add(createFuture(assignment8, executor));
        }

        List<List<Integer>> allNumbers = getAllNumbers(futures);

        ConcurrentMap<Integer, Long> numberFrequencies = allNumbers.parallelStream()
                .flatMap(List::stream)
                .collect(groupingByConcurrent(Function.identity(), counting()));

        numberFrequencies.forEach((number, frequency) -> System.out.println(number + " : " + frequency));

        shutdownExecutor(executor);

        printExecutionTime(startTime);
    }

    private static CompletableFuture<List<Integer>> createFuture(Assignment8 assignment8, ExecutorService executor) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return assignment8.getNumbers();
            } catch (RuntimeException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }, executor);
    }

    private static List<List<Integer>> getAllNumbers(List<CompletableFuture<List<Integer>>> futures) throws InterruptedException, ExecutionException {
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        return allFutures.thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .get();
    }

    private static void shutdownExecutor(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            List<Runnable> droppedTasks = executor.shutdownNow();
            if (!droppedTasks.isEmpty()) {
                System.err.println("Executor did not terminate in the specified time. " + droppedTasks.size() + " tasks were dropped.");
            }
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate after forceful shutdown.");
            }
        }
    }

    private static void printExecutionTime(long startTime) {
        long endTime = System.currentTimeMillis();
        long totalTimeMillis = endTime - startTime;
        long totalTimeSeconds = totalTimeMillis / 1_000;
        long remainingMillis = totalTimeMillis % 1_000;
        System.out.println("Total execution time: " + totalTimeSeconds + " seconds and " + remainingMillis + " milliseconds");
    }
}
