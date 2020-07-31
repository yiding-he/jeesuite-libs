package com.jeesuite.rest.excetion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jeesuite.rest.response.ResponseCode;
import com.jeesuite.rest.response.WrapperResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * REST 异常映射
 * <p>
 * 将异常转换成RestResponse
 *
 * @author LinHaobin
 */
public class BaseExceptionMapper implements ExceptionMapper<Exception> {

    private static Logger log = LoggerFactory.getLogger(BaseExceptionMapper.class);

    @Context
    private HttpServletRequest request;

    private ExcetionWrapper excetionWrapper;

    public BaseExceptionMapper() {
    }

    public BaseExceptionMapper(ExcetionWrapper excetionWrapper) {
        super();
        this.excetionWrapper = excetionWrapper;
    }

    @Override
    public Response toResponse(Exception e) {

        WrapperResponseEntity response = null;
        if (e instanceof NotFoundException) {
            response = new WrapperResponseEntity(ResponseCode.NOT_FOUND);
        } else if (e instanceof NotAllowedException) {
            response = new WrapperResponseEntity(ResponseCode.FORBIDDEN);
        } else if (e instanceof JsonProcessingException) {
            response = new WrapperResponseEntity(ResponseCode.ERROR_JSON);
        } else if (e instanceof NotSupportedException) {
            response = new WrapperResponseEntity(ResponseCode.UNSUPPORTED_MEDIA_TYPE);
        } else {
            response = excetionWrapper != null ? excetionWrapper.toResponse(e) : null;
            if (response == null) response = new WrapperResponseEntity(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        return Response.status(response.httpStatus()).type(MediaType.APPLICATION_JSON).entity(response).build();
    }

}
