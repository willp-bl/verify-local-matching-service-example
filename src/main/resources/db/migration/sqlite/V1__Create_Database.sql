CREATE TABLE address (
    address_id INTEGER PRIMARY KEY AUTOINCREMENT,
    lines VARCHAR,
    city VARCHAR,
    country VARCHAR,
    postcode VARCHAR,
    international_postcode VARCHAR
);

CREATE TABLE person (
    person_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name VARCHAR,
    middle_name VARCHAR,
    surname VARCHAR,
    address INTEGER,
    date_of_birth DATE,
    FOREIGN KEY(address) REFERENCES address(address_id)
);

CREATE TABLE verifiedPid (
    pid VARCHAR PRIMARY KEY,
    person INTEGER,
    FOREIGN KEY(person) REFERENCES person(person_id)
);