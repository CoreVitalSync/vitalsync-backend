package com.vitalsync.sharing;

import com.vitalsync.sharing.dto.AcceptLinkDTO;
import com.vitalsync.sharing.dto.SharingResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/sharing")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Data Sharing", description = "Vínculo entre Médicos e Pacientes")
public class SharingResource {

    private final SharingService service;

    @Inject
    public SharingResource(SharingService service) {
        this.service = service;
    }

    @POST
    @Path("/invite")
    @RolesAllowed("PATIENT") // Só paciente gera
    @Operation(summary = "Gerar Convite", description = "Cria um token para o médico acessar os dados")
    public Response createInvite() {
        SharingResponseDTO invite = service.createInvite();
        return Response.status(Response.Status.CREATED).entity(invite).build();
    }

    @POST
    @Path("/accept")
    @RolesAllowed("DOCTOR") // Só médico aceita
    @Operation(summary = "Aceitar Convite", description = "Médico insere o token para vincular-se ao paciente")
    public Response acceptInvite(@Valid AcceptLinkDTO dto) {
        SharingResponseDTO result = service.acceptInvite(dto);
        return Response.ok(result).build();
    }
}