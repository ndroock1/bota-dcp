package org.nilostep.bota.dcp.data.repository;

import org.nilostep.bota.dcp.data.domain.Bookmaker;
import org.nilostep.bota.dcp.data.domain.BookmakerEvent;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BookmakerEventRepository extends CrudRepository<BookmakerEvent, Long> {

    List<BookmakerEvent> findBookmakerEventsByBookmakerIs(Bookmaker bookmaker);

    List<BookmakerEvent> findBookmakerEventsByHasPayloadEquals(int hasPayload);

    List<BookmakerEvent> findBookmakerEventsByBookmakerIsAndHasPayloadEquals(Bookmaker bookmaker, int hasPayload);
}