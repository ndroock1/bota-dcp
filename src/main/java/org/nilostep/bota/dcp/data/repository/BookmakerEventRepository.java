package org.nilostep.bota.dcp.data.repository;

import org.nilostep.bota.dcp.data.domain.BookmakerEvent;
import org.nilostep.bota.dcp.data.domain.ConfigBC;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookmakerEventRepository extends CrudRepository<BookmakerEvent, Long> {

    List<BookmakerEvent> findBookmakerEventsByHasPayloadEquals(int hasPayload);

    List<BookmakerEvent> findBookmakerEventsByConfigBC(ConfigBC configBC);
}