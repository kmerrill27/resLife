/**
  * 
  * A database that tracks residential life at Pomona College.
  *
  * Kim Merrill and Richard Yannow
  * April 2012
  *
  * sudo /usr/local/mysql/bin/mysql
  *
  * Notes:
  * Room numbers are unique per dorm, but repeat across dorms.
  * Floor numbers are unique per dorm, but repeat across dorms.
  * Suites may have multiple private bathrooms.
  * Only one rating may be made per semester of each schoolyear because
  * only one student/group of students (if double) may live in a room
  * each semester. Allowing one student to rate a room several times
  * will skew average rating values.
  *
  **/

DROP DATABASE IF EXISTS resLife; /* Clear existing database. */

CREATE DATABASE resLife;

GRANT ALL PRIVILEGES ON resLife.* to
      dbproject@localhost IDENTIFIED by 'merrillyannow';

USE resLife;

CREATE TABLE Dorms (
       dname VARCHAR(40),
       address VARCHAR(300),
       year INTEGER, /* Year dorm was built. */
       ac boolean, /* True if dorm has air-conditioning. */
       PRIMARY KEY (dname),
       UNIQUE (address) /* Every dorm must be located at a different address. */
);

CREATE TABLE Floors (
       fnum INTEGER,
       dname VARCHAR(40),
       subfree BOOLEAN, /* True if subfree, false if subop. */
       laundry BOOLEAN, /* True if floor contains laundry facility. */
       ra INTEGER, /* A student; added as foreign key later bc of mutual dependencies */
       PRIMARY KEY (fnum, dname),
       FOREIGN KEY (dname) REFERENCES Dorms(dname)
       	       ON UPDATE CASCADE
	       ON DELETE CASCADE
);

CREATE TABLE Groups (
       gnum INTEGER,
       fnum INTEGER,
       dname VARCHAR(40),
       size INTEGER, /* Number of rooms in suite. */
       capacity INTEGER, /* Max number of Students suite holds. */
       PRIMARY KEY (gnum, fnum, dname),
       FOREIGN KEY (fnum, dname) REFERENCES Floors(fnum, dname)
       	       ON UPDATE CASCADE
	       	   ON DELETE CASCADE
);

CREATE TABLE Rooms (
       rnum INTEGER,
       gnum INTEGER,
       fnum INTEGER,
       dname VARCHAR(40),
       sqft INTEGER,
       rcapacity INTEGER NOT NULL, /* 1=single, 2=double */
       noise REAL DEFAULT 0, /* Average ratings. */
       view REAL DEFAULT 0,
       cell REAL DEFAULT 0,
       PRIMARY KEY (rnum, fnum, dname),
       FOREIGN KEY (gnum, fnum, dname) REFERENCES Groups (gnum, fnum, dname)
       		   ON UPDATE CASCADE
       		   ON DELETE CASCADE,
       CHECK (rcapacity=1 OR rcapacity=2)
);

CREATE TABLE Bathrooms (
       bnum INTEGER, /* Numbers may repeat per group. */
       fnum INTEGER,
       dname VARCHAR(40),
       gnum INTEGER,
       sqft INTEGER,
       gender VARCHAR(10),
       PRIMARY KEY (bnum, fnum, dname),
       FOREIGN KEY (gnum, fnum, dname) REFERENCES Groups(gnum, fnum, dname),
       CHECK (gender='m' OR gender='f' OR gender='n') /* Male, female, or gender neutral. */
);

CREATE TABLE Students (
       sid INTEGER, /* Unique student id number. */
       rnum INTEGER, /* Room in which student lives. Null if off-campus. */
       fnum INTEGER,
       dname VARCHAR(40),
       name VARCHAR(60) NOT NULL,
       year INTEGER NOT NULL, /* Class year. */
       sex VARCHAR(10), /* Designated as 'male' or 'female'. */
       PRIMARY KEY (sid),
       FOREIGN KEY (rnum, fnum, dname) REFERENCES Rooms(rnum, fnum, dname),
       CHECK (sex="f" OR sex="m")
);

/* Sponsor groups are freshman halls. */
CREATE TABLE Spogros (
       hname VARCHAR(20), /* Back, side, front, east, west, etc. */
       gnum INTEGER,
       fnum INTEGER,
       dname VARCHAR(40),
       theme VARCHAR(40), /* Hall theme: 'Pixar', 'Harry Potter', etc. */
       team VARCHAR(40), /* Freshman cup team. */
       sponsor1 INTEGER, 
       sponsor2 INTEGER,
       headsponsor INTEGER,
       PRIMARY KEY (hname, fnum, dname),
       FOREIGN KEY (gnum, fnum, dname) REFERENCES Groups (gnum, fnum, dname)
       	       ON UPDATE CASCADE
	       ON DELETE CASCADE,
       FOREIGN KEY (sponsor1) REFERENCES Students(sid),
       FOREIGN KEY (sponsor2) REFERENCES Students(sid),
       FOREIGN KEY (headsponsor) REFERENCES Students(sid)
);

CREATE TABLE Ratings (
       rnum INTEGER,
       dname VARCHAR(40),
       fnum INTEGER,
       year INTEGER, /* Year ratee lived in room. */
       semester VARCHAR(10), /* Semester in which rating made. */
       comment VARCHAR(1000),
       noise INTEGER, /* Noise rating. */
       cell INTEGER, /* Cell service rating. */
       view INTEGER, /* View rating. */
       PRIMARY KEY (rnum, fnum, dname, year, semester),
       FOREIGN KEY (rnum, fnum, dname) REFERENCES Rooms (rnum, fnum, dname)
       	       ON UPDATE CASCADE
	       ON DELETE CASCADE,
       CHECK (semester='fall' OR semester='spring' OR semester='summer'),
       CHECK (noise >= 1 AND noise <= 5), /* Ratings must be 1-5 stars. */
       CHECK (cell >= 1 AND cell <= 5),
       CHECK (view >= 1 AND view <= 5)
);

/* An RA is a student - one RA can serve multiple floors. */
/* Mutually dependent constraint must be added in later
 * because Students references Rooms which references Floors
 * and Floors references Students.*/
ALTER TABLE Floors
ADD FOREIGN KEY (ra) REFERENCES Students(sid);

DELIMITER $$

/** Trigger that recalculates a room's average rating
  * for each category each time a new rating is added.
  * Ratings should never be updated, so a similar
  * trigger is not needed after update. **/
CREATE TRIGGER averageRating AFTER INSERT ON Ratings
       FOR EACH ROW BEGIN
	UPDATE Rooms Rm SET cell = 
      	      (SELECT AVG(R.cell)
	      FROM Ratings R
	      WHERE R.rnum=NEW.rnum AND R.fnum=NEW.fnum AND R.dname=NEW.dname)
	   WHERE Rm.rnum=NEW.rnum AND Rm.fnum=NEW.fnum AND Rm.dname=NEW.dname;
	UPDATE Rooms Rm SET view = 
       	      (SELECT AVG(R.view)
	      FROM Ratings R
	      WHERE R.rnum = NEW.rnum AND R.fnum=NEW.fnum AND R.dname=NEW.dname)
	   WHERE Rm.rnum=NEW.rnum AND Rm.fnum=NEW.fnum AND Rm.dname=NEW.dname;
	UPDATE Rooms Rm SET noise = 
       	      (SELECT AVG(R.noise)
	      FROM Ratings R
	      WHERE R.rnum = NEW.rnum AND R.fnum=NEW.fnum AND R.dname=NEW.dname)
	   WHERE Rm.rnum=NEW.rnum AND Rm.fnum=NEW.fnum AND Rm.dname=NEW.dname;
END$$

/** Trigger that calculates the number of rooms in a group
  * and the number of students a group holds, based on the
  * rcapacities of the rooms it contains.
  * Infrequently called because database is largely static,
  * making performance concerns negligible. See report for details. **/
CREATE TRIGGER groupSize AFTER INSERT ON Rooms
       FOR EACH ROW
       BEGIN
       	UPDATE Groups G SET size = 
	       (SELECT (COUNT(*))
		FROM Rooms R
		WHERE R.gnum=NEW.gnum AND R.fnum=NEW.fnum AND R.dname=NEW.dname)
	   WHERE G.gnum=NEW.gnum AND G.fnum=NEW.fnum AND G.dname=NEW.dname;
	UPDATE Groups G SET capacity = 
	       (SELECT SUM(R.rcapacity)
		 FROM Rooms R
		 WHERE R.gnum=NEW.gnum AND R.fnum=NEW.fnum AND R.dname=NEW.dname)
	   WHERE G.gnum=NEW.gnum AND G.fnum=NEW.fnum AND G.dname=NEW.dname;
END$$

/** Same action as previous trigger - runs on update. **/
CREATE TRIGGER groupSize2 AFTER UPDATE ON Rooms
       FOR EACH ROW
       BEGIN
       	UPDATE Groups G SET size = 
	       (SELECT (COUNT(*))
		FROM Rooms R
		WHERE R.gnum=NEW.gnum AND R.fnum=NEW.fnum AND R.dname=NEW.dname)
	   WHERE G.gnum=NEW.gnum AND G.fnum=NEW.fnum AND G.dname=NEW.dname;
	UPDATE Groups G SET capacity = 
	       (SELECT SUM(R.rcapacity)
		 FROM Rooms R
		 WHERE R.gnum=NEW.gnum AND R.fnum=NEW.fnum AND R.dname=NEW.dname)
	   WHERE G.gnum=NEW.gnum AND G.fnum=NEW.fnum AND G.dname=NEW.dname;
END$$

DELIMITER ;
