package org.nilostep.bota.dcp.data.domain;

import lombok.Data;
import org.nilostep.bota.dcp.bookmakers.IQuery;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class ConfigBC implements IQuery {

    @Id
    @GeneratedValue
    private Long bcId;

    private int selected;

    private int type;

    private String url;

    private String groupUrl;

    private String UrlCssSelector;

    private String cssSelector;

    private String regexEventUrl;

    private String eventJsPre;

    private String eventCssSelector;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id")
    private Bookmaker bookmaker;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    private int hasPayload;

    private transient List<String> queryResult;
}