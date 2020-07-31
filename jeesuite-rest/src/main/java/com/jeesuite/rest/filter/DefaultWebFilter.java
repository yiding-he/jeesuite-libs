package com.jeesuite.rest.filter;

import com.jeesuite.rest.filter.handler.CorsHandler;
import com.jeesuite.rest.filter.handler.ReqResLogHandler;
import com.jeesuite.rest.filter.handler.ResponseWrapperHandler;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Priority(5)
public class DefaultWebFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Context
    HttpServletRequest request;

    @Context
    ResourceInfo resourceInfo;

    List<FilterHandler> filterHandlers = new ArrayList<>();

    int handlerCount;

    public DefaultWebFilter() {
        if (FilterConfig.corsEnabled()) {
            registerHandler(new CorsHandler());
        }
        if (FilterConfig.reqRspLogEnabled()) {
            registerHandler(new ReqResLogHandler());
        }
        registerHandler(new ResponseWrapperHandler());
    }

    private void registerHandler(FilterHandler hander) {
        filterHandlers.add(hander);
        handlerCount = filterHandlers.size();

        Collections.sort(filterHandlers, new Comparator<FilterHandler>() {
            @Override
            public int compare(FilterHandler o1, FilterHandler o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        for (int i = 0; i < handlerCount; i++) {
            filterHandlers.get(i).processRequest(requestContext, request, resourceInfo);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        for (int i = handlerCount - 1; i >= 0; i--) {
            filterHandlers.get(i).processResponse(requestContext, responseContext, resourceInfo);
        }

    }

}
