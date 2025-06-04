-- Creazione dello schema
CREATE SCHEMA "MusicDB";

-- Imposta lo schema come default
SET search_path TO "MusicDB";

-- Creazione delle tabelle nello schema MusicDB
CREATE TABLE grouptable (
    groupid INTEGER PRIMARY KEY,
    name VARCHAR(30)
);

CREATE TABLE artist (
    artistid INTEGER PRIMARY KEY,
    name VARCHAR(30),
    groupid INTEGER,
    FOREIGN KEY (groupid) REFERENCES grouptable(groupid)
);

CREATE TABLE genre (
    genreid INTEGER PRIMARY KEY,
    name VARCHAR(30)
);

CREATE TABLE album (
    albumid INTEGER PRIMARY KEY,
    title VARCHAR(30),
    year SMALLINT,
    groupid INTEGER,
    FOREIGN KEY (groupid) REFERENCES grouptable(groupid)
);

CREATE TABLE music (
    musicid INTEGER PRIMARY KEY,
    title VARCHAR(30),
    year SMALLINT,
    authorid INTEGER,
    albumid INTEGER,
    genreid INTEGER,
    FOREIGN KEY (authorid) REFERENCES artist(artistid),
    FOREIGN KEY (albumid) REFERENCES album(albumid),
    FOREIGN KEY (genreid) REFERENCES genre(genreid)
);

CREATE TABLE link (
    musicid INTEGER,
    link VARCHAR(100),
    FOREIGN KEY (musicid) REFERENCES music(musicid)
);

-- Inserimento di dati di esempio

-- Inserimento gruppi
INSERT INTO grouptable (groupid, name) VALUES
(1, 'The Beatles'),
(2, 'Queen');

-- Inserimento artisti
INSERT INTO artist (artistid, name, groupid) VALUES
(1, 'John Lennon', 1),
(2, 'Paul McCartney', 1),
(3, 'Freddie Mercury', 2);

-- Inserimento generi
INSERT INTO genre (genreid, name) VALUES
(1, 'Rock'),
(2, 'Pop'),
(3, 'Classic Rock');

-- Inserimento album
INSERT INTO album (albumid, title, year, groupid) VALUES
(1, 'Abbey Road', 1969, 1),
(2, 'A Night at the Opera', 1975, 2);

-- Inserimento brani musicali
INSERT INTO music (musicid, title, year, authorid, albumid, genreid) VALUES
(1, 'Come Together', 1969, 1, 1, 1),
(2, 'Something', 1969, 2, 1, 2),
(3, 'Bohemian Rhapsody', 1975, 3, 2, 3);

-- Inserimento link
INSERT INTO link (musicid, link) VALUES
(1, 'https://www.youtube.com/watch?v=45cYwDMibGo'),
(2, 'https://www.youtube.com/watch?v=UelDrZ1aFeY'),
(3, 'https://www.youtube.com/watch?v=fJ9rUzIMcZQ');
