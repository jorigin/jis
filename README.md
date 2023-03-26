# JIS
A Java based Geographic Information System library. This library provides utilities based on Geotools GIS library.

## Integration
JIS can be used as a Maven dependency or as a standalone library.

### Maven
JIS is available at [Maven Central](https://search.maven.org/search?q=org.jorigin.jis). 

To import the library, add the following parts to the maven project:
```xml

<!-- You can update the properties section with Jeometry version -->
<properties>
  <jis.version>1.0.0</jis.version> 
</properties>

<!-- The Jeometry API that contains all interfaces -->
<dependency>
  <groupId>org.jorigin</groupId>
  <artifactId>jis-core</artifactId>
  <version>${jeometry.version}</version>
</dependency>

<!-- (Optional) The JIS module that contains extensions for Swing -->
<dependency>
  <groupId>org.jorigin</groupId>
  <artifactId>jeometry-swing</artifactId>
  <version>${jeometry.version}</version>
</dependency>

<!-- (Optional) The JIS module that contains extensions for JavaFX -->
<dependency>
  <groupId>org.jorigin</groupId>
  <artifactId>jeometry-jfx</artifactId>
  <version>${jeometry.version}</version>
</dependency>
```

### Standalone
JIS can be used as standalone library by integrating the jars provided by a [release](https://github.com/jorigin/jis/releases) to the classpath. 
Be carrefull to also integrate the [JCommon](https://github.com/jorigin/jcommon) dependency.

## Usage
For a quick overwiew ot the library, please refer to the [Getting Started](https://github.com/jorigin/jis/wiki/Getting-Started).

For more information, tutorials and advanced uses, please check the [Wiki](https://github.com/jorigin/jis/wiki).

## Changes:

see [changelog](CHANGELOG.md) for details.
