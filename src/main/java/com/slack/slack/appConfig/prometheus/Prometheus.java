package com.slack.slack.appConfig.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 프로메테우스
 *
 * 어플리케이션의 수치 데이터를 측정할 수 있도록 돕는 도구
 * 사용법은 아래와 같다.
 *
 * // 의존성 주입 필수
 * private final Prometheus prometheus;
 *
 * @GetMapping("/test")
 * public void test() {
 *      // 해당 메소드가 실행될 때마다, 원하는 매트릭 값이 증가(수치 증가)
 *     Prometheus.getCounter().increment();
 * }
 *
 * */
@Component
public class Prometheus {
    private PrometheusMeterRegistry meterRegistry;

    public Prometheus(PrometheusMeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * counter 이란 메트릭 (모니터링 할 수치)을 생성 한 뒤,
     *
     * */
    private Counter counter;

    /**
     * @PostConstruct => WAS가 실행될 때 같이 실행되는 메소드
     * */
    @PostConstruct
    public void init() {
        counter = meterRegistry.counter("api.call.count");
    }

    public Counter getCounter() {
        return counter;
    }

}
