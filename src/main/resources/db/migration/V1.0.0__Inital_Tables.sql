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
)

CREATE TABLE game_board (
    game_id INTEGER NOT NULL
    player_id VARCHAR(80) NOT NULL,
    CONSTRAINT game_board_id_pk PRIMARY KEY (game_id, player_id)
    -- Maybe lost
)

CREATE TABLE friendly_grid (
    game_id INTEGER NOT NULL
    player_id VARCHAR(80) NOT NULL,
    CONSTRAINT friendly_grid_pk PRIMARY KEY (game_id, player_id)
)

CREATE TABLE friendly_cell (
    game_id INTEGER NOT NULL,
    player_id VARCHAR(80) NOT NULL,
    cell_index INTEGER NOT NULL,

    CONSTRAINT game_board_id_pk (game_id, player_id),
    CONSTRAINT cell_id_pk PRIMARY KEY (game_board_id_pk, cell_index)

    cell_status VARCHAR(10)
)

CREATE TABLE enemy_grid (
    game_id INTEGER NOT NULL
    player_id VARCHAR(80) NOT NULL,
    CONSTRAINT enemy_grid_pk PRIMARY KEY (game_id, player_id)
)

CREATE TABLE enemy_cell (
    game_id INTEGER NOT NULL,
    player_id VARCHAR(80) NOT NULL,
    cell_index INTEGER NOT NULL,

    CONSTRAINT game_board_id_pk (game_id, player_id),
    CONSTRAINT cell_id_pk PRIMARY KEY (game_board_id_pk, cell_index)

    cell_status VARCHAR(10)
)