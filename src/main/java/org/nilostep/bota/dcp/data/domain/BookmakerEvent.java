package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BookmakerEvent {

    @Id
    @GeneratedValue
    private Long beId;

    private String eventDescriptionBookmaker;

    private String similarityType;

    private double similarity;

    private String marketUrl;

    @ManyToOne
    @JoinColumn(name = "id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id")
    private Bookmaker bookmaker;
}