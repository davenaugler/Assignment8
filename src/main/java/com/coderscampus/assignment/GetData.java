package com.coderscampus.assignment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class GetData {
    public static void main(String[] args) throws InterruptedException, ExecutionException {

            Assignment8 assignment8 = new Assignment8();
            List<CompletableFuture<List<Integer>>> futures = new ArrayList<>();

            // Assuming getNumbers() needs to be called 1000 times
            for (int count = 0; count < 1000; count++) {
                CompletableFuture<List<Integer>> future = CompletableFuture.supplyAsync(assignment8::getNumbers);
                futures.add(future);
            }

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            CompletableFuture<List<List<Integer>>> allNumbersFuture = allFutures.thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList())
                    );

            // Get the combined result
            List<List<Integer>> allNumbers = allNumbersFuture.get();

            // Count the frequency of each number
        Map<Integer, Integer> numberFrequencies = new HashMap<>();
        for (List<Integer> numberList : allNumbers) {
            for (Integer number : numberList) {
                numberFrequencies.put(number, numberFrequencies.getOrDefault(number, 0) + 1);
            }
        }

        // Output the frequencies
        numberFrequencies.forEach((number, frequency) -> {
                    System.out.println("Number " + number + " appears " + frequency + " times");
                });

    }
//        Assignment8 assignment = new Assignment8();
//        ExecutorService executor = Executors.newFixedThreadPool(10); // Using 10 threads for example
//
//        List<Future<List<Integer>>> futures = new ArrayList<>();
//
//        for (int i = 0; i < 1000; i++) {
//            futures.add(executor.submit(assignment::getNumbers));
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.HOURS);
//
//        ConcurrentHashMap<Integer, Long> frequencyMap = new ConcurrentHashMap<>();
//        for (Future<List<Integer>> future : futures) {
//            List<Integer> numbers = future.get();
//            for (Integer number : numbers) {
//                frequencyMap.compute(number, (key, val) -> (val == null) ? 1 : val + 1);
//            }
//        }
//
//        frequencyMap.forEach((key, value) -> System.out.println(key + "=" + value));
//    }
}
