CREATE OR REPLACE VIEW duplicates AS
  SELECT
    bce.be_id,
    bce.bookmaker_id,
    bce.id,
    bce.event_description_bookmaker,
    ce.name,
    bce.similarity AS sim
  FROM
    bookmaker_event bce,
    event ce
  WHERE
    bce.id = ce.id AND
    ce.id IN (
      SELECT t2.id
      FROM
        bookmaker_event t1,
        event t2
      WHERE
        t1.id = t2.id
      GROUP BY
        t1.bookmaker_id,
        t1.id
      HAVING
        COUNT(t1.id) > 1)