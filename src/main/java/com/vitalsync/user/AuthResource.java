package com.vitalsync.user;

import com.vitalsync.user.dto.UserLoginDTO;
import com.vitalsync.user.dto.UserResponseTokenDTO;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Operações de Login")
public class AuthResource {

    @Inject
    UserService userService;

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Login", description = "Autentica e retorna o Token JWT")
    public Response login(@Valid UserLoginDTO dto) {
        UserResponseTokenDTO token = userService.login(dto);
        return Response.ok(token).build();
    }
}