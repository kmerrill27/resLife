package resLife;

/**
 * Generate standard HTML header and footer with fixed navbar.
 * 
 * Kim Merrill & Richard Yannow
 */
public class HtmlHead {
	
	// Static header for HTML pages with links to stylesheets.
	private static String head =
		"<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Strict//EN'" + 
		"'http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd'>" +
		"<html xmlns='http://www.w3.org/1999/xhtml' xml:lang='en' lang='en'>" +
		"<head>" +
		"<meta http-equiv='Content-Type' content='text/html; charset=utf-8'/>" +
		"<link rel='stylesheet' type='text/css' href='bootstrap.css'/>" +
		"<link rel='stylesheet' type='text/css' href='resLife.css' /><title>";
		
	// Fixed navbar head for HTML pages with links to other pages.
	private static String navbar =
		"</title></head> <body><div class='navbar navbar-fixed-top'> " +
		"<div class='navbar-inner'> <div class='container'> <ul class='nav'> " +
		"<li class='brand'><a title='Home' href='index.html'> " +
		"<img alt='logo' src='logo.gif'></img></a></li> " +
		"</li></ul><ul class='nav pull-right'> " +
		"<li><a title='Welcome page' href='index.html'>Home</a></li> " +
		"<li><a title='Rate your current room' href='index.html'>Ratings</a></li> " +
		"<li><a title='Find your friends' href='student.html'>Students</a></li> " +
		"<li><a title='Search by room' href='room.html'>Rooms</a></li> " +
		"<li><a title='Search by suite' href='suite.html'>Suites</a></li> " +
		"</ul></div></div></div><div class='padded-top'><div class='jello'>" +
		"<div class='well'>";
	
	// Footer opening tags for HTML pages with back link.
	private static String footerFront = "<br/><div class='center'><a href='";
	
	// Footer closing tags for HTML pages.
	private static String footerBack = 	"' class='btn btn-info btn-large'>" +
		"Back</a></div></div></body></html>";
	
	/**
	 * Return formatted HTML header and navbar with given title.
	 */
	public static String getHeader(String title) {
		return (head + title + navbar);
	}
	
	/**
	 * Return formatted HTML footer with back link to given page.
	 */
	public static String getFooter(String page) {
		return (footerFront + page + footerBack);
	}

}
