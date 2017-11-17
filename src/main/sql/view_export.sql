CREATE OR REPLACE VIEW export AS
  SELECT
    bce_mbo.bo_id,
    bce_mbo.bet                    AS "Bet",
    bce_mbo.odd                    AS "Odd",
    bookmaker.bookmaker_name       AS "bookmaker",
    competition.competition_name   AS "compName",
    competition.competition_region AS "competitionRegion",
    event.name                     AS "eventName",
    eventtype.name                 AS "eventTypeName",
    bce_mbo.markettype             AS "marketType",
    event.open_date                AS "openDate",
    event.timezone
  FROM
    bce_mbo,
    bookmaker_event,
    bookmaker,
    event,
    competition,
    eventtype
  WHERE
    bce_mbo.be_id = bookmaker_event.be_id AND
    bookmaker_event.id = event.id AND
    bookmaker_event.bookmaker_id = bookmaker.bookmaker_id AND
    event.competition_id = competition.competition_id AND
    competition.eventtype = eventtype.eventtype