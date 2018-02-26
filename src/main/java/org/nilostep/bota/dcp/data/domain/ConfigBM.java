package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class ConfigBM {

    @Id
    @GeneratedValue
    private Long bmId;

    private int selected;

    private int address;

    private String regexMarket;

    private String regexOdds;

    private int betCount;

    private String betCountS;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id")
    private Bookmaker bookmaker;

    @ManyToOne
    private Markettype markettypeId;
}