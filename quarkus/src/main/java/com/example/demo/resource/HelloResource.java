package com.example.demo.resource;

import com.example.demo.dto.GreetingDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {
    @GET
    @Path("/{name}")
    public Response hello(@PathParam("name") String name) {
        GreetingDto dto = new GreetingDto("Hello, %s!".formatted(name));
        return Response.ok(dto).build();
    }
}
