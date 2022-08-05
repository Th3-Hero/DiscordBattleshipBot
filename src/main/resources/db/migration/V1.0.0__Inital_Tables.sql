CREATE TABLE player (
    player_id VARCHAR(80) NOT NULL PRIMARY KEY,
    wins INTEGER NOT NULL,
    losses INTEGER NOT NULL,
    hits INTEGER NOT NULL,
    misses INTEGER NOT NULL,
    ships_sunk INTEGER NOT NULL,
    ships_lost INTEGER NOT NULL
);

CREATE SEQUENCE seq_game_id START WITH 1 INCREMENT BY 1;
CREATE TABLE game (
    game_id INTEGER NOT NULL PRIMARY KEY,
    player_one VARCHAR(80) NOT NULL,
    player_two VARCHAR(80) NOT NULL,
    game_status VARCHAR(10) NOT NULL
);

CREATE TABLE game_board (
    game_id INTEGER NOT NULL,
    player_id VARCHAR(80) NOT NULL,
    channel_id VARCHAR(80),
    CONSTRAINT game_board_fk FOREIGN KEY (game_id)
        REFERENCES game (game_id),
    CONSTRAINT game_board_id_pk PRIMARY KEY (game_id, player_id)
);

CREATE TABLE friendly_cell (
    game_id INTEGER NOT NULL,
    player_id VARCHAR(80) NOT NULL,
    cell_index INTEGER NOT NULL,
    cell_status VARCHAR(10),

    CONSTRAINT friendly_cell_fk FOREIGN KEY (game_id, player_id)
        REFERENCES game_board (game_id, player_id),
    CONSTRAINT friendly_cell_id_pk PRIMARY KEY (game_id, player_id, cell_index)
);

CREATE TABLE enemy_cell (
    game_id INTEGER NOT NULL,
    player_id VARCHAR(80) NOT NULL,
    cell_index INTEGER NOT NULL,
    cell_status VARCHAR(10),

    CONSTRAINT enemy_cell_fk FOREIGN KEY(game_id, player_id)
        REFERENCES game_board (game_id, player_id),
    CONSTRAINT enemy_cell_id_pk PRIMARY KEY (game_id, player_id, cell_index)
);