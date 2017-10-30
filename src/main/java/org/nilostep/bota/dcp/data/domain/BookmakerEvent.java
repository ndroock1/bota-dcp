package org.nilostep.bota.dcp.data.domain;

import lombok.Data;
import org.nilostep.bota.dcp.bookmakers.IQuery;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class BookmakerEvent implements IQuery {

    @Id
    @GeneratedValue
    private Long beId;

    private String eventDescriptionBookmaker;

    private String similarityType;

    private double similarity;

    private String Url;

    private String CssSelector;

    private String eventJsPre;

    @ManyToOne
    @JoinColumn(name = "id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "bookmaker_id")
    private Bookmaker bookmaker;

    private transient List<String> queryResult;
}