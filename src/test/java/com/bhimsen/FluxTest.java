package com.bhimsen;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@Slf4j
public class FluxTest {

    // Flux: want to return more than one element

    @Test
    public void fluxSubscriber() {
        Flux<String> flux = Flux.just("Bhimsen", "Rajat").log();
        StepVerifier.create(flux).expectNext("Bhimsen", "Rajat").verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbers() {
        Flux<Integer> flux = Flux.range(1, 5).log();
        flux.subscribe(i -> log.info("number {}", i));
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5).verifyComplete();
    }

    @Test
    public void fluxSubscriberFromList() {
        // Flux<List<Integer>> flux = Flux.just(List.of(1,2,3,4,5)).log();
        // But we need flux of integers
        Flux<Integer> flux = Flux.fromIterable(List.of(1, 2, 3, 4, 15)).log();
        flux.subscribe(i -> log.info("number {}", i));
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 15).verifyComplete();
    }

    @Test
    public void fluxSubscriberNumbersError() {
        Flux<Integer> flux = Flux.range(1, 5)
                .log()
                .map(i -> {
                    if (i == 4) throw new ArrayIndexOutOfBoundsException("Array out of Bound");
                    return i;
                });
        flux.subscribe(i -> log.info("i value: ", i),
                Throwable::printStackTrace,
                () -> log.info("Done!!!!!!!!!!!"),
                subscription -> subscription.request(3));

        StepVerifier.create(flux).expectNext(1, 2, 3).expectError(IndexOutOfBoundsException.class).verify();

    }

    // I want a pair of numbers being returned
    @Test
    public void fluxSubscriberNumberUglyBackpressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();
        flux.subscribe(new Subscriber<>() {

            private int count = 0;
            private Subscription subscription;
            private final int requestCount = 2;


            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(requestCount);
            }

            @Override
            public void onNext(Integer integer) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    subscription.request(requestCount);
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).verifyComplete();

    }

    // for backpressure
    @Test
    public void fluxSubscriberPrettyBackPressure() {
        Flux<Integer> flux = Flux.range(1, 5).log().limitRate(2);
        flux.subscribe(i -> log.info("number {}", i));
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5).verifyComplete();
    }

    public void fluxSubscriberNumberNotUglyBackpressure() {
        Flux<Integer> flux = Flux.range(1, 10)
                .log();
        flux.subscribe(new BaseSubscriber<>() {

            private int count = 0;
            private final int requestCount = 2;

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(requestCount);
            }

            @Override
            protected void hookOnNext(Integer value) {
                count++;
                if (count >= requestCount) {
                    count = 0;
                    request(requestCount);
                }
            }
        });
        StepVerifier.create(flux).expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).verifyComplete();
    }

    @Test
    public void fluxSubscriberIntervalOne() throws InterruptedException {
//        interval rins in a different thread
//        Flux<Long> interval = Flux
//                .interval(Duration.ofDays(1));     // every day it will print 1 number i.e 0
        Flux<Long> interval = Flux
                .interval(Duration.ofMillis(100))
                .take(10)          // optional
                .log();
        interval.subscribe(i -> {
            log.info("Number {}", i);
        });
        // if thread will not sleep, then no able to get the output bcz immediately main thread will exit
        // so now main thread is sleeping for 500ms then we can able to see 5 elements(500/100), after that main thread will exit.
        Thread.sleep(500);
    }

    @Test
    public void connectableFlux() throws InterruptedException {
        ConnectableFlux<Integer> connectableFlux = Flux.range(1, 10)
//                .log()
                .delayElements(Duration.ofMillis(200))
                .publish();
        connectableFlux.connect();    // connected by daemon thread

        log.info("Thread sleeping for 400ms");
        Thread.sleep(400);    // able to see 2 elements here .. 400/200
        connectableFlux.subscribe(i -> log.info("Sub1 number: {}", i));

        log.info("Thread sleeping for 600ms");
        Thread.sleep(600);    // able to see 3 elements here ... 600/200
        connectableFlux.subscribe(i -> log.info("Sub2 number: {}", i));
    }
}
