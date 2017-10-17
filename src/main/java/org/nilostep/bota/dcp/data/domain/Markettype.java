package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Markettype {

    @Id
    @GeneratedValue
    private Long mtId;

    @ManyToOne
    @JoinColumn(name = "eventtype")
    private Eventtype eventtype;

    private String markettype;
}