package dsw.ms.incidencias.model;

/**
 * DTO liviano con los datos del tecnico que necesita Incidencias.
 * Se obtiene del microservicio de Identidad via REST (GET /api/tecnicos/{id}).
 */
public record TecnicoDTO(
        Integer idUsuario,
        String especialidad,
        Integer maxIncidencias,
        Boolean disponibilidad
) {}
