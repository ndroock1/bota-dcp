package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
@Entity
public class Event {

    @Id
    private String id;

    private String name;

    private String countryCode;

    private String timezone;

    private String venue;

    private Date openDate;

    @ManyToOne
    @JoinColumn(name="competition_id")
    private Competition competition;
}
