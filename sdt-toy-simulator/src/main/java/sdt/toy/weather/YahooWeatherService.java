/**
 * 
 */
package sdt.toy.weather;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sdt.toy.weather.binding.RSSParser;
import sdt.toy.weather.data.Channel;
import sdt.toy.weather.data.Rss;
import sdt.toy.weather.data.unit.DegreeUnit;

public class YahooWeatherService {

	private static final String WEATHER_SERVICE_BASE_URL = "https://query.yahooapis.com/v1/public/yql";

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	/** Google elevatiion API **/
	public static final String ELEVATION_PARAMETER_NAME = "https://maps.googleapis.com/maps/api/elevation/json";

	public interface LimitDeclaration {

		/**
		 * Limits results to first <code>count</code> {@link Channel}s.
		 * 
		 * @param count
		 *            the limit of the number of results.
		 * @return the results.
		 * @throws JAXBException
		 *             if an error occurs parsing the response.
		 * @throws IOException
		 *             if an error occurs communicating with the service.
		 */
		List<Channel> first(int count) throws JAXBException, IOException;

		/**
		 * Limits results to last <code>count</code> {@link Channel}s.
		 * 
		 * @param count
		 *            the limit of the number of results.
		 * @return the results.
		 * @throws JAXBException
		 *             if an error occurs parsing the response.
		 * @throws IOException
		 *             if an error occurs communicating with the service.
		 */
		List<Channel> last(int count) throws JAXBException, IOException;

		/**
		 * Returns all the results with no limits.
		 * 
		 * @return the results.
		 * @throws JAXBException
		 *             if an error occurs parsing the response.
		 * @throws IOException
		 *             if an error occurs communicating with the service.
		 */
		List<Channel> all() throws JAXBException, IOException;
	}

	// private Logger logger =
	// LoggerFactory.getLogger(YahooWeatherService.class);
	private RSSParser parser;
	private Proxy proxy;

	public YahooWeatherService() throws JAXBException {
		this.parser = new RSSParser();
		this.proxy = Proxy.NO_PROXY;
	}

	public YahooWeatherService(Proxy proxy) throws JAXBException {
		this.parser = new RSSParser();
		this.proxy = proxy;
	}

	/**
	 * Gets the Weather RSS feed.
	 * 
	 * @param woeid
	 *            the location WOEID.
	 * @param unit
	 *            the degrees units.
	 * @return the retrieved Channel.
	 * @throws JAXBException
	 *             if an error occurs parsing the response.
	 * @throws IOException
	 *             if an error occurs communicating with the service.
	 */
	public Channel getForecast(String woeid, DegreeUnit unit) throws JAXBException, IOException {
		QueryBuilder query = new QueryBuilder();
		query.woeid(woeid).unit(unit);
		List<Channel> channels = execute(query.build());
		if (channels.isEmpty())
			throw new IllegalStateException("No results from the service.");
		return channels.get(0);
	}

	/**
	 * Gets the Weather RSS feed for the specified location.
	 * 
	 * @param location
	 *            the location to search.
	 * @param unit
	 *            the degrees units.
	 * @return the limit declaration.
	 */
	public LimitDeclaration getForecastForLocation(String location, DegreeUnit unit) {
		final QueryBuilder query = new QueryBuilder();
		query.location(location).unit(unit);

		return new LimitDeclaration() {

			@Override
			public List<Channel> last(int count) throws JAXBException, IOException {
				query.last(count);
				return execute(query.build());
			}

			@Override
			public List<Channel> first(int count) throws JAXBException, IOException {
				query.first(count);
				return execute(query.build());
			}

			@Override
			public List<Channel> all() throws JAXBException, IOException {
				return execute(query.build());
			}
		};
	}

	/**
	 * Composes the URL with the specified query.
	 * 
	 * @param query
	 *            the query to submit.
	 * @return the composed URL.
	 * @throws Exception
	 */
	public String getWoeid(String text) throws Exception {
		// String urlParam =
		// "http://yahooapis.com/geo/placefinder?text=mumbai&flags=J&oauth_consumer_key=dj0yJmk9Nlo5RldRanR4S3U4JmQ9WVdrOVFVMXlXR0YzTlRBbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD1hZQ--";
		String woeidget = "select * from geo.places where text=\'" + text + "\'";
		StringBuilder url = new StringBuilder(WEATHER_SERVICE_BASE_URL);
		try {
			url.append("?q=").append(URLEncoder.encode(woeidget, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Url encoding failed", e);
		}
		URL obj = new URL(url.toString());
		HttpURLConnection yahoo = (HttpURLConnection) obj.openConnection();
		yahoo.setRequestMethod("GET");
		yahoo.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		yahoo.setRequestProperty("Content-Language", "en-US");
		yahoo.setDoOutput(true);

		StringBuffer response = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(yahoo.getInputStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null) {

			response.append(inputLine);

		}

		in.close();
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(response.toString()));

		org.w3c.dom.Document doc = db.parse(is);
		NodeList nodes = doc.getElementsByTagName("results");

		String woeid;
		Element element = (Element) nodes.item(0);
		woeid = element.getElementsByTagName("woeid").item(0).getTextContent();

		return woeid;
	}

	public double getElevation(float lat, float longi) throws Exception {
		String ELEVATION_API_URL = "https://maps.googleapis.com/maps/api/elevation/json";

		// String USER_AGENT = "Mozilla/5.0";

		String urlParameters = "locations=" + Float.toString(lat) + "," + Float.toString(longi) + "&sensor=true";

		URL obj = new URL(ELEVATION_API_URL + "?" + urlParameters);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod("GET");
		con.setConnectTimeout(2 * 1000);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("Content-Language", "en-US");

		// Send get request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters.toString());
		wr.flush();
		wr.close();
		StringBuilder response = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		String inputLine;

		while ((inputLine = in.readLine()) != null) {

			response.append(inputLine);

		}

		in.close();

		String jsonContent = response.toString();
		JSONObject jobj = new JSONObject(jsonContent);
		JSONObject res = jobj.getJSONArray("results").getJSONObject(0);
		return res.getDouble("elevation");
	}

	public String getCondition(String condition) {
		// System.out.println(condition);
		final Set<String> cloudy = new HashSet<String>(Arrays.asList("Cloudy", "Mostly Cloudy", "Mostly Cloudy (night)",
				"Mostly Cloudy (day)", "Partly Cloudy (night)", "Partly Cloudy (day)", "Tornado", "Tropical Storm",
				"Hurricane", "Severe Thunderstorms", "Thunderstorms", "Mixed Rain and Snow", "Mixed Rain and Sleet",
				"mixed snow and sleet", "Freezing Drizzle", "Drizzle", "Freezing Rain", "Showers", "Showers",
				"Isolated Thunderstorms", "Scattered Thunderstorms", "Scattered Thunderstorms", "Scattered Showers",
				"Partly Cloudy"));
		final Set<String> sunny = new HashSet<String>(Arrays.asList("Windy", "Clear (night)", "Sunny", "Fair (night)",
				"Fair (day)", "Hot", "Fair", "Not Available", "Mostly Clear", "Breezy"));
		final Set<String> snow = new HashSet<String>(
				Arrays.asList("Thundershowers", "Snow Showers", "Isolated Thundershowers", "Mixed Rain and Hail",
						"Snow Flurries", "Light Snow Showers", "Blowing Snow", "Snow", "Hail", "Sleet", "Dust", "Foggy",
						"Haze", "Smoky", "Blustery", "Cold", "Heavy Snow", "Scattered Snow Showers", "Heavy Snow"));
		String cond = (cloudy.contains(condition)) ? "Rain"
				: ((sunny.contains(condition) ? "Sunny" : (snow.contains(condition) ? "Snow" : null)));
		// System.out.println(cond);
		return cond;
	}

	private String composeUrl(String query) {
		// logger.trace("query: {}", query);
		StringBuilder url = new StringBuilder(WEATHER_SERVICE_BASE_URL);
		try {
			url.append("?q=").append(URLEncoder.encode(query, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Url encoding failed", e);
		}
		return url.toString();
	}

	private List<Channel> execute(String query) throws IOException, JAXBException {
		String url = composeUrl(query);
		String xml = retrieveRSS(url);
		Rss rss = parser.parse(xml);
		return rss.getChannels();
	}

	/**
	 * Open the connection to the service and retrieves the data.
	 * 
	 * @param serviceUrl
	 *            the service URL.
	 * @return the service response.
	 * @throws IOException
	 *             if an error occurs during the connection.
	 */
	private String retrieveRSS(String serviceUrl) throws IOException {
		URL url = new URL(serviceUrl);
		URLConnection connection = url.openConnection(proxy);
		InputStream is = connection.getInputStream();
		InputStreamReader reader = new InputStreamReader(is);
		StringWriter writer = new StringWriter();
		copy(reader, writer);
		reader.close();
		is.close();

		return writer.toString();
	}

	/**
	 * Copy the input reader into the output writer.
	 * 
	 * @param input
	 *            the input reader.
	 * @param output
	 *            the output writer.
	 * @return the number of char copied.
	 * @throws IOException
	 *             if an error occurs during the copy.
	 */
	private static long copy(Reader input, Writer output) throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
