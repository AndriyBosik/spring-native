package com.example.demo.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/exception")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class ExceptionResource {
    @GET
    @Path("/illegal-argument")
    public Response illegalArgument() {
        throw new IllegalArgumentException("Invalid request");
    }
}
