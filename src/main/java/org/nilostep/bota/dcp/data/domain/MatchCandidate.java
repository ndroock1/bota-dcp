package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class MatchCandidate {

    @Id
    @GeneratedValue
    private Long mcId;

    private String eventDescriptionBookmaker;

    private String eventDescriptionBetfair;

    private String similarityType;

    private double similarity;

    @ManyToOne
    @JoinColumn(name = "bce_id")
    private UnmatchedBCE unmatchedBCE;

    @ManyToOne
    @JoinColumn(name = "id")
    private Event event;
}