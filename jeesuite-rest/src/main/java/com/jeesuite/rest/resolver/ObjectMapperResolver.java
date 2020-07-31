package com.jeesuite.rest.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeesuite.common.json.JsonMapper;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Jackson ObjectMapper 解析器
 * <p>
 * 用于设置ObjectMapper
 *
 * @author LinHaobin
 */
@Provider
public class ObjectMapperResolver implements ContextResolver<ObjectMapper> {

    static JsonMapper jsonMapper = JsonMapper.nonNullMapper().dateAndTimestampConvert(true);

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return jsonMapper.getMapper();
    }
}