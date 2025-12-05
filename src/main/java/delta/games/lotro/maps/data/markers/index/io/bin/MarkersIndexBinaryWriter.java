package delta.games.lotro.maps.data.markers.index.io.bin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import delta.common.utils.io.FileIO;
import delta.games.lotro.maps.data.markers.index.MarkersIndex;

/**
 * Writes a markers index to a file in binary format.
 * @author DAM
 */
public class MarkersIndexBinaryWriter
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MarkersIndexBinaryWriter.class);

  /**
   * Write a markers index to a file.
   * @param outFile Output file.
   * @param index index to write.
   * @return <code>true</code> if it succeeds, <code>false</code> otherwise.
   */
  public boolean write(File outFile, final MarkersIndex index)
  {
    try
    {
      int[] markerIds=index.getMarkers().getValues();
      int size=markerIds.length*4;
      ByteArrayOutputStream baos=new ByteArrayOutputStream(size);
      DataOutputStream dos=new DataOutputStream(baos);
      for(int markerId : markerIds)
      {
        dos.writeInt(markerId);
      }
      return FileIO.writeFile(outFile,baos.toByteArray());
    }
    catch(Exception e)
    {
      LOGGER.error("Could not write markers index "+outFile,e);
    }
    return false;
  }
}
