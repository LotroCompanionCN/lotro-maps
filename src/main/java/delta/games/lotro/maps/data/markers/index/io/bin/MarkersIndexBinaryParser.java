package delta.games.lotro.maps.data.markers.index.io.bin;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.games.lotro.maps.data.markers.index.MarkersIndex;

/**
 * Parser for the markers indexes stored in a binary format (plain or gzipped).
 * @author DAM
 */
public class MarkersIndexBinaryParser
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MarkersIndexBinaryParser.class);

  /**
   * Parse the binary index file.
   * @param source Source file.
   * @param key Index key.
   * @return Parsed index or <code>null</code>.
   */
  public MarkersIndex parse(File source, int key)
  {
    HashSet<Integer> ids=new HashSet<Integer>();
    try
    {
      InputStream is=new BufferedInputStream(new FileInputStream(source));
      if (source.getName().endsWith(".gz"))
      {
        is=new GZIPInputStream(is);
      }
      DataInputStream dis=new DataInputStream(is);
      try
      {
        while (dis.available()>0 || is instanceof BufferedInputStream)
        {
          int id=dis.readInt();
          ids.add(Integer.valueOf(id));
        }
      }
      catch (java.io.EOFException eof)
      {
        // End of stream reached
      }
      finally
      {
        dis.close();
      }
      return new MarkersIndex(key,ids);
    }
    catch(Exception e)
    {
      LOGGER.error("Error when loading markers index file " + source, e);
    }
    return null;
  }
}
