CREATE TABLE oddsbrowser.configbc
(
  bc_id              INT(10) UNSIGNED PRIMARY KEY NOT NULL AUTO_INCREMENT,
  selected           INT(11),
  type               INT(10) UNSIGNED,
  css_selector       VARCHAR(255),
  event_css_selector VARCHAR(255),
  event_js_pre       VARCHAR(255),
  regex_event_url    VARCHAR(255),
  url                VARCHAR(255),
  bookmaker_id       VARCHAR(255),
  competition_id     VARCHAR(255),
  CONSTRAINT FK_jb7x8n5ojd0116lfie69h1emd FOREIGN KEY (bookmaker_id) REFERENCES bookmaker (bookmaker_id),
  CONSTRAINT FK_f8m6an1b4eyaf62awxqn02c0g FOREIGN KEY (competition_id) REFERENCES competition (competition_id)
);
CREATE INDEX FK_f8m6an1b4eyaf62awxqn02c0g
  ON oddsbrowser.configbc (competition_id);
CREATE INDEX FK_jb7x8n5ojd0116lfie69h1emd
  ON oddsbrowser.configbc (bookmaker_id);
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '[data-gtm-cd-event]', '.table-container', NULL, '(?<=t">)(.*?)(?=<)|(?<=a\\sng-href=")(.*?)(?=")',
        'https://sportsbook.betsson.com/en/football/england/fa-premier-league', 'bkm001', '10932509');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '.featured-match', '.ev-layout', 'ob.pref.change_odds(''DECIMAL'')',
        '(?<=html" title=")(.*?)(?=")|(?<=sports\\.coral\\.co\\.uk)(.*?)(?="\\st)',
        'http://sports.coral.co.uk/football/england/premier-league', 'bkm002', '10932509');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 1, '.bets.ml', '.bet_type', 'BranchWindow.showLeague(1,40253)',
        '(?<=tooltip=").*?(?=")|(?<=javascript:).*?(?="\\sc)', 'https://www.10bet.co.uk/sports/football/', 'bkm003',
        '10932509');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '[data-gtm-cd-event]', '.table-container', NULL, '(?<=t">)(.*?)(?=<)|(?<=a\\sng-href=")(.*?)(?=")',
        'https://sportsbook.betsson.com/en/football/netherlands/dutch-eredivisie', 'bkm001', '9404054');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '.featured-match', '.ev-layout', 'ob.pref.change_odds(''DECIMAL'')',
        '(?<=html" title=")(.*?)(?=")|(?<=sports\\.coral\\.co\\.uk)(.*?)(?="\\st)',
        'http://sports.coral.co.uk/football/netherlands/eredivisie', 'bkm002', '9404054');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 1, '.bets.ml', '.bet_type', 'BranchWindow.showLeague(1,41372)',
        '(?<=tooltip=").*?(?=")|(?<=javascript:).*?(?="\\sc)', 'https://www.10bet.co.uk/sports/football/', 'bkm003',
        '9404054');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '[data-gtm-cd-event]', '.table-container', NULL, '(?<=t">)(.*?)(?=<)|(?<=a\\sng-href=")(.*?)(?=")',
        'https://sportsbook.betsson.com/en/football/germany/german-bundesliga', 'bkm001', '59');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '.featured-match', '.ev-layout', 'ob.pref.change_odds(''DECIMAL'')',
        '(?<=html" title=")(.*?)(?=")|(?<=sports\\.coral\\.co\\.uk)(.*?)(?="\\st)',
        'http://sports.coral.co.uk/football/germany/bundesliga', 'bkm002', '59');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 1, '.bets.ml', '.bet_type', 'BranchWindow.showLeague(1,40481)',
        '(?<=tooltip=").*?(?=")|(?<=javascript:).*?(?="\\sc)', 'https://www.10bet.co.uk/sports/football/', 'bkm003',
        '59');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '[data-gtm-cd-event]', '.table-container', NULL, '(?<=t">)(.*?)(?=<)|(?<=a\\sng-href=")(.*?)(?=")',
        'https://sportsbook.betsson.com/en/football/spain/primera-division', 'bkm001', '117');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '.featured-match', '.ev-layout', 'ob.pref.change_odds(''DECIMAL'')',
        '(?<=html" title=")(.*?)(?=")|(?<=sports\\.coral\\.co\\.uk)(.*?)(?="\\st)',
        'http://sports.coral.co.uk/football/spain/la-liga', 'bkm002', '117');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 1, '.bets.ml', '.bet_type', 'BranchWindow.showLeague(1,40031)',
        '(?<=tooltip=").*?(?=")|(?<=javascript:).*?(?="\\sc)', 'https://www.10bet.co.uk/sports/football/', 'bkm003',
        '117');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 2, '.rowOdd', '.marketHolderExpanded', 'document.site.set_pref(''price_display'',''DECIMAL'')',
        '(?<=space">)(.*?)(?=<)|(?<=sports.williamhill\\.com)(.*?)(?=")',
        'http://sports.williamhill.com/bet/en-gb/betting/t/295/English+Premier+League.html', 'bkm004', '10932509');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 2, '.rowOdd', '.marketHolderExpanded', 'document.site.set_pref(''price_display'',''DECIMAL'')',
        '(?<=space">)(.*?)(?=<)|(?<=sports.williamhill\\.com)(.*?)(?=")',
        'http://sports.williamhill.com/bet/en-gb/betting/t/306/Dutch+Eredivisie.html', 'bkm004', '9404054');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 2, '.rowOdd', '.marketHolderExpanded', 'document.site.set_pref(''price_display'',''DECIMAL'')',
        '(?<=space">)(.*?)(?=<)|(?<=sports.williamhill\\.com)(.*?)(?=")',
        'http://sports.williamhill.com/bet/en-gb/betting/t/315/German+Bundesliga.html', 'bkm004', '59');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 2, '.rowOdd', '.marketHolderExpanded', 'document.site.set_pref(''price_display'',''DECIMAL'')',
        '(?<=space">)(.*?)(?=<)|(?<=sports.williamhill\\.com)(.*?)(?=")',
        'http://sports.williamhill.com/bet/en-gb/betting/t/338/Spanish+La+Liga+Primera.html', 'bkm004', '117');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '[data-gtm-cd-event]', '.table-container', NULL, '(?<=t">)(.*?)(?=<)|(?<=a\\sng-href=")(.*?)(?=")',
        'https://sportsbook.betsson.com/en/football/italy/italian-serie-a', 'bkm001', '81');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 0, '.featured-match', '.ev-layout', 'ob.pref.change_odds(''DECIMAL'')',
        '(?<=html" title=")(.*?)(?=")|(?<=sports\\.coral\\.co\\.uk)(.*?)(?="\\st)',
        'http://sports.coral.co.uk/football/italy/serie-a', 'bkm002', '81');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 1, '.bets.ml', '.bet_type', 'BranchWindow.showLeague(1,40030)',
        '(?<=tooltip=").*?(?=")|(?<=javascript:).*?(?="\\sc)', 'https://www.10bet.co.uk/sports/football/', 'bkm003',
        '81');
INSERT INTO oddsbrowser.configbc (selected, type, css_selector, event_css_selector, event_js_pre, regex_event_url, url, bookmaker_id, competition_id)
VALUES (1, 2, '.rowOdd', '.marketHolderExpanded', 'document.site.set_pref(''price_display'',''DECIMAL'')',
        '(?<=space">)(.*?)(?=<)|(?<=sports.williamhill\\.com)(.*?)(?=")',
        'http://sports.williamhill.com/bet/en-gb/betting/t/321/Italian+Serie+A.html', 'bkm004', '81');