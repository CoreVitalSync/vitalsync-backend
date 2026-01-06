package com.vitalsync.vitals;

import com.vitalsync.vitals.dto.VitalSignCreateDTO;
import com.vitalsync.vitals.dto.VitalSignResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Path("/vitals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Vital Signs", description = "Monitoramento de saúde")
@RolesAllowed({"PATIENT"}) // Apenas pacientes registram seus sinais
public class VitalSignResource {

    private final VitalSignService service;

    public  VitalSignResource(VitalSignService service) {
        this.service = service;
    }

    @POST
    @Operation(summary = "Registrar Sinal Vital", description = "Salva uma nova medição (Pressão, Glicemia, etc)")
    public Response create(@Valid VitalSignCreateDTO dto) {
        VitalSignResponseDTO created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    @Operation(summary = "Histórico de Sinais", description = "Lista todo o histórico do paciente logado")
    public Response listHistory() {
        List<VitalSignResponseDTO> history = service.listMyHistory();
        return Response.ok(history).build();
    }

    @GET
    @Path("/patient/{patientId}")
    @RolesAllowed("DOCTOR")
    @Operation(summary = "Ver Sinais Vitais do Paciente", description = "Acesso médico ao histórico de sinais vitais")
    public Response getPatientHistory(@PathParam("patientId") UUID patientId) {
        List<VitalSignResponseDTO> list = service.listHistoryByPatient(patientId);
        return Response.ok(list).build();
    }
}