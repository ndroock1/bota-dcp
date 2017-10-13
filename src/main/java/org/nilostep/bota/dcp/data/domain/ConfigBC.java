package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class ConfigBC {

    @Id
    @GeneratedValue
    private Long bcId;

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

    private transient List<String> queryResult;
}