package org.jorigin.jis.io.reader;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;

/**
 * Interface that specifies the read of an {@link org.geotools.coverage.grid.io.AbstractGridCoverage2DReader grid coverage} 
 * from an input {@link java.lang.Object Object}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.1.0
 */
public interface GISGridCoverageReader {

  /**
   * Read a {@link org.geotools.coverage.grid.io.AbstractGridCoverage2DReader grid coverage} from the given <code>input</code>. 
   * The use of {@link java.lang.Object Object} enables to handle various inputs 
   * ({@link java.io.File File}, {@link java.io.InputStream InputStream}, {@link java.lang.String path}, ...)
   * @param input the source of the read
   * @return a ready to use grid coverage
   */
  public AbstractGridCoverage2DReader readGridCoverage(Object input);
}
