DROP TABLE IF EXISTS Player_in_match;
DROP TABLE IF EXISTS Matches;
DROP TABLE IF EXISTS Players;
DROP TABLE IF EXISTS Tournaments;

CREATE TABLE Players
(
    player_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(40),
    dominant_hand CHAR(1), CHECK (dominant_hand IN  ('L', 'R', 'U')),
    height NUMERIC(3,0),
    IOC CHAR(3),
    date_of_birth DATE
);

CREATE TABLE Tournaments
(
    tourney_id VARCHAR(50) PRIMARY KEY,
    tourney_name VARCHAR(50),
    surface VARCHAR(6),
    draw_size NUMERIC(3,0),
    tourney_level CHAR(1),
    tourney_date DATE,
    best_of NUMERIC(1,0)
);

CREATE TABLE Matches
(
    match_id INTEGER,
    tourney_id VARCHAR(50),
    score VARCHAR(50),
    round_of_tourn VARCHAR(4),
    minutes INT,
    winner_id INT,
    loser_id INT,
    PRIMARY KEY (tourney_id, match_id),
    FOREIGN KEY (tourney_id) REFERENCES Tournaments(tourney_id) ON DELETE CASCADE
);

CREATE TABLE Player_in_match
(
    pim_id INT AUTO_INCREMENT PRIMARY KEY, #need to make a new primary key since player_id needs to be nullable
    player_id INT NULL,
    match_id INT NOT NULL,
    tourney_id VARCHAR(50) NOT NULL,
    ace INT,
    df INT,
    svpt INT,
    first_in INT,
    first_won INT,
    second_won INT,
    sv_Gms INT,
    bp_Saved INT,
    bp_Faced INT,
    player_rank INT CHECK (player_rank > 0),
    FOREIGN KEY (player_id) REFERENCES Players(player_id) ON DELETE SET NULL, #upon deleting a player, do not delete the associated match
    FOREIGN KEY (tourney_id, match_id) REFERENCES Matches(tourney_id, match_id)
);

