/**
 * 
 */
package sdt.toy.weather.binding.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import sdt.toy.weather.data.unit.PressureUnit;


public class PressureUnitAdapter extends XmlAdapter<String, PressureUnit> {

	private static final String MB = "mb";
	private static final String IN = "in";
	
	//private Logger logger = LoggerFactory.getLogger(PressureUnitAdapter.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PressureUnit unmarshal(String v) throws Exception {
		if (IN.equalsIgnoreCase(v)) return PressureUnit.IN;
		if (MB.equalsIgnoreCase(v)) return PressureUnit.MB;
	//	logger.warn("Unknown pressure unit \"{}\"", v);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String marshal(PressureUnit v) throws Exception {
		switch (v) {
			case IN: return IN;
			case MB: return MB;
			default: return "";
		}
	}

}
