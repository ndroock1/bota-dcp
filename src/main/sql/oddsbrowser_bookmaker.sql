CREATE TABLE oddsbrowser.bookmaker
(
  bookmaker_id       VARCHAR(255) PRIMARY KEY NOT NULL,
  bookmaker_name     VARCHAR(255),
  bookmaker_root_url VARCHAR(255)
);
INSERT INTO oddsbrowser.bookmaker (bookmaker_id, bookmaker_name, bookmaker_root_url)
VALUES ('bkm001', 'Betsson', 'https://sportsbook.betsson.com');
INSERT INTO oddsbrowser.bookmaker (bookmaker_id, bookmaker_name, bookmaker_root_url)
VALUES ('bkm002', 'Coral', 'http://sports.coral.co.uk');
INSERT INTO oddsbrowser.bookmaker (bookmaker_id, bookmaker_name, bookmaker_root_url)
VALUES ('bkm003', '10Bet', 'https://www.10bet.co.uk/sports/football/');
INSERT INTO oddsbrowser.bookmaker (bookmaker_id, bookmaker_name, bookmaker_root_url)
VALUES ('bkm004', 'William Hill', 'http://sports.williamhill.com');