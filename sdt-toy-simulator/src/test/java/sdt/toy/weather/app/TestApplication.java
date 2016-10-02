package sdt.toy.weather.app;

import org.junit.Test;

import sdt.toy.weather.YahooWeatherService;
import sdt.toy.weather.data.Channel;
import sdt.toy.weather.data.unit.DegreeUnit;

import static org.junit.Assert.assertEquals;

public class TestApplication {

	@Test
	public void testWeatherDetails() throws Exception {
		
		String expected = "Kolkata|22.54399,88.37908,3.023699283599854|2016-09-17T12:30Z|Rain|30|493.11901680266385|76.0";
		YahooWeatherService service = new YahooWeatherService();
		
		String location = service.getWoeid("kolkata");
		Channel channel = service.getForecast(location, DegreeUnit.CELSIUS);
		String actual = Application.getWeatherDetails(service, channel);
		assertEquals(actual, expected);
	}

}
