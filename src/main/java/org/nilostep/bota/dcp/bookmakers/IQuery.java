package org.nilostep.bota.dcp.bookmakers;

import java.util.List;

public interface IQuery {

    String getUrl();

    String getUrlCssSelector();

    String getGroupUrl();

    String getCssSelector();

    String getEventJsPre();

    int getHasPayload();

    void setQueryResult(List<String> result);
}