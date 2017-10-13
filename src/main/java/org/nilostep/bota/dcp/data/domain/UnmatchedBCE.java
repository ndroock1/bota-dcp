package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class UnmatchedBCE {

    @Id
    @GeneratedValue
    private Long bceId;

    private String eventDescription;

    private String marketUrl;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id")
    private Bookmaker bookmaker;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;
}