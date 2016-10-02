/**
 * 
 */
package sdt.toy.weather.binding.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import sdt.toy.weather.data.unit.DistanceUnit;


public class DistanceUnitAdapter extends XmlAdapter<String, DistanceUnit> {

	private static final String MI = "mi";
	private static final String KM = "km";
//	private Logger logger = LoggerFactory.getLogger(DistanceUnitAdapter.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DistanceUnit unmarshal(String v) throws Exception {
		if (MI.equalsIgnoreCase(v)) return DistanceUnit.MI;
		if (KM.equalsIgnoreCase(v)) return DistanceUnit.KM;
		//logger.warn("Unknown distance unit \"{}\"", v);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(DistanceUnit v) throws Exception {
		switch (v) {
			case KM: return KM;
			case MI: return MI;
			default: return "";
		}
	}

}
