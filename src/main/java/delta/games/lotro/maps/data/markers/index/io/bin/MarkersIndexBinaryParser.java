package delta.games.lotro.maps.data.markers.index.io.bin;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.io.FileIO;
import delta.games.lotro.maps.data.markers.index.MarkersIndex;

/**
 * Parser for the markers indexes stored in a binary format.
 * @author DAM
 */
public class MarkersIndexBinaryParser
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MarkersIndexBinaryParser.class);

  /**
   * Parse the XML file.
   * @param source Source file.
   * @param key Index key.
   * @return Parsed index or <code>null</code>.
   */
  public MarkersIndex parse(File source, int key)
  {
    HashSet<Integer> ids=new HashSet<Integer>();
    byte[] buffer=FileIO.readFile(source);
    long length=source.length();
    long nbMarkers=length/4;
    DataInputStream dis=new DataInputStream(new ByteArrayInputStream(buffer));
    try
    {
      for(int i=0;i<nbMarkers;i++)
      {
        int id=dis.readInt();
        ids.add(Integer.valueOf(id));
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
