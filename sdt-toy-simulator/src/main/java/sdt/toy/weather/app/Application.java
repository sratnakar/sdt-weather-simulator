/**
 * 
 */
package sdt.toy.weather.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import sdt.toy.weather.YahooWeatherService;
import sdt.toy.weather.data.Channel;
import sdt.toy.weather.data.unit.DegreeUnit;

public class Application {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args[0] == null) {
			System.out.println("Please specify the input file.");
			System.out.println("Proper Usage is: java -jar jarfile inputfile");
			System.exit(0);
		}

		/*Read the Input file which contains locations*/
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String strLine;
		String location = null;
		YahooWeatherService service = new YahooWeatherService();

		while ((strLine = br.readLine()) != null) {
			location = strLine.indexOf(',') > 0 ? service.getWoeid("(" + strLine + ")") : service.getWoeid(strLine);
			Channel channel = service.getForecast(location, DegreeUnit.CELSIUS);
			getWeatherDetails(service, channel);
		}
		br.close();
	}
	
	/* The following method will return weather details for the given location */
	
	public static String getWeatherDetails(YahooWeatherService service, Channel channel) throws Exception {
		String city = channel.getLocation().getCity();
		float geoLat = channel.getItem().getGeoLat();
		float geoLong = channel.getItem().getGeoLong();
		double elevation = service.getElevation(geoLat, geoLong);
		Date date = channel.getItem().getCondition().getDate();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		String nowAsString = df.format(date);
		String cond = channel.getItem().getCondition().getText();
		String condition = service.getCondition(cond);
		int temp = channel.getItem().getCondition().getTemp();
		double pressure = (channel.getAtmosphere().getPressure()) / 68.94757293168;
		float humidity = channel.getAtmosphere().getHumidity();
		String weatherDetails = city + "|" + geoLat + "," + geoLong + "," + elevation + "|" + nowAsString + "|"
				+ condition + "|" + temp + "|" + pressure + "|" + humidity;
		System.out.println(weatherDetails);
		return weatherDetails;

	}

}
