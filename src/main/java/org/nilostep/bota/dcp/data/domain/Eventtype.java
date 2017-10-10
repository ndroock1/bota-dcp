package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Eventtype {

    @Id
    private String eventtype;

    private String name;
}