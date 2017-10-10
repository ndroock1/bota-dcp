package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
public class Competition {

    @Id
    private String competitionId;

    private String competitionName;

    private String competitionRegion;

    @ManyToOne
    @JoinColumn(name="eventtype")
    private Eventtype eventtype;
}