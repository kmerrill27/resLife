/**
 * Sample data.
 */

INSERT INTO Groups (gnum, fnum, dname) VALUES
       (1, 1, "Mudd"), 
       (2, 2, "Smiley"),
       (3, 2, "Mudd"),
       (4, 1, "Wig");

INSERT INTO Rooms (rnum, gnum, fnum, dname, rcapacity, sqft) VALUES
       (10, 1, 1, "Mudd", 1, 100),
       (12, 1, 1, "Mudd", 1, 200),
       (24, 1, 1, "Mudd", 2, 300),
       (23, 2, 2, "Smiley", 2, 90),
       (10, 3, 2, "Mudd", 1, 90),
       (88, 4, 1, "Wig", 2, 140);

INSERT INTO Bathrooms (bnum, gnum, fnum, dname, sqft, gender) VALUES
       (72, 1, 1, "Mudd", 80, "m"),
       (43, 2, 2, "Smiley", 90, "n"),
       (12, 3, 2, "Mudd", 100, "f");

INSERT INTO Students (sid, rnum, fnum, dname, name, sex, year) VALUES
       (20, 10, 1, "Mudd", "John Smith", "m", 2014),
       (21, 88, 1, "Wig", "Lauren Jones", "f", 2013),
       (22, 88, 1, "Wig", "Maria Tucker", "f", 2013),
       (23, 10, 2, "Mudd", "Bob Erickson", "m", 2015);

INSERT INTO Spogros (hname, gnum, fnum, dname, theme, team, headsponsor, sponsor1, sponsor2) VALUES
       ("Back", 1, 1, "Mudd", "HP", "Go", 20, 21, 22),
       ("Front", 4, 1, "Wig", "Pixar", "Go", 20, 23, 21);