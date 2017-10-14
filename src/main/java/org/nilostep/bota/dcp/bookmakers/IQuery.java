package org.nilostep.bota.dcp.bookmakers;

import java.util.List;

public interface IQuery {

    String getBcUrl();

    String getCssSelector();

    void setQueryResult(List<String> result);
}
