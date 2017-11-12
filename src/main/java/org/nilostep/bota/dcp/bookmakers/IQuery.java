package org.nilostep.bota.dcp.bookmakers;

import java.util.List;

public interface IQuery {

    String getUrl();

    String getCssSelector();

    String getEventJsPre();

    void setQueryResult(List<String> result);
}