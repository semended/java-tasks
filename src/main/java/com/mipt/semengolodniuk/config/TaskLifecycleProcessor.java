package com.mipt.semengolodniuk.config;

import com.mipt.semengolodniuk.repository.TaskRepository;
import com.mipt.semengolodniuk.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Bean post processor that logs lifecycle events for task services and repositories.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            LOGGER.info("Before initialization: beanName={}, beanType={}", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService || bean instanceof TaskRepository) {
            LOGGER.info("After initialization: beanName={}, beanType={}", beanName, bean.getClass().getSimpleName());
        }
        return bean;
    }
}
