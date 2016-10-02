package sdt.toy.weather.app;

import org.junit.Test;

import sdt.toy.weather.YahooWeatherService;
import sdt.toy.weather.data.Channel;
import sdt.toy.weather.data.unit.DegreeUnit;

import static org.junit.Assert.assertEquals;

public class TestApplication {

	@Test
	public void testWeatherDetails() throws Exception {
		
		String expected = "Kolkata|22.54994,88.37158,9.730396270751953";
		YahooWeatherService service = new YahooWeatherService();
		
		String location = service.getWoeid("kolkata");
		Channel channel = service.getForecast(location, DegreeUnit.CELSIUS);
		String actual = Application.getlocLatLong(service, channel);
		assertEquals(actual, expected);
	}

}
