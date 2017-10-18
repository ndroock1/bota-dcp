package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class BceMbO {

    @Id
    @GeneratedValue
    private Long boId;

    @ManyToOne
    @JoinColumn(name = "be_id")
    private BookmakerEvent bookmakerEvent;

    private String markettype;

    private String bet;

    private double odd;
}