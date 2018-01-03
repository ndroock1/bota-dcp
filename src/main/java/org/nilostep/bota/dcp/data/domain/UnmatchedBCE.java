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

    private String UrlCssSelector;

    private String groupUrl;

    private String marketCssSelector;

    private String eventJsPre;

    // ToDo: Can this be removed ? YES!
    @ManyToOne
    @JoinColumn(name = "bookmaker_id")
    private Bookmaker bookmaker;

    // ToDo: Can this be removed ? YES!
    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @ManyToOne
    @JoinColumn(name = "bc_id")
    private ConfigBC configBC;

}