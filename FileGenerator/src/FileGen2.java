import java.io.*;
import java.util.*;

/* 
 * This code generates internally consistent data sets for Students, rooms, groups, bathrooms, ratings, and floors.
 *
 */
public class FileGen2 {

	// Number of instances of each entity to be created
	protected static int NUM_INSTANCES = 50;

	private static Random randgen = new Random();
	private static PrintWriter fileWriter = null;
	private static PrintWriter roomfileWriter = null;
	private static PrintWriter groupfileWriter = null;
	private static PrintWriter bathroomfileWriter = null;
	private static PrintWriter ratingsfileWriter = null;
	private static PrintWriter floorsfileWriter = null;
	
	// Possible dorms
	private static String[] dorms = { "B Hall", "Sontag", "Clark I", "Clark V",
			"Clark III", "Walker", "Lawry", "Norton", "Smiley", "Oldenborg",
			"Wig", "Lyon", "Harwood", "Gibson", "Mudd", "Blaisdell" };

	// Possible class years
	private static int[] syear = { 2015, 2014, 2013, 2012 };

	// Possible sexes
	private static String[] sex = { "m", "f" };
	private static String[] bathroomtype = { "m", "f", "n" };

	// Possible student positions
	private static String[] title = { "sponsor", "ra", "head sponsor",
			"aspc officer", "posse scholar", "questbridge scholar",
			"varsity athlete", null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null };

	// Possible first names
	private static String[] fnames = { "Adam", "Ann", "Art", "Amy", "Andrew",
			"Asuman", "Alfred", "Audrey", "April", "Anthony", "Andrea",
			"Arash", "Amilia", "Bonny", "Bobby", "Bevin", "Bettina", "Chris",
			"Christie", "Charles", "Carrie", "Cindy", "Cheryl", "Craig",
			"Daniel", "Debbie", "Dustin", "Delia", "Diane", "Edward", "Ellen",
			"Eric", "Esther", "Frank", "Fay", "Farhad", "Fred", "Gene", "Gary",
			"Gerry", "Gerald", "Grechen", "Gaston", "Gregg", "Heather",
			"Henry", "Harry", "Hilary", "Ian", "Igor", "Jeff", "John", "Jack",
			"James", "Joe", "Jerome", "Jorge", "Jennifer", "Joshua", "Jodie",
			"Jay", "June", "Kimberly", "Kersey", "Kathleen", "Karen", "Kay",
			"Larry", "Lynn", "Lauren", "Lisa", "Micheal", "Michelle", "Minju",
			"Mario", "Mary", "Miriam", "Mark", "Maria", "Majorie", "Mercedes",
			"Nancy", "Nellie", "Newton", "Pam", "Peter", "Penny", "Phillip",
			"Pari", "Patrick", "Paul", "Ron", "Rob", "Rod", "Roger", "Robert",
			"Richard", "Sally", "Shannah", "Sonny", "Sven", "Shana", "Steven",
			"Scott", "Sam", "Sandra", "Sarah", "Tom", "Todd", "Timothy",
			"Tobias", "Ulrich", "Vaughn", "Walter", "Wayne", "William", "Zach",
			"Abir", "Acelin", "Acton", "Adair", "Adar", "Addison", "Aden",
			"Adir", "Aiken", "Aimon", "Ainsley", "Ajani", "Akira", "Ali",
			"Amadeus", "Amal", "Amir", "Ammon", "Amon", "Aneurin", "Angelo",
			"Annan", "Ansari", "Anwar", "Archer", "Arden", "Ari", "Ariel",
			"Arion", "Arje", "Arjuna", "Arley", "Arlo", "Asher", "Ashlin",
			"Asim", "Aston", "Avan", "Ayer", "Bae", "Bakari", "Barak", "Beck",
			"Benedict", "Blaine", "Blair", "Blaise", "Boyce", "Brady", "Brae",
			"Brock", "Brodie", "Bryn", "Bryce", "Cadell", "Cadmus", "Caedmon",
			"Caesar", "Cahil", "Cailean", "Cain", "Caleb", "Camden", "Carey",
			"Carlin", "Carne", "Carrik", "Carson", "Casey", "Caspar",
			"Cassidy", "Cathan", "Cato", "Cavan", "Chad", "Chaim", "Chaney",
			"Chase", "Chet", "Chiron", "Cian", "Ciaran", "Ciro", "Clay",
			"Cleary", "Cody", "Cole", "Conall", "Conan", "Conn", "Corey",
			"Crewe", "Crispin", "Cristian", "Cristo", "Cy", "Cyrus", "Dacey",
			"Dai", "Dakota", "Daley", "Damas", "Damon", "Dane", "Dante",
			"Darby", "Darcy", "Darien", "Darius", "Darnell", "Delmar", "Demas",
			"Dev", "Diego", "Dimitri", "Donato", "Drew", "Duarte", "Dyami",
			"Dyre", "Eaton", "Edison", "Ehren", "Elan", "Eli", "Ellery",
			"Emerson", "Emil", "Emyr", "Ennis", "Ephraim", "Estes", "Etienne",
			"Evander", "Ezra", "Fabian", "Fabrice", "Fane", "Farquhar",
			"Fariss", "Favian", "Fenn", "Fidel", "Fineas", "Finian", "Finn",
			"Flavien", "Frayne", "Gabriel", "Galen", "Garek", "Gaston",
			"Geary", "Germaine", "Garvais", "Gianni", "Giles", "Giordano",
			"Jourdano", "Girvan", "Grady", "Graison", "Gye", "Haine", "Hakim",
			"Hakon", "Haley", "Hamlin", "Hamon", "Hanan", "Hani", "Hannes",
			"Harlan", "Harper", "Harvae", "Hasim", "Hassan", "Haven", "Heath",
			"Hemi", "Howell", "Hume", "Hunter", "Hyam", "Iago", "Iden",
			"Ihaka", "Innes", "Ira", "Issac", "Isaiah", "Israel", "Ives",
			"Jacek", "Jael", "Jai", "Jaleel", "Jamal", "Jaron", "Javed",
			"Jervase", "Jesse", "Jett", "Joachim", "Jourdan", "Kadir", "Kamal",
			"Kane", "Karan", "Kareem", "Kasim", "Kavan", "Keane", "Keenan",
			"Kelan", "Kent", "Kerne", "Khalil", "Kieran", "Killian", "Kim",
			"Kinnard", "Kinsey", "Kiran", "Kito", "Kolya", "Kyne", "Lachlan",
			"Lafayette", "Lal", "Lamar", "Lamech", "Lamont", "Laine", "Lani",
			"Lann", "Lars", "Leal", "Leif", "Lennon", "Lennox", "Levin",
			"Levi", "Linton", "Lorant", "Lorenzo", "Lorne", "Lucian", "Madoc",
			"Mahir", "Mahon", "Makani", "Makis", "Maksim", "Malachi", "Malik",
			"Malin", "Malise", "Manas", "Manaia", "Mani", "Manu", "Marcello",
			"Marius", "Marley", "Marlon", "Mason", "Massimo", "Matai", "Mayan",
			"Mayer", "Melor", "Mercer", "Micah", "Midas", "Milan", "Miles",
			"Milos", "Mischa", "Myall", "Myron", "Naaman", "Nadir", "Nairn",
			"Namir", "Narayn", "Nasir", "Niall", "Nico", "Nikita", "Nils",
			"Niran", "Nishan", "Nur", "Nye", "Nyall", "Omar", "Oran", "Orion",
			"Orlando", "Orton", "Paige", "Paine", "Paris", "Parker", "Pascal",
			"Payton", "Pearce", "Perry", "Phelan", "Philemon", "Phineas",
			"Phoenix", "Pearson", "Qadir", "Qasim", "Quillan", "Quilliam",
			"Quincy", "Quinlan", "Quinn", "Rafe", "Rafferty", "Rafi", "Rafiq",
			"Rainer", "Ramiro", "Rasheed", "Ravi", "Reeve", "Reid", "Regan",
			"Reilly", "Remy", "Renato", "Renzo", "Riordan", "Roarke", "Rocco",
			"Rohan", "Roman", "Ronan", "Rory", "Roshan", "Royce", "Rune",
			"Ryder", "Back", "Sabian", "Sacha", "Sage", "Saleem", "Sancho",
			"Sandor", "Santos", "Saul", "Saviero", "Saville", "Saxton",
			"Sayed", "Sebastian", "Semyon", "Seth", "Severn", "Shae", "Shakir",
			"Sharif", "Sheehan", "Shen", "Sheridan", "Shiloh", "Sloane", "Sol",
			"Soren", "Steele", "Steede", "Sumner", "Swayne", "Tahir", "Tai",
			"Tait", "Tanner", "Tariq", "Tarn", "Tarquin", "Tarun", "Tashi",
			"Tehan", "Thane", "Theon", "Thierry", "Tierney", "Timon", "Tobias",
			"Torin", "Travis", "Tremaine", "Tynan", "Tyne", "Tyson", "Ultan",
			"Upton", "Uriah", "Urian", "Vachel", "Valentino", "Valerian",
			"Valles", "Van", "Varden", "Varian", "Varick", "Vartan", "Venn",
			"Vere", "Vidal", "Vidas", "Vidya", "Vijay", "Vitale", "Vyasa",
			"Wade", "Waite", "Weston", "Willard", "Wren", "Xavier", "Xylon",
			"Yael", "Yannis", "Yasir", "Yeshe", "Yves", "Zahir", "Zaki",
			"Zamir", "Zaine", "Zed", "Zeke" };

	// Possible last names
	private static String[] lnames = { "Adams", "Adamson", "Adolfo",
			"Adolphson", "Aerus", "Affin", "Aksoy", "Antecol", "Appel",
			"Appelbaum", "Amacost", "Arndt", "Ascher", "Ashmiller", "Black",
			"BlackBurn", "Blackham", "Bishop", "Brown", "Brunvand", "Baduini",
			"Balitzer", "Bessette", "Bilger", "Black", "Biltz", "Blomberg",
			"Bowman", "Bradley", "Burdekin", "Burton", "Busch", "Chang", "Chi",
			"Chin", "Clinton", "Calichman", "Camp", "Campbell", "Candaele",
			"Casad", "Castro", "Chamorel", "Chase", "Chorba", "Clark", "Cody",
			"Dimagio", "Davis", "Dershem", "Douville", "Eby", "Edwards",
			"Elliott", "Espinosa", "Eldregde", "Eggett", "Ehrig", "Egg",
			"Fahim", "Featherstone", "Flatt", "Faggen", "Farell", "Fucaloro",
			"Garris", "Gilbert", "Goeree", "Goodrich", "Gould", "Greth",
			"Griffiths", "Gross", "Goss", "Guthrie", "Hahn", "Hanscom",
			"Heerwald", "Hiskey", "Haley", "Haskell", "Helland", "Hess",
			"Higdon", "Huang", "Jackson", "Kell", "Keil", "Kind", "Kim",
			"Kirkland", "Kennedy", "Kerry", "Krauss", "Lee", "Lepereu", "Lim",
			"Lindstrom", "Mayfield", "Mansfield", "Monroe", "Myer", "Miller",
			"Milton", "Mintz", "Miyasaki", "Nelson", "Peterson",
			"Petersen", "Riloff", "Robinson", "Sampson", "Slind", "Smith",
			"Thom", "Thompson", "Trang", "Williams", "Woods", "Zachary",
			"Zamin", "Zone" };

	// Number at which id generator starts
	private static int idgenerator = 1;

	/**
	 * Generates random Students and writes them to corresponding files.
	 **/
	public FileGen2() {
		// createProfessorFile(5);
		createStudentFile();
	}

	/**
	 * Create file, students.txt, of randomly generated students.
	 **/
	private static void createStudentFile() {
		try {
			File groups = new File("groups.txt");
			if (!groups.exists()) {
				groups.createNewFile();
			}
			fileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					".//students.txt")));
			roomfileWriter = new PrintWriter(new BufferedWriter(new FileWriter(
					".//rooms.txt")));
			groupfileWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(".//groups.txt")));
			bathroomfileWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(".//bathrooms.txt")));
			ratingsfileWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(".//ratings.txt")));
			floorsfileWriter = new PrintWriter(new BufferedWriter(
					new FileWriter(".//floors.txt")));


			String dorm, roomnum;
			int floor, bottomfloor, roomsonfloor = 0, topfloor, groupnum, roomcapacity, numindivrooms = 0, indivroomscap = 0;
			int spogrosize = 0, numspogros = 0, spogrocapacity = 0;
			int suitesize = 0, suitecapacity = 0, numsuites = 0, roomsingroups = 0, groupsonfloor = 0;
			int bathroomid;

			// for (int i = 0; i < n; i++) {

			for (int k = 0; k < dorms.length; k++) {
				// for (int k = 0; k < 5; k++) {
				//System.out.println("test");
				bottomfloor = 1;
				dorm = dorms[k]; // dorm

				// Determine floor - only certain floor values
				// are valid for each dorms
				if (dorm == "Gibson") {
					topfloor = 1;
				} else if (dorm == "Mudd" || dorm == "Blaisdell"
						|| dorm == "Harwood") {
					topfloor = 3;
					// Basement is floor 0
					bottomfloor = 0;
				} else if (dorm == "Lawry" || dorm == "Clark III"
						|| dorm == "Smiley" || dorm == "Oldenborg") {
					topfloor = 3;
				} else if (dorm == "Wig") {
					bottomfloor = 0;
					topfloor = 2;
				} else {
					topfloor = 2;
				}
				for (floor = bottomfloor; floor <= topfloor; floor++) {
					floorsfileWriter.print("UPDATE Floors \n");
					floorsfileWriter.print("SET RA="+rand(2500)+"\n");
					floorsfileWriter.print("WHERE dname='"+dorm+"' AND fnum="+floor+";\n");
					bathroomid=1;
					if (dorm == "B Hall" || dorm == "Sontag") {
						roomsingroups = 48;
						suitesize = 6;
						suitecapacity = 6;
						numsuites = 8;
						numspogros= 0;
						numindivrooms=0;
						indivroomscap=0;
					} else if (dorm == "Clark I" || dorm == "Clark V") {
						roomsingroups = 48;
						suitesize = 4;
						suitecapacity = 6;
						numsuites = 12;
						numspogros= 0;
						numindivrooms=10;
						indivroomscap=18;
					}
					// checking for floor doesn't work
					else if (((dorm == "Mudd" || dorm == "Gibson"
							|| dorm == "Blaisdell" || dorm == "Lyon" || dorm == "Harwood") && floor == 1)
							|| (dorm == "Wig" && floor != 1)) {
						spogrosize = 9;
						numspogros = 2;
						spogrocapacity = 14;
						suitecapacity = 3;
						suitesize = 2;
						numsuites = 11;						
						numindivrooms = 10;
						indivroomscap = 12;
					} else if (((dorm == "Mudd"
						|| dorm == "Blaisdell" || dorm == "Lyon" || dorm == "Harwood") && floor == 2)
						|| (dorm == "Wig" && floor == 1)) {
						spogrosize = 12;
						numspogros = 3;
						spogrocapacity = 14;
						numsuites=0;
						numindivrooms = 8;
						indivroomscap = 10;
					}
					else if (dorm == "Walker" && floor == 2){
					spogrosize = 14;
					numspogros = 1;
					spogrocapacity = 18;
					numsuites = 7;
					suitesize = 4;
					suitecapacity = 4;
					numindivrooms = 16;
					indivroomscap = 20;
					
					} else if (dorm == "Oldenborg"){
						numspogros = 0;
						numsuites = 47;
						suitesize = 2;
						suitecapacity = 2;
						numindivrooms = 0;
						indivroomscap = 0;
					} else {
						numspogros = 0;
						numsuites = 8;
						suitecapacity = 4;
						suitesize = 3;
						numindivrooms = 14;
						indivroomscap = 22;
					}
					
					roomsingroups = (numspogros * spogrosize)
					+ (numsuites * suitesize);
					groupsonfloor = numspogros + numsuites + numindivrooms;		

					for (int x = 1; x <= groupsonfloor; x++) {
						groupfileWriter.print("(" + x + ", ");
						groupfileWriter.print(floor + ", ");
						groupfileWriter.print("'" + dorm + "', ");
						// if in spogro print out spogro size and cap
						if (x <= numspogros) {
							groupfileWriter.print(spogrosize + ", ");
							groupfileWriter.print(spogrocapacity);
						}
						// else if in suite print suite size and cap
						else if (x <= (numspogros + numsuites)) {
							groupfileWriter.print(suitesize + ", ");
							groupfileWriter.print(suitecapacity);
							if (rand(100) > 55){
								bathroomfileWriter.print("(" + (bathroomid++) + ", ");
								bathroomfileWriter.print(floor + ", ");
								bathroomfileWriter.print("'" + dorm + "', ");
								bathroomfileWriter.print(x + ", ");
								bathroomfileWriter.print(70+rand(50) + ", ");
								bathroomfileWriter.print("'" + bathroomtype[rand(bathroomtype.length)] + "'), \n");	
							}
						}
						// else in indiv room so print 1 and cap
						else {
							groupfileWriter.print(1 + ", ");
							if (x - (numspogros + numsuites) <= indivroomscap
									- numindivrooms) {
								groupfileWriter.print(2);
							} else {
								groupfileWriter.print(1);
							}
						}
						groupfileWriter.print("), \n"); // add blank line
					}
					roomsonfloor = roomsingroups + numindivrooms;
					for (int j = 1; j <= roomsonfloor; j++) {
						roomnum = "" + j;
						if (roomnum.length() == 1) {
							String a = "" + 0 + "" + j;
							roomnum = a;
						}
						if (j <= spogrosize * numspogros) {
							groupnum = 1 + ((j - 1) / spogrosize);
							if ((spogrocapacity - spogrosize) > j % spogrosize) {
								roomcapacity = 2;
							} else {
								roomcapacity = 1;
							}
						} else if (j <= roomsingroups) {
							groupnum = numspogros
									+ 1
									+ (((j - (spogrosize * numspogros)) - 1) / suitesize);
							//System.out.println("" + dorm + " " + groupnum + " "
							//	+ floor);
							// if the number of doubles required is greater
							// than roomnum mod groupsize
							if ((suitecapacity - suitesize) > j % suitesize) {
								// then put one double for every point of
								// difference between capacity and size
								roomcapacity = 2;
							} else {
								roomcapacity = 1;
							}
						} else {
							groupnum = j - roomsingroups + numsuites
									+ numspogros;
							if (indivroomscap - numindivrooms >= j
									- roomsingroups) {
								roomcapacity = 2;
							} else {
								roomcapacity = 1;
							}
						}
						roomfileWriter.print("(" + roomnum + ", ");
						roomfileWriter.print(groupnum + ", ");
						roomfileWriter.print(floor + ", ");
						roomfileWriter.print("'" + dorm + "', ");
						roomfileWriter.print(100 + rand(150) + ", ");
						roomfileWriter.print(roomcapacity);
						roomfileWriter.print("), \n");
						
						if (rand(100)>60){
							ratingsfileWriter.print("("+roomnum + ", ");
							ratingsfileWriter.print("'" + dorm + "', ");
							ratingsfileWriter.print(floor + ", ");
							ratingsfileWriter.print(""+ 2011 + ", ");
							ratingsfileWriter.print("'Spring', ");
							ratingsfileWriter.print(1+ rand(5) +", ");
							ratingsfileWriter.print(1+ rand(5) +", ");
							ratingsfileWriter.print(1+ rand(5) +"), \n");
						}
						// r = new Room(roomnum, floor, groupnum, dorm,
						// rand(2),
						// spogro);
						for (int y = 0; y < roomcapacity; y++) {

							fileWriter.print("("+ (idgenerator++) + ", "); // sid
							fileWriter.print(roomnum + ", ");
							fileWriter.print(floor + ", ");
							fileWriter.print("'" + dorm + "', ");
							fileWriter.print("'" + fnames[rand(fnames.length)]
									+ " " + lnames[rand(lnames.length)] + "'" + ", ");
							fileWriter.print(syear[rand(syear.length)] + ", ");
							fileWriter.print("'" + sex[rand(sex.length)] +"'"+"), \n");

						}
					}
				}
			}

			// }
			fileWriter.close();
			roomfileWriter.close();
			groupfileWriter.close();
			bathroomfileWriter.close();
			ratingsfileWriter.close();
			floorsfileWriter.close();
		} catch (IOException e) {
			System.out.println("Error writing student file.");
		}
	}

	/**
	 * Creates file, professors.txt, of randomly generated professors.
	 **/
	/*
	 * private static void createProfessorFile(int n) { try { fileWriter = new
	 * PrintWriter(new BufferedWriter(new FileWriter( ".//professors.txt"))); //
	 * (new BufferedWriter (new FileWriter(".\\professors.txt")));
	 * 
	 * for (int i = 0; i < n; i++) { fileWriter.println(idgenerator++); // pid
	 * fileWriter.println(fnames[rand(fnames.length)] + " " +
	 * lnames[rand(lnames.length)]); // name
	 * 
	 * fileWriter.println(); // add blank line } fileWriter.close(); } catch
	 * (IOException e) { System.out.println("Error writing file."); } }
	 */
	/**
	 * Returns random integer.
	 **/
	private static int rand(int n) {
		return randgen.nextInt(n);
	}

}
