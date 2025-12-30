package com.vitalsync.medication;

import com.vitalsync.medication.dto.MedicationRequestDTO;
import com.vitalsync.medication.dto.MedicationResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/medications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Medication Management", description = "Gestão de remédios e horários")
@RolesAllowed({"PATIENT"})
public class MedicationResource {

    private final MedicationService service;

    public MedicationResource(MedicationService service) {
        this.service = service;
    }

    @POST
    @Operation(summary = "Cadastrar Medicamento", description = "Cria um novo medicamento com seus horários")
    public Response create(@Valid MedicationRequestDTO dto) {
        MedicationResponseDTO created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "Listar Meus Medicamentos", description = "Retorna todos os medicamentos ativos do paciente logado")
    public Response listMyMedications() {
        List<MedicationResponseDTO> list = service.listMyMedications();
        return Response.ok(list).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Excluir Medicamento", description = "Remove um medicamento (apenas se pertencer ao usuário)")
    public Response delete(@PathParam("id") UUID id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Atualizar Medicamento", description = "Atualiza dados e horários de um medicamento existente")
    public Response update(@PathParam("id") UUID id, @Valid MedicationRequestDTO dto) {
        MedicationResponseDTO updated = service.update(id, dto);
        return Response.ok(updated).build();
    }
}