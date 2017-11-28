package org.nilostep.bota.dcp.data.repository;

import org.nilostep.bota.dcp.data.domain.ConfigBC;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConfigBCRepository extends CrudRepository<ConfigBC, Long> {

    List<ConfigBC> findConfigBCSBySelectedEquals(int selected);

    List<ConfigBC> findConfigBCSBySelectedEqualsAndHasPayloadEquals(int selected, int hasPayload);
}