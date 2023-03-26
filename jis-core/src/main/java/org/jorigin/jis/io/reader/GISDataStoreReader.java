package org.jorigin.jis.io.reader;

import org.geotools.data.DataStore;

/**
 * Interface that specifies the read of an {@link org.geotools.data.DataStore DataStore} 
 * from an input {@link java.lang.Object Object}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.1.0
 */
public interface GISDataStoreReader {

  /**
   * Read a {@link org.geotools.data.DataStore DataStore} from the given <code>input</code>. 
   * The use of {@link java.lang.Object Object} enables to handle various inputs 
   * ({@link java.io.File File}, {@link java.io.InputStream InputStream}, {@link java.lang.String path}, ...)
   * @param input the input to read.
   * @return the data store related to the input.
   */
  public DataStore readDataStore(Object input);
}
