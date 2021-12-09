package com.slack.slack.common.event;

import com.slack.slack.common.event.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.LinkedList;
import java.util.Queue;

@Aspect
@Component
@RequiredArgsConstructor
public class EventPublisherAspect implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher publisher;
    private ThreadLocal<Boolean> appliedLocal = new ThreadLocal<>();
    private final EntityManager entityManager;
    private LinkedList<DomainEvent> queue = new LinkedList<>();

    /* 메소드에 트랜젝션 어노테이션 붙은 경우에만 동작하도록 되어 있다. */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object handleEvent(ProceedingJoinPoint joinPoint) throws Throwable {
        Boolean appliedValue = appliedLocal.get();
        boolean nested = false;

        if (appliedValue != null && appliedValue) {
            nested = true;
        } else {
            nested = false;
            appliedLocal.set(Boolean.TRUE);
        }

        if (!nested) {
            Events.setPublisher(publisher);
            Events.setEntityManager(entityManager);
            Events.setQueue(queue);
        }

        try {
            return joinPoint.proceed();
        } finally {
            if (!nested) {
                Events.reset();
                appliedLocal.remove();
            }
        }
    }

    @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.save(..))")
    public Object handleDelayEvent(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            return joinPoint.proceed();
        } finally {
            // save 메소드 호출할 때 남은 이벤트 가져와서 다시 raise
            Queue<DomainEvent> delayEvent = Events.getDelayEvent();

            if (delayEvent != null)
                while (!delayEvent.isEmpty())
                    Events.raise(delayEvent.poll());
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.publisher = eventPublisher;
    }
}
