package com.example.demo.resource;

import com.example.demo.dto.PageDto;
import com.example.demo.dto.UserDto;
import com.example.demo.model.PageRequest;
import com.example.demo.service.UserService;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GET
    public Response getUsers(
            @BeanParam PageRequest pageRequest
    ) {
        PageDto<UserDto> pageDto = userService.findAll(pageRequest);
        return Response.ok(pageDto).build();
    }
}
