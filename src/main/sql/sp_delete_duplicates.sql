USE `oddsbrowser`;
DROP PROCEDURE IF EXISTS `sp_delete_duplicates`;

DELIMITER $$
USE `oddsbrowser`$$
CREATE PROCEDURE `oddsbrowser`.`sp_delete_duplicates`()
  BEGIN

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
      DELETE FROM tbl_duplicate_be_id;
    END;

    INSERT INTO tbl_duplicate_be_id (be_id)
      SELECT duplicates.be_id
      FROM
        duplicates
        LEFT JOIN
        (SELECT
           be_id,
           MAX(sim)
         FROM
           duplicates
         GROUP BY
           bookmaker_id, id) X
          ON duplicates.be_id = X.be_id
      WHERE
        X.be_id IS NOT NULL;

    DELETE FROM bookmaker_event
    WHERE
      be_id IN (
        SELECT tbl_duplicate_be_id.be_id
        FROM
          tbl_duplicate_be_id);

    DELETE FROM tbl_duplicate_be_id;

  END$$
DELIMITER ;