package org.nilostep.bota.dcp.bookmakers;

import java.util.List;

public interface IQuery {

    String getUrl();

    String getCssSelector();

    void setQueryResult(List<String> result);
}
