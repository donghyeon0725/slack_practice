π λͺ¨λν°λ§ μμ€ν κ΅¬μΆ
-

* actuator + Prometheusμ μ΄μ©ν΄ λͺ¨λν°λ§ μμ€νμ κ΅¬μΆν  μμ 


<br/>

π actuator λ?
-
* λͺ¨λν°λ§μ μμ½κ² ν  μ μλλ‘ λλ λΌμ΄λΈλ¬λ¦¬λ‘ μΌμ’μ μλ ν¬μΈνΈ(μ‘°μμ μν μ°½κ΅¬)μ΄λ€.
* Chaos Monkey μ μ°λν΄μ, μ νλ¦¬μΌμ΄μμ μ§μ°μ μ£Όκ±°λ κ³ μλ‘ μμΈλ₯Ό λ°μμν€λ κ²μ΄ κ°λ₯νλ€.
* Prometheus μ μ°λν΄μ λͺ¨λν°λ§μ μν λ°μ΄ν°(λ§€νΈλ¦­ λ±λ±)λ₯Ό μμ§νκ² νκ³  λ³λ Document Base DB μ μ μ₯νκ² ν  μ μλ€.
    * Prometheus κ° μμ§ν λ°μ΄ν°λ₯Ό Grafana μ μ°λν΄μ λ°μ΄ν°λ₯Ό DashBoardλ‘ λͺ¨λν°λ§ ν  μ μλλ‘ λ§λ€μλ μλ€. 
    * ν¬μΈνΈ(μ‘°μμ μν μ°½κ΅¬)μ HTTP Request λ₯Ό λ λ € λ΄λΆ DBμ κ°μ μ μ¬νλ λ°©μμΌλ‘ push λ°©μμ΄ μλ λΉκ²¨μ€λ pull λ°©μμ΄λ€.
<br/>

π actuator μ μ©νκΈ° 
-
* λν¬λμ μΆκ° 
```xml
<!-- actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
* μ¬μ€ν ν μλ λ§ν¬ μ€ν

```java
http://localhost:8080/actuator/health
```
> μλ²κ° μ μ μ€ν μ€μΈμ§ νμΈ
 
```java
http://localhost:8080/actuator
```
> μμΈν μ λ³΄ νμΈμ μν΄μ μλ ₯

* λΈμΆν  μ λ³΄λ μλ μ€μ λ΄μ©μ ν΅ν΄ μμ ν  μ μμ 
```java
management:
  endpoints:
    web:
      exposure:
        include: health, info, prometheus
```



<br/>


π Prometheus μ¬μ©ν  μ€λΉνκΈ°
-
μ°Έκ³ λ§ν¬ : <https://badcandy.github.io/2018/12/29/prometheus-practice/>


* SpringBoot 2.0μ΄μλΆν°λ MicrometerλΌλ λ©νΈλ¦­ μμ§μ μ§μνλ€.
* μ΄ μμ§μ μ΄μ©ν΄μ λͺ¨λν°λ§ λ§€νΈλ¦­μ μμ±ν  μ μλλ‘ κ΄λ ¨ λν¬λμ μΆκ°

```xml
<!-- Micrometer core dependecy  -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>
<!-- Micrometer Prometheus registry  -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

* μ€μ  νμΌμ metrics & prometheus μ€μ  μΆκ°
```yaml
management:
  # metrics μ€μ 
  endpoint:
    metrics:
      enabled: true
    # metrics & prometheus μ€μ 
    prometheus:
      enabled: true
  # prometheus μ€μ 
  metrics:
    export:
      prometheus:
        enabled: true
```

* μλ μ£Όμμ μ μν΄μ prometheus λ©νΈλ¦­ λΈμΆ μλν¬μΈνΈκ° μ‘΄μ¬νλμ§ νμΈ 
```java
http://localhost:8080/actuator
```
```java
    "prometheus": {
      "href": "http://localhost:8080/actuator/prometheus",
      "templated": false
    },
```

* μ΄μ΄μ μλν¬μΈνΈμ λ΄μ©μ΄ λΈμΆ λλμ§ νμΈν΄μΌν¨
```java
http://localhost:8080/actuator/prometheus
```

```text
# HELP jvm_buffer_count_buffers An estimate of the number of buffers in the pool
# TYPE jvm_buffer_count_buffers gauge
jvm_buffer_count_buffers{id="direct",} 6.0
jvm_buffer_count_buffers{id="mapped",} 0.0
# HELP process_uptime_seconds The uptime of the Java virtual machine
...
```

* λ§μ½ λΈμΆλμ§ μλ κ²½μ°, plain/textλ₯Ό μ§μνλ μ»¨λ²ν°λ₯Ό μΆκ°ν΄μ€μΌν¨ 
```java
@EnableWebMvc
@Configuration
public class FormatConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // xml ν¬λ©§μ μ§μνλ μ»¨λ²ν°
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        // json ν¬λ©§μ μ§μνλ μ»¨λ²ν°
        converters.add(new MappingJackson2HttpMessageConverter());
        /**
        * μΆκ°ν λΆλΆ
        * plain/text ν¬λ©μ μ§μνλ μ»¨λ²ν°
        */
        converters.add(new StringHttpMessageConverter());
    }
}
```
> prometheus exporterμ κ²½μ° νμ€νΈλ₯Ό plain/text λ‘ return νκΈ° λλ¬Έμ ν΄λΉ μ»¨λ²ν°λ₯Ό μΆκ°ν΄μΌνλ€.

μ°Έκ³  λ§ν¬ : <https://jessyt.tistory.com/52>




<br/>

π Prometheus μ»€μ€ν°λ§μ΄μ§
-
* μνλ νλ(λ©νΈλ¦­κ°)κ°μ μΆκ°νκ³  μμΉνν  μ μλ€.
    * μ€νλ§μ΄ μ κ³΅νλ MeterRegistry λ₯Ό μ£Όμλ°κ³  λ©νΈλ¦­ μΆκ°νκΈ°
    
    ```java
    /**
     * νλ‘λ©νμ°μ€
     *
     * μ΄νλ¦¬μΌμ΄μμ μμΉ λ°μ΄ν°λ₯Ό μΈ‘μ ν  μ μλλ‘ λλ λκ΅¬
     * μ¬μ©λ²μ μλμ κ°λ€.
     *
     * // μμ‘΄μ± μ£Όμ νμ
     * private final Prometheus prometheus;
     *
     * @GetMapping("/test")
     * public void test() {
     *      // ν΄λΉ λ©μλκ° μ€νλ  λλ§λ€, μνλ λ§€νΈλ¦­ κ°μ΄ μ¦κ°(μμΉ μ¦κ°)
     *     Prometheus.getCounter().increment();
     * }
     *
     * */
    @Component
    public class Prometheus {
        private MeterRegistry meterRegistry;
    
        public Prometheus(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }
    
        /**
         * counter μ΄λ λ©νΈλ¦­ (λͺ¨λν°λ§ ν  μμΉ)μ μμ± ν λ€,
         *
         * */
        private Counter counter;
    
        /**
         * @PostConstruct => WASκ° μ€νλ  λ κ°μ΄ μ€νλλ λ©μλ
         * */
        @PostConstruct
        public void init() {
            counter = meterRegistry.counter("api.call.count");
        }
    
        public Counter getCounter() {
            return counter;
        }
    
    }
    ```
  
    * μμμ μΆκ°ν μΉ΄μ΄ν°(λ©νΈλ¦­ = μΈ‘μ ν  λ°μ΄ν°)μ μ΄λ¦μ΄ api.call.countμΈλ°, ν΄λΉ μ΄λ¦μ api_call_count_totalμΌλ‘ λ³νλμ΄ ν΄λΉ μ΄λ¦μ΄ λ§€νΈλ¦­μ μ΄λ¦μ΄ λλ€.
    * http://localhost:8080/actuator/prometheus μμ²­ ν api_call_count_total κ°μ νμΈνλ€.
    * http://localhost:8080/test μ ν΄λΉνλ μ»¨νΈλ‘€λ¬(μμ€ μ£Όμ νμΈ)λ₯Ό μΆκ°νλ€λ©΄ ν΄λΉ urlμ μμ²­νμ λ api_call_count_totalκ°μ΄ νλ μ¦κ°λμ΄ μμ κ²μ  

π Prometheus μ€μΉνκΈ°
-
* [νλ‘λ©νμ°μ€ ννμ΄μ§](https://prometheus.io/download/) μμ μλμ° μ© μμ€λ₯Ό λ΄λ €λ°λλ€.
* λ°μ νμΌμ μμΆμ νκ³  "prometheus.exe" νμΌ μ€ννκΈ°
* http://localhost:9090 μΌλ‘ λ€μ΄κ°μ λ Prometheus Server μΉνμ΄μ§κ° λ³΄μ΄λ©΄ μ±κ³΅μ μΌλ‘ μ€ν λ κ²μ΄λ€.

![prometheus_monitoring.png](./img/prometheus_monitoring.png)

* λ§μ½ μλμ κ°μ λ§ν¬λ‘ μ μ ν΄μ λͺ¨λν°λ§ λμ μ΄νλ¦¬μΌμ΄μμ΄ μ λλ‘ λ μλμ§ νμΈ 
```java
http://localhost:9090/targets
```
![prometheus_targets.png](./img/prometheus_targets.png)


π Prometheus μ€μ νμΌ λ³΄κΈ°
-

* μμΆμ νΌ κ²½λ‘μ μλ "prometheus.yml" νμΌμ ν΄λΉ λ΄μ©μ μΆκ°ν΄μ€¬μ
```yaml
global:
  scrape_interval: 10s # 10μ΄ λ§λ€ Metricμ Pulling
  evaluation_interval: 10s
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus' # Application prometheus endpoint
    static_configs:
      - targets: ['localhost:8080'] # λ΄ μ±μ΄ λ μλ μ£Όμ
```

* μ€ν ν μ€μ  νμΌ νμΈ
```java
http://localhost:9090/config
```


<br/>

π Grafana μ€μΉνκΈ°
-
> μμ κ°μ λ°©λ²μΌλ‘ μΌμΌμ΄ μΏΌλ¦¬λ₯Ό λλ € μμ€νμ λͺ¨λν°λ§νλ κ²μ λ§€μ° λΉν¨μ¨μ μ΄κ³  νμΈμ μ΄λ ΅κ² νλ μΌμλλ€. λ°λΌμ Grafanaλ₯Ό μ€μΉν΄μ λμλ³΄λλ‘ κ΄λ¦¬νλ©΄ μμ€νμ ν¨μ¨μ μΌλ‘ λͺ¨λν°λ§ ν  μ μμ΅λλ€.
 

<br/>

* [Grafana μ€μΉ ννμ΄μ§](https://grafana.com/grafana/download?platform=windows) μμ νλ‘κ·Έλ¨μ μ€μΉ ν©λλ€.
* "C:\Program Files\GrafanaLabs\grafana\bin" κ²½λ‘μμ "grafana-server.exe" νμΌμ μ€μΉν©λλ€.
* "http://localhost:3000/login" μμ λ‘κ·ΈμΈν©λλ€. μ΄κΈ° κ³μ μ μλμ κ°μ΅λλ€.
    ```java
    ID : admin
    Password : admin
    ```
* μλμ κ°μ νλ©΄μ΄ λ³΄μΈλ€λ©΄ μ±κ³΅ν κ²λλ€.
![grafanaRoot.png](./img/grafanaRoot.png)
> λΉλ°λ²νΈλ₯Ό λ³κ²½ν λ€ μ¬μ©νλ κ²μ κΆμ₯ν©λλ€.




<br/>

π Grafana & Prometheus μ°λνκΈ°
-

* datasourceλ‘ prometheus μΆκ°
![grafanaSystem.png](./img/grafanaSystem.png)

* μ λ³΄ μλ ₯
![grafanaSystem1.png](./img/grafanaSystem1.png)
> μ΄ λ urlμλ http://localhost:9090/ (νλ‘λ©νμ°μ€κ° λ  μλ μ£Όμ & ν¬νΈ)λ₯Ό μλ ₯ν΄μ€μΌνλ€.


<br/>

* λμλ³΄λ μΆκ°
![grafanaSystem2.png](./img/grafanaSystem2.png)


* ν¨λ μΆκ°
![grafanaSystem3.png](./img/grafanaSystem3.png)

* μΏΌλ¦¬ μΆκ° (Metrics μ ν­λͺ© μΆκ°)
![grafanaSystem4.png](./img/grafanaSystem4.png)


* λμλ³΄λ μ€μ μ μλμ κ°μ΄ μΆκ° 
![grafanaSystem5.png](./img/grafanaSystem5.png)
