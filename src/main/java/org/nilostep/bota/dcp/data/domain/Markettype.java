package org.nilostep.bota.dcp.data.domain;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Entity
public class Markettype {

    @EmbeddedId
    private MarkettypeId markettypeId;
}