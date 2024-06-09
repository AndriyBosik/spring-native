package com.example.demo.exception.mapper;

import com.example.demo.dto.ErrorInfoDto;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException exception) {
        ErrorInfoDto errorInfoDto = new ErrorInfoDto(exception.getLocalizedMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorInfoDto)
                .build();
    }
}
