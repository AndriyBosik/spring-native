package com.example.demo.resource;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;

    @GET
    public Response getUsers(
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<UserDto> pageDto = userService.findAll(pageable);
        return Response.ok(pageDto).build();
    }
}
