CREATE TABLE oddsbrowser.configbm
(
  bm_id                    BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  selected                 INT(11),
  address                  INT(11)                NOT NULL,
  bet_count                INT(11)                NOT NULL,
  regex_odds               VARCHAR(255),
  bookmaker_id             VARCHAR(255),
  markettype_id_eventtype  VARCHAR(255),
  markettype_id_markettype VARCHAR(255),
  CONSTRAINT FK_blu5by9yjwch20bvpfwgg4d9d FOREIGN KEY (bookmaker_id) REFERENCES bookmaker (bookmaker_id),
  CONSTRAINT FK_mc15q63gigac3ckjxkidiw3gb FOREIGN KEY (markettype_id_eventtype, markettype_id_markettype) REFERENCES markettype (eventtype, markettype)
);
CREATE INDEX FK_blu5by9yjwch20bvpfwgg4d9d
  ON oddsbrowser.configbm (bookmaker_id);
CREATE INDEX FK_mc15q63gigac3ckjxkidiw3gb
  ON oddsbrowser.configbm (markettype_id_eventtype, markettype_id_markettype);
INSERT INTO oddsbrowser.configbm (selected, address, bet_count, regex_odds, bookmaker_id, markettype_id_eventtype, markettype_id_markettype)
VALUES (1, 0, 3, '(\\d+\\.\\d{2})', 'bkm001', '1', 'MATCH_ODDS');
INSERT INTO oddsbrowser.configbm (selected, address, bet_count, regex_odds, bookmaker_id, markettype_id_eventtype, markettype_id_markettype)
VALUES (1, 0, 3, '(\\d+\\.\\d{2})', 'bkm002', '1', 'MATCH_ODDS');
INSERT INTO oddsbrowser.configbm (selected, address, bet_count, regex_odds, bookmaker_id, markettype_id_eventtype, markettype_id_markettype)
VALUES (1, 0, 3, '(\\d+\\.\\d{2})', 'bkm003', '1', 'MATCH_ODDS');
INSERT INTO oddsbrowser.configbm (selected, address, bet_count, regex_odds, bookmaker_id, markettype_id_eventtype, markettype_id_markettype)
VALUES (1, 0, 3, '(\\d+\\.\\d{2})', 'bkm004', '1', 'MATCH_ODDS');