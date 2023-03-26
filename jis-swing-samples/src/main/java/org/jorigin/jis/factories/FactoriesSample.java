package org.jorigin.jis.factories;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.GeometryFactory;

public class FactoriesSample {

	
	public static void main(String[] args) {
		GeometryFactory gf;

		Hints hints = new Hints( Hints.CRS, DefaultGeographicCRS.WGS84 );
		      
		try {
		  gf     = JTSFactoryFinder.getGeometryFactory(hints);
		  
		  
		  
		} catch (Exception e) {
		  System.out.println("Cannot determine geometry factory: "+e.getMessage());
		}
	}	
}
