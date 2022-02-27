package com.bhim.BRS.Logic;

import com.bhim.BRS.Entites.Activity;
import com.bhim.BRS.Entites.ErrorMsg;
import com.bhim.BRS.Entites.Response;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InsuranceLicenseDataProcessor_Modify {
    public void callApi(Activity activity, Response response) throws InterruptedException {
        log.info("Inside callApi()");
        Mono<Activity> execution_done = Mono.just(response)
                .timeout(Duration.ofHours(1))
                .doOnError(error -> {
                    log.error("Getting error inside doOnError(), setting system unavailable", error);
                })
                .doOnTerminate(() ->
                        log.info("Inside doOnTerminate() "))
                .defaultIfEmpty(new Response())
                .map(rsp -> transform(rsp, activity))
                .doOnNext(errorMessages ->
                        log.info("Response in error messages= {}", errorMessages))
                .map(activity::setLcFaResponseCodes)
                .doFinally(signalType -> {
                    log.info("Execution done");
                });
        execution_done.subscribe(s -> log.info("final output: {}", s));
        //Thread.sleep(5000);
    }

    public void validate(Activity activity) {
        log.info("Inside validate()");
        if (Objects.isNull(activity.getLcFaResponseCodes())) {
            log.info("!!!!!!!!!!!!validate system unavailable!!!!!!!!!!!!!");
        }
    }

    public List<String> transform(final Response response, final Activity activity) {
        log.info("Inside transform()");
        if (Objects.isNull(response.getErrorMsgs())
                || (Objects.deepEquals(response.getResult(), "N") && response.getErrorMsgs().isEmpty()))
            validate(activity);
        return Optional.ofNullable(response.getErrorMsgs())
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.groupingBy(ErrorMsg::getErrCode,
                        Collectors.mapping(ErrorMsg::getErrMsg, Collectors.toList())))
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
