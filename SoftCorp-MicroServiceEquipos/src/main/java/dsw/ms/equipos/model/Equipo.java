package dsw.ms.equipos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Equipo. Se conserva EXACTAMENTE el mapeo del monolito
 * (tabla "equipos", PK String "codigo_equipo") para que el microservicio
 * opere sobre las mismas filas de la base compartida sin migraciones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @Column(name = "codigo_equipo")
    private String codigoEquipo;

    @Column(name = "marca_modelo")
    private String marcaModelo;

    @Column(name = "area_ubicacion")
    private String areaUbicacion;

    @Column(name = "responsable")
    private String responsable;
}
