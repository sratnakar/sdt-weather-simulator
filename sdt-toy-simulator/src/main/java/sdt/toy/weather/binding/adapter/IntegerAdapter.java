/**
 *
 */
package sdt.toy.weather.binding.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerAdapter extends XmlAdapter<String, Integer> {

	/**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Integer v) throws Exception {
        return String.valueOf(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer unmarshal(String v) throws Exception {
    	if (v == null || v.isEmpty()) return null;
    	return Integer.parseInt(v);
    }

}
