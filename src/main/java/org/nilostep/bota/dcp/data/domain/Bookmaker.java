package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Bookmaker {

    @Id
    private String bookmakerId;

    private String bookmakerName;

    private String bookmakerRootUrl;
}