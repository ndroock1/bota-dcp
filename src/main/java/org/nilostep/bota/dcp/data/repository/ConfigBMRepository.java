package org.nilostep.bota.dcp.data.repository;

import org.nilostep.bota.dcp.data.domain.Bookmaker;
import org.nilostep.bota.dcp.data.domain.ConfigBM;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConfigBMRepository extends CrudRepository<ConfigBM, Long> {
    List<ConfigBM> findByBookmaker(Bookmaker bookmaker);
}