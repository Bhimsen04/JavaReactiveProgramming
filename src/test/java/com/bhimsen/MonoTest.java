package com.bhimsen;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

    @Test
    public void test() {
        log.info("Everything working as intended");
    }

    @Test
    public void monoSubscriber() {
        String name = "Bhimsen Garg";
//        Mono<String> mono = Mono.just(name);  // this is a publisher
//        log.info("Mono {}" , mono);   // Mono MonoJust
        // if we need info of publisher then we need to subscribe

        Mono<String> mono = Mono.just(name).log();
        mono.subscribe();
        log.info("-----------------------------");
        StepVerifier.create(mono)
                .expectNext(name)
                .verifyComplete();

    }

    @Test
    public void monoSubscriberConsumer() {
        String name = "Bhimsen Garg";
        Mono<String> mono = Mono.just(name).log();
        mono.subscribe(s -> log.info("Value {}", s));
    }

    @Test
    public void monoSubscriberConsumerError() {
        String name = "Bhimsen Garg";
        Mono<String> mono = Mono.just(name)
                .map(s -> {
                    throw new RuntimeException("geeeeeting error");
                });
        mono.subscribe(s -> log.info("Value {}", s), s -> log.error("bad happened"));
        mono.subscribe(s -> log.info("Value {}", s), Throwable::getStackTrace);
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();

    }

    @Test
    public void monoSubscriberConsumerComplete() {
        String name = "Bhimsen Garg";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);
        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"));
    }

    @Test
    public void monoSubscriberConsumerSubscriptionCancel() {
        String name = "Bhimsen Garg";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);
        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"),
                Subscription::cancel);   // it will clear the resources
    }

    @Test
    public void monoSubscriberConsumerSubscriptionRequest() {
        String name = "Bhimsen Garg";
        Mono<String> mono = Mono.just(name)
                .log()
                .map(String::toUpperCase);
        mono.subscribe(s -> log.info("Value {}", s),
                Throwable::printStackTrace,
                () -> log.info("FINISHED!"),
                subscription -> subscription.request(5));   // it will clear the resources
    }

}
