DELIMITER //

CREATE FUNCTION aceCount(playerName VARCHAR(40), startDate DATE, endDate DATE)
RETURNS FLOAT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE acePlayerId INT;
    DECLARE avgAces FLOAT;

    SELECT player_id INTO acePlayerId
    FROM Players
    WHERE name = playerName
    LIMIT 1;

    IF acePlayerId IS NULL THEN
        RETURN 0;
    END IF;

    SELECT AVG(pim.ace) INTO avgAces
    FROM Player_in_match pim
    JOIN Matches m ON pim.match_id = m.match_id
    JOIN Tournaments t ON m.tourney_id = t.tourney_id
    WHERE pim.player_id = acePlayerId
      AND t.tourney_date BETWEEN startDate AND endDate;

    IF avgAces IS NULL THEN
        RETURN 0;
    END IF;

    RETURN avgAces;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE showAggregateStatistics(
    IN player_name VARCHAR(40),
    IN start_date DATE,
    IN end_date DATE
)
BEGIN
    SELECT
        p.name,
        COUNT(pim.match_id) AS total_matches,
        SUM(pim.ace) AS total_aces,
        SUM(pim.df) AS total_double_faults,
        SUM(pim.svpt) AS total_service_points,
        SUM(pim.first_in) AS total_first_in,
        SUM(pim.first_won) AS total_first_won,
        SUM(pim.second_won) AS total_second_won,
        SUM(pim.bp_saved) AS total_bp_saved,
        SUM(pim.bp_faced) AS total_bp_faced
    FROM Player_in_match pim
    JOIN Players p ON pim.player_id = p.player_id
    JOIN Matches m ON pim.match_id = m.match_id AND pim.tourney_id = m.tourney_id
    JOIN Tournaments t ON m.tourney_id = t.tourney_id
    WHERE p.name = player_name
      AND t.tourney_date BETWEEN start_date AND end_date
    GROUP BY p.name;
END //

DELIMITER ;

CREATE VIEW TopAces AS
SELECT
    p.name,
    SUM(pim.ace) AS total_aces
FROM Player_in_match pim
JOIN Players p ON pim.player_id = p.player_id
GROUP BY p.player_id, p.name
ORDER BY total_aces DESC
LIMIT 10;

DELIMITER //

CREATE TRIGGER onInsertionPlayer
BEFORE INSERT ON Players
FOR EACH ROW
BEGIN
    IF NEW.IOC IN ('RUS', 'EST') THEN
        SET NEW.IOC = 'USR';
    END IF;
END //

DELIMITER ;