package com.bhimsen;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class OperatorsTest {

    // Mixing asynchronous code with synchronous calls
    // we'll not block main thread, useful when operation IO, requesting from external API's

    @Test
    public void subscribeOnSimple() {
        Flux<Integer> flux = Flux.range(1, 4)
                .map(i -> {     // executed by another thread Ex: boundedElastic-1
                    log.info("Map1 value: {} , Thread: {}", i, Thread.currentThread().getName());
                    return i;
                })
//                .subscribeOn(Schedulers.single())      // creating single thread
//                .subscribeOn(Schedulers.boundedElastic())      // creating single thread
                .subscribeOn(Schedulers.elastic())      // creating single thread
                .map(i -> {    // executed by another thread Ex: boundedElastic-1
                    log.info("Map2 value: {} , Thread: {}", i, Thread.currentThread().getName());
                    return i;
                });
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void publishOnSimple() {
        Flux<Integer> flux = Flux.range(1, 4)
                .map(i -> {    // executed by main thread
                    log.info("Map1 value: {} , Thread: {}", i, Thread.currentThread().getName());
                    return i;
                })
//                .publishOn(Schedulers.single())      // creating single thread
                .publishOn(Schedulers.boundedElastic())      // creating single thread
//                .publishOn(Schedulers.elastic())      // creating single thread
                .map(i -> {   // executed by another thread Ex: boundedElastic-1
                    log.info("Map2 value: {} , Thread: {}", i, Thread.currentThread().getName());
                    return i;
                });
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    @Test
    public void subscribeAndPublishOnSimple() {
        Flux<Integer> flux = Flux.range(1, 4)
                .subscribeOn(Schedulers.single())
                .map(i -> {
                    log.info("Map1 value: {} , Thread: {}", i, Thread.currentThread().getName());
                    return i;
                })
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    log.info("Map2 value: {} , Thread: {}", i, Thread.currentThread().getName());
                    return i;
                });
        StepVerifier.create(flux)
                .expectSubscription()
                .expectNext(1, 2, 3, 4)
                .verifyComplete();
    }

    // used to execute blocking io or call in background thread
    @Test
    public void subscribeOnIO() throws InterruptedException {
        Mono<List<String>> list = Mono.fromCallable(() -> Files.readAllLines(Path.of("test-file")))
                .log()
                .subscribeOn(Schedulers.boundedElastic());

        list.subscribe(s -> log.info("List: {}", s));

        Thread.sleep(20000);

        StepVerifier.create(list)
                .expectSubscription()
                .thenConsumeWhile(l -> {
                    Assertions.assertFalse(l.isEmpty());
                    log.info("Size {} ,", l.size());
                    return true;
                })
                .verifyComplete();
    }
}




