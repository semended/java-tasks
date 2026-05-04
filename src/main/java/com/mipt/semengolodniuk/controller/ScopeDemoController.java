package com.mipt.semengolodniuk.controller;

import com.mipt.semengolodniuk.config.PrototypeScopedBean;
import com.mipt.semengolodniuk.config.RequestScopedBean;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for scope bean demonstration.
 */
@RestController
@RequestMapping("/api/scope")
public class ScopeDemoController {

    private final RequestScopedBean requestScopedBean;
    private final ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider;

    public ScopeDemoController(RequestScopedBean requestScopedBean,
                               ObjectProvider<PrototypeScopedBean> prototypeScopedBeanProvider) {
        this.requestScopedBean = requestScopedBean;
        this.prototypeScopedBeanProvider = prototypeScopedBeanProvider;
    }

    @GetMapping("/request")
    public Map<String, Object> getRequestScopeInfo() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("requestId", requestScopedBean.getRequestId());
        response.put("startedAt", requestScopedBean.getStartedAt());
        return response;
    }

    @GetMapping("/prototype")
    public Map<String, Object> getPrototypeScopeInfo() {
        PrototypeScopedBean prototypeScopedBean = prototypeScopedBeanProvider.getObject();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("generatedTaskId", prototypeScopedBean.generateTaskId());
        return response;
    }
}
