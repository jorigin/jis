package org.jorigin.jis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.geotools.referencing.CRS;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class CRSDefaults {

  /**
   * Value representing a North axes latitude.
   */
  public final String LAT_NORTH   = "LAT_NORTH";
  
  /**
   * Value representing a South axed latitude.
   */
  public final String LAT_SOUTH   = "LAT_SOUTH";
  
  /**
   * Value representing an East axed longitude.
   */
  public final String LON_EAST    = "LON_EAST";
  
  /**
   * Value representing a West axed longitude.
   */
  public final String LON_WEST    = "LON_WEST";
  
  /** The VTK representation of the WSG84 coordinates system. */
  public static final String WSG84_WKT = "GEOGCS[\"WGS 84\", "
                                         +  "DATUM[\"WGS_1984\", " 
                                         +     "SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], " 
                                         +     "AUTHORITY[\"EPSG\",\"6326\"]], " 
                                         +  "PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], " 
                                         +  "UNIT[\"degree\", 0.017453292519943295], " 
                                         +  "AXIS[\"Longitude\", EAST], "
                                         +  "AXIS[\"Latitude\", NORTH], "
                                         +  "AUTHORITY[\"EPSG\",\"4326\"]]";

  /** The VTK representation of the UTM31 coordinates system. */
  public static final String UTM31N_WKT = "PROJCS[\"WGS 84 / UTM zone 31N\", "
                                          +  "GEOGCS[\"WGS 84\", " 
                                          +    "DATUM[\"WGS_1984\", " 
                                          +      "SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]], " 
                                          +      "AUTHORITY[\"EPSG\",\"6326\"]], " 
                                          +    "PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]], " 
                                          +    "UNIT[\"degree\", 0.017453292519943295], "
                                          +    "AXIS[\"Longitude\", EAST], "
                                          +    "AXIS[\"Latitude\", NORTH], " 
                                          +    "AUTHORITY[\"EPSG\",\"4326\"]], "
                                          +    "PROJECTION[\"Transverse_Mercator\"], "
                                          +    "PARAMETER[\"central_meridian\", 3.0], " 
                                          +    "PARAMETER[\"latitude_of_origin\", 0.0], "
                                          +    "PARAMETER[\"scale_factor\", 0.9996], "
                                          +    "PARAMETER[\"false_easting\", 500000.0], "
                                          +    "PARAMETER[\"false_northing\", 0.0], "
                                          +    "UNIT[\"m\", 1.0], "
                                          +    "AXIS[\"x\", EAST], "
                                          +    "AXIS[\"y\", NORTH], "
                                          +    "AUTHORITY[\"EPSG\",\"32631\"]]";

  /**
   * Get a list of the available CRS. The elements of the list are triplet defined as <code>{authorityID, code, crsName}</code>
   * @return the list of available CRS.
   */
  public static Vector<String[]> getAvailablesCRSList(){
    Set<String> authorities       = CRS.getSupportedAuthorities(false);
    String authorityID            = null;
    Iterator<String> authIter     = authorities.iterator();
    
    Set<String> codes             = null;
    String code                   = null;
    Iterator<String> codeIter     = null;
    
    CoordinateReferenceSystem crs = null;
    
    ReferenceIdentifier refID     = null;
    
    Vector<String[]> datas        = null;
     
    String crsName                = null;
    
    datas = new Vector<String[]>();
    
    ArrayList<String> ignoredAuth = new ArrayList<String>();
    
    while(authIter.hasNext()){
      authorityID = authIter.next();

      if (!ignoredAuth.contains(authorityID)){
        codes = CRS.getSupportedCodes(authorityID);
        boolean ok  = true;
        
        if (codes != null){
          codeIter = codes.iterator();
          while(codeIter.hasNext() && ok){
            code = codeIter.next();
            
            if (!code.contains(":")){
              code = authorityID+":"+code;
            }
            
            if (! ignoredAuth.contains(code.substring(0, code.lastIndexOf(":")))){
              // On ne decode que si le code CRS est bien un nombre.
              if (code.substring(code.lastIndexOf(":")+1).matches("[0-9]+")){
                try {
                  crs = CRS.decode(code);
                  refID = crs.getName();

                  crsName = crs.getName().toString();
                  if (crsName.startsWith(authorityID+":")){
                    crsName = ""+crsName.substring(refID.toString().indexOf(":")+1);
                  }
                  
                  datas.add(new String[]{authorityID, code, crsName});

                } catch (NoSuchAuthorityCodeException e) {
                  ignoredAuth.add(code.substring(0, code.lastIndexOf(":")));
                  JIS.logger.log(Level.WARNING, "Code: "+code+" Cannot be decoded: "+e.getMessage(), e);
                } catch (FactoryException e) {
                	JIS.logger.log(Level.WARNING, "Code: "+code+" Cannot be decoded: "+e.getMessage(), e);
                }
              } else {
            	  JIS.logger.log(Level.SEVERE, "Invalid CRS code "+code+" (code should be a number.)");
              }
            }
          }
        }
      } else {
    	  JIS.logger.log(Level.SEVERE, "Ignoring authority "+authorityID);
      }
    }
    
    return datas;
  }
  
  /**
   * Get all the available Coordinates Reference Systems within the underlying Geotools library.
   * @return the available Coordinates Reference Systems within the underlying Geotools library
   */
public static ArrayList<CoordinateReferenceSystem> getAvailablesCRS(){
    ArrayList<CoordinateReferenceSystem> list = new ArrayList<CoordinateReferenceSystem>();
    
    Set<String> authorities       = CRS.getSupportedAuthorities(false);
    String authorityID            = null;
    Iterator<String> authIter     = authorities.iterator();
    
    Set<String> codes             = null;
    String code                   = null;
    Iterator<String> codeIter     = null;
    
    CoordinateReferenceSystem crs = null;
    
    ArrayList<String> ignoredAuth = new ArrayList<String>();
    
    while(authIter.hasNext()){
      authorityID = authIter.next();

      if (!ignoredAuth.contains(authorityID)){
        codes = CRS.getSupportedCodes(authorityID);
        boolean ok  = true;
        
        if (codes != null){
          codeIter = codes.iterator();
          while(codeIter.hasNext() && ok){
            code = codeIter.next();
            
            if (!code.contains(":")){
              code = authorityID+":"+code;
            }
            
            if (! ignoredAuth.contains(code.substring(0, code.lastIndexOf(":")))){
              // On ne decode que si le code CRS est bien un nombre.
              //if (code.substring(code.lastIndexOf(":")+1).matches("[0-9]+")){
                try {
                  crs = CRS.decode(code);
                  list.add(crs);
                  JIS.logger.log(Level.INFO, "CRS "+code+" decoded.");
                } catch (NoSuchAuthorityCodeException e) {
                	JIS.logger.log(Level.SEVERE, "Cannot decode "+code+": authority "+authorityID+" was not found.");
                	JIS.logger.log(Level.FINE, e.getMessage(), e);
                  
                  //ignoredAuth.add(code.substring(0, code.lastIndexOf(":")));

                } catch (FactoryException e) {
                	JIS.logger.log(Level.WARNING, "Cannot decode "+code+": "+e.getMessage());
                	JIS.logger.log(Level.FINE, e.getMessage(), e);
                }
              //} else {
            	//  JIS.logger.log(Level.SEVERE, "Cannot decode "+code+", ");
              //}
            }
          }
        }
      } else {
    	  JIS.logger.log(Level.SEVERE, "Ignoring authority "+authorityID);
      }
    }
    
    if (list.size() < 1){
      list = null;
    }
    
    return list;
  }
  
  /**
   * Check if the given WSG84 coordinate is bounded by the given CRS.
   * @param crs the CRS to check
   * @param latAngle the latitude angle in WSG84.
   * @param lonAngle the longitude angle in WSG84
   * @return true if the coordinates are bounded by the CRS, false otherwise.
   */
  public static boolean isBounded(CoordinateReferenceSystem crs, double latAngle, double lonAngle){
    boolean bounded = true;
    
    GeographicBoundingBox gbb = CRS.getGeographicBoundingBox(crs);
    
    if (gbb != null){
      if (gbb.getEastBoundLongitude() < gbb.getWestBoundLongitude()){
        bounded &= (lonAngle >= gbb.getEastBoundLongitude()) && (lonAngle <= gbb.getWestBoundLongitude());
      } else {
        bounded &= (lonAngle >= gbb.getWestBoundLongitude()) && (lonAngle <= gbb.getEastBoundLongitude());
      }
      
      if (gbb.getNorthBoundLatitude() < gbb.getSouthBoundLatitude()){
        bounded &= (latAngle >= gbb.getNorthBoundLatitude()) && (latAngle <= gbb.getSouthBoundLatitude());
      } else {
        bounded &= (latAngle >= gbb.getSouthBoundLatitude()) && (latAngle <= gbb.getNorthBoundLatitude());
      }
      
    } else {
      bounded = false;
    }  
    return bounded;
  }
  
  /**
   * Get the list of CRS that bounds the position given by the <code>latAngle</code> and <code>lonAngle</code>. 
   * The given CRS list is sorted as the first element has its center the nearest of the position. The last element has its center the most far.
   * @param crsCollection the CRS to check.
   * @param latAngle the latitude angle representing the position.
   * @param lonAngle the longitude angle representing the position.
   * @return the list of the CRS that bounds the position.
   */
  public static ArrayList<CoordinateReferenceSystem> getBoundingsCRS(Collection<CoordinateReferenceSystem> crsCollection, double latAngle, double lonAngle){
    ArrayList<CoordinateReferenceSystem> boundingsCRS = new ArrayList<CoordinateReferenceSystem>();
    CoordinateReferenceSystem crs                     = null;
    Iterator<CoordinateReferenceSystem> crsIter       = null;
    
    crsIter = crsCollection.iterator();
    while(crsIter.hasNext()){
      crs = crsIter.next();
      
      if (isBounded(crs, latAngle, lonAngle)){
        boundingsCRS.add(crs);
      }
      
    }
 
    // Tri de la collection en fonction de la distance entre les centres des CRS et la position.
    ComparatorPos comp = new ComparatorPos(lonAngle, latAngle);
    Collections.sort(boundingsCRS, comp);
    
    return boundingsCRS;
    
  }
  
  /**
   * Compute the distance between the center of the given Coordinates Reference System (CRS) and the coordinates specified by the given longitude and latitude.
   * @param crs the Coordinates Reference System (CRS)
   * @param lon the longitude of the coordinate
   * @param lat the latitude of the coordinate
   * @return the distance between the center of the given Coordinates Reference System (CRS) and the specified coordinates
   */
public static double distanceFromCRSCenter(CoordinateReferenceSystem crs, double lon, double lat){
    double distance = 0.0d;
    
    double lonCenter = 0.0d;
    double latCenter = 0.0d;
    
    GeographicBoundingBox gbb = CRS.getGeographicBoundingBox(crs);
    
    if (gbb == null){
      return Double.NaN;
    }
    
    if (gbb.getEastBoundLongitude() < gbb.getWestBoundLongitude()){
      lonCenter = (gbb.getWestBoundLongitude() + gbb.getEastBoundLongitude()) / 2;
    } else {
      lonCenter = (gbb.getEastBoundLongitude() + gbb.getWestBoundLongitude()) / 2;
    }
    
    if (gbb.getNorthBoundLatitude() < gbb.getSouthBoundLatitude()){
      latCenter = (gbb.getSouthBoundLatitude() + gbb.getNorthBoundLatitude()) /2;
    } else {
      latCenter = (gbb.getNorthBoundLatitude() + gbb.getSouthBoundLatitude()) /2;
    }
    
    distance = Math.sqrt(Math.pow(lonCenter-lon, 2)+Math.pow(latCenter-lat, 2)); 
    return distance;
  }
  
  /**
   * Convert a longitude angle to a formatted Degree°Minute'Seconds'' {@link java.lang.String String}.
   * the seconds value can be decimal.
   * @param longitudeAngle the angle.
   * @return the formatted Degree°Minute'Seconds {@link java.lang.String String} for the given longitude angle.
   * @see #toDegreeMinuteSecondsLat(double)
   */
  public static String toDegreeMinuteSecondsLon(double longitudeAngle){
    
    String degree    = "0";
    String minute    = "0";
    String seconds   = "0";
    
    String direction = null;
    
    double angle     = 0.0d;
    
    if (longitudeAngle < 0){
      direction = "W";
    } else {
      direction = "E";
    }
    
    angle   = Math.abs(longitudeAngle);
    degree  = ""+((int)(angle));
    
    angle   = (angle % 10)*60;
    minute  = ""+((int)(angle / 10));
    
    angle   = (angle % 10)*60;
    seconds = ""+(angle / 10.0d);
    
    return degree+"° "+minute+"' "+seconds+"''"+direction;
  }
  
  /**
   * Convert a latitude angle to a formatted Degree°Minute'Seconds'' {@link java.lang.String String}.
   * the seconds value can be decimal.
   * @param latitudeAngle
   * @return the formatted Degree°Minute'Seconds {@link java.lang.String String} for the given latitude angle.
   * @see #toDegreeMinuteSecondsLon(double)
   */
  public static String toDegreeMinuteSecondsLat(double latitudeAngle){
    String degree    = "0";
    String minute    = "0";
    String seconds   = "0";
    
    String direction = null;
    
    double angle     = 0.0d;
    
    if (latitudeAngle < 0){
      direction = "N";
    } else {
      direction = "S";
    }
    
    angle   = Math.abs(latitudeAngle);
    degree  = ""+((int)(angle));
    
    angle   = (angle % 10)*60;
    minute  = ""+((int)(angle / 10));
    
    angle   = (angle % 10)*60;
    seconds = ""+(angle / 10.0d);
    
    return degree+"° "+minute+"' "+seconds+"''"+direction;
  }
}

//Tri de la liste selon l'eloignement croissant du centre du CRS avec la position.
class ComparatorPos implements Comparator<CoordinateReferenceSystem>{

  double lon = 0.0d;
  double lat = 0.0d;
  
  GeographicBoundingBox gbb1 = null;
  GeographicBoundingBox gbb2 = null;
  
  public ComparatorPos(double lon, double lat){
    setLonLat(lon, lat);
  }
  
  public void setLonLat(double lon, double lat){
    this.lon = lon;
    this.lat = lat;
  }
  
  public double distanceFromCRSCenter(GeographicBoundingBox gbb, double lon, double lat){
    double distance = 0.0d;
    
    double lonCenter = 0.0d;
    double latCenter = 0.0d;
    
    
    if (gbb.getEastBoundLongitude() < gbb.getWestBoundLongitude()){
      lonCenter = (gbb.getWestBoundLongitude() + gbb.getEastBoundLongitude()) / 2;
    } else {
      lonCenter = (gbb.getEastBoundLongitude() + gbb.getWestBoundLongitude()) / 2;
    }
    
    if (gbb.getNorthBoundLatitude() < gbb.getSouthBoundLatitude()){
      latCenter = (gbb.getSouthBoundLatitude() + gbb.getNorthBoundLatitude()) /2;
    } else {
      latCenter = (gbb.getNorthBoundLatitude() + gbb.getSouthBoundLatitude()) /2;
    }
    
    distance = Math.sqrt(Math.pow(lonCenter-lon, 2)+Math.pow(latCenter-lat, 2)); 
    
    return distance;
  }
  
  @Override
  public int compare(CoordinateReferenceSystem o1, CoordinateReferenceSystem o2) {
    
    this.gbb1 = CRS.getGeographicBoundingBox(o1);
    this.gbb2 = CRS.getGeographicBoundingBox(o2);
    
    if (this.gbb1 == null){
      if (this.gbb2 == null){
        return 0;
      } else {
        return 1;
      }
    } else {
      if (this.gbb2 == null){
        return -1;
      } else {
        // Calcul des distances de la position au "centre" du CRS
        if (distanceFromCRSCenter(this.gbb1, this.lon, this.lat) == distanceFromCRSCenter(this.gbb2, this.lon, this.lat)){
          return 0;
        } else if (distanceFromCRSCenter(this.gbb1, this.lon, this.lat) < distanceFromCRSCenter(this.gbb2, this.lon, this.lat)){
          return -1;
        } else {
          return 1;
        }
      }
    }
    
  }};
