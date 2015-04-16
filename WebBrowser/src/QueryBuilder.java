import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QueryBuilder {
	
	public static Query parse(String query) throws BadQueryFormatException {
		// modify query 
		query = query.trim().toLowerCase();
		if (!checkRegularForm(query)) {
			System.err.println("Query is not corrected.");
			throw new BadQueryFormatException();
		}
		
		// start parsing
		// atomic query
		if (!query.contains("(") && !query.contains(")")) {
			if (checkRegularCharacter(query)) {
				return new AtomicQuery(query);
			}
			throw new BadQueryFormatException();
		}
		
		// not query
		if (query.startsWith("not")) {			
			query = query.substring(query.indexOf("(") + 1, query.lastIndexOf(")")).trim();
			if (checkRegularCharacter(query)) {
				return new NotQuery(query);
			}
			throw new BadQueryFormatException();
		}
		
		// and query
		if (query.startsWith("and")) {
			ArrayList<Query> andQuerys = new ArrayList<>();
			query = query.replaceFirst("and", "").trim();
			query = query.substring(1, query.length() - 1);
			String[] queryStrings = QueryBuilder.split(query);
			for (String singleQuery : queryStrings) {
				andQuerys.add(QueryBuilder.parse(singleQuery));
			}
			return new AndQuery(andQuerys);
		}
		
		// or query
		if (query.startsWith("or")) {
			ArrayList<Query> orQuerys = new ArrayList<>();
			query = query.replaceFirst("or", "").trim();
			query = query.substring(1, query.length() - 1);
			String[] queryStrings = QueryBuilder.split(query);
			for (String singleQuery : queryStrings) {
				orQuerys.add(QueryBuilder.parse(singleQuery));
			}
			return new OrQuery(orQuerys);
		}
		throw new BadQueryFormatException();
	}
	
	public static String[] split(String query) {
		ArrayList<String> queryStrings = new ArrayList<>();
		int bracketCount = 0;
		int lastIndex = 0;
		for (int i = 0; i < query.length(); i++) {
			char charCursor = query.charAt(i);
			if (charCursor == ',' && bracketCount == 0) {
				queryStrings.add(query.substring(lastIndex, i));
				lastIndex = i + 1;
			} else if (i == query.length() - 1) {
				queryStrings.add(query.substring(lastIndex, i + 1));
			}
			if (charCursor == '(') {
				bracketCount++;
			}
			if (charCursor == ')') {
				bracketCount--;
			}
		}
		return queryStrings.toArray(new String[queryStrings.size()]);
	}
	
	public static Query parseInfixForm(String query) {
		return new AtomicQuery(query);
	}
	
	// check regular format
	public static boolean checkRegularForm(String query) throws BadQueryFormatException{
		if (countOccurrences(query, '(') != countOccurrences(query, ')')) {
			return false;
		}
		if (!query.contains("(") || !query.contains(")")) {
			checkRegularCharacter(query);
		}
		return true;
	}
	
	// check if a query contains regular characters only
	public static boolean checkRegularCharacter(String query) throws BadQueryFormatException {
		// use regex to check
		Pattern pattern = Pattern.compile("[\\W]");
		Matcher matcher = pattern.matcher(query);
		if (matcher.find()) {
			System.err.println(query + " is a wrong query.");
			throw new BadQueryFormatException();
		}
		return true;
	}
	
	// check infix format
	public static boolean checkInfixForm(String query) {
		if (query.contains("(") || query.contains(")")) {
			return false;
		}
		return true;
	}
	
	public static int countOccurrences(String haystack, char character) {
		if (haystack == null) {
			return 0;
		}
		int count = 0;
		for (int i = 0; i < haystack.length(); i++) {
			if (haystack.charAt(i) == character) {
				++count;
			}
		}
		return count;
	}


	public static void main(String[] args) {
		long startTime = System.nanoTime();
		String a = " and ( a , b )";

		try {
			Query myQuery = QueryBuilder.parse(a);
			System.out.println(myQuery);
		} catch (Exception e) {
			System.out.println(e);
		}

		long endTime = System.nanoTime();
		
		long durantion = (endTime - startTime);
		System.out.println("execution time is " + durantion / 1000000.0 + "ms");


	}
}
