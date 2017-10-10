package org.nilostep.bota.dcp.data.repository;

import org.nilostep.bota.dcp.data.domain.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, String> {
}