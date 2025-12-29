package com.vitalsync.user;

import com.vitalsync.user.dto.UserRegistrationDTO;
import com.vitalsync.user.dto.UserResponseDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    @Path("/register")
    @Operation(summary = "Criar novo usuário", description = "Cadastra um novo usuário no sistema.")
    public Response register(@Valid UserRegistrationDTO dto) {
        UserResponseDTO createdUser = userService.register(dto);
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    }

}