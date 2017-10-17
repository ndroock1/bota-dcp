package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
public class Markettype {

    @Id
    private String markettype;

    @ManyToOne
    @JoinColumn(name = "eventtype")
    private Eventtype eventtype;
}