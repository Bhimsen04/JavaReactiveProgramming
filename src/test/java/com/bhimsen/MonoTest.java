package com.bhimsen;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class MonoTest {

    // mono: want to stream one or no elements
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

    @Test
    public void monoDoOnMethods() {
        String name = "Rajat Garg";
        Mono<Object> mono = Mono.just(name)
                //.log()
                .map(String::toUpperCase)
                .doOnSubscribe(subscription -> log.info("Subscribed"))     // when someone will subscribe ....  after onSubscribe
                .doOnRequest(longNumber -> log.info("Request received, stating doing something..."))  // before request(unbounded)
                .doOnNext(s -> log.info("value is here. Executing doOnNext {}", s)) // after request(unbounded) and onNext(Rajat Garg)
                // .doOnNext(s -> log.info("value is here. Executing doOnNext {}", s)) // after request(unbounded) and onNext(Rajat Garg)
                .flatMap(s -> Mono.empty())
                .doOnNext(s -> log.info("value is here. Executing doOnNext {}", s)) // after request(unbounded) and onNext(Rajat Garg)
                .doOnSuccess(s -> log.info("doOnSuccess executed {}", s));
        mono.subscribe(s -> log.info("name {}", s),
                Throwable::printStackTrace,
                () -> log.info("Finished"));
    }

    @Test
    public void monoDoOnError() {
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception!!!!!"))
                .doOnError(e -> MonoTest.log.error("Error message: {}", e.getMessage()))
                .doOnNext(s -> log.info("executing this doOnNext"));     // will not execute bcz getting error

        StepVerifier.create(error).expectError(IllegalArgumentException.class).verify();
    }

    // we can return only mono
    @Test
    public void monoDoOnErrorResume() {
        int a = 20;
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception!!!!!"))
                .doOnError(e -> MonoTest.log.error("Error message: {}", e.getMessage()))
                .onErrorResume(s -> {                                               // will execute even getting error
                    log.info("Inside onErrorResume");
                    System.out.println(a + 50 + " value");
                    return Mono.just("Bhimsen G");   // will return the Mono
                });

        StepVerifier.create(error).expectNext("Bhimsen G").verifyComplete();
    }

    // we can return any , not only mono
    @Test
    public void monoDoOnErrorReturn() {
        int a = 20;
        Mono<Object> error = Mono.error(new IllegalArgumentException("Illegal argument exception!!!!!"))
                .doOnError(e -> MonoTest.log.error("Error message: {}", e.getMessage()))
                .onErrorReturn("empty")
                .onErrorResume(s -> {                                               // now it will be ignored
                    log.info("Inside onErrorResume");
                    System.out.println(a + 50 + " value");
                    return Mono.just("Bhimsen G");
                });
//                .onErrorReturn("empty");

        StepVerifier.create(error).expectNext("empty").verifyComplete();
    }
}
