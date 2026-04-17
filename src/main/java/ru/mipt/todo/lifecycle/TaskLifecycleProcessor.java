package ru.mipt.todo.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.mipt.todo.service.TaskService;

/**
 * BeanPostProcessor для логирования жизненного цикла бинов TaskService.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService) {
            log.info("[Lifecycle] Bean '{}' created, before initialization", beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof TaskService) {
            log.info("[Lifecycle] Bean '{}' fully initialized", beanName);
        }
        return bean;
    }
}
