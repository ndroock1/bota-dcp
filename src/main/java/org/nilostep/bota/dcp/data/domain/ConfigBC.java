package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
@Entity
public class ConfigBC {

    @Id
    private String bcId;

    private String bcUrl;

    private String cssSelector;

    private String rgxEventDescription;

    private String rgxMarketUrl;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id")
    private Bookmaker bookmaker;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;
}