package dsw.ms.equipos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @Column(name = "codigo_equipo")
    @NotBlank(message = "El codigo de equipo es obligatorio")
    private String codigoEquipo;

    @Column(name = "marca_modelo")
    private String marcaModelo;

    @Column(name = "area_ubicacion")
    private String areaUbicacion;

    @Column(name = "responsable")
    private String responsable;
}
