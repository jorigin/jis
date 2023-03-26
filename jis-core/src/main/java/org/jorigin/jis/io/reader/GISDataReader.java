package org.jorigin.jis.io.reader;

import org.geotools.renderer.style.Style;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * 
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.1.0
 *
 */
public interface GISDataReader {
  
  /**
   * Get the name of the reader.
   * @return the name of the reader.
   */
  public String getName();
  
  /**
   * Get if the input wrapped by the given <code>object</code> can be read.
   * @param object a wrapper to the input.
   * @return <code>true</code> if the input can be read and <code>false</code> otherwise.
   */
  public boolean canRead(Object object);
  
  /**
   * Get if this reader will force the coordinate reference system attached to the input.
   * @return <code>true</code> if this reader will force the coordinate reference system and <code>false</code> otherwise.
   * @see #setCRS(CoordinateReferenceSystem)
   */
  public boolean isForceCRS();
    
  /**
   * Get the {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} attached to this reader.
   * @return the {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} attached to this reader.
   * @see #isForceCRS()
   * @see #setCRS(CoordinateReferenceSystem)
   */
  public CoordinateReferenceSystem getCRS();

  /**
   * Set the {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} attached to this reader.
   * @param crs the {@link org.opengis.referencing.crs.CoordinateReferenceSystem coordinate reference system} attached to this reader.
   * @see #getCRS()
   * @see #isForceCRS()
   */
  public void setCRS(CoordinateReferenceSystem crs);
  
  /**
   * Return the {@link org.geotools.renderer.style.Style style} attached to this reader. The style is attached to read input that do not provide own style.
   * @return the {@link org.geotools.renderer.style.Style style} attached to this reader.
   * @see #setStyle(Style)
   */
  public Style getStyle();
  
  /**
   * Set the {@link org.geotools.renderer.style.Style style} attached to this reader. The style is attached to read input that do not provide own style.
   * @param style the {@link org.geotools.renderer.style.Style style} to attach to this reader. The style is attached to read input that do not provide own style.
   * @see #getStyle()
   */
  public void setStyle(Style style);

}
