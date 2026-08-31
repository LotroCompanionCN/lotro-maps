package delta.games.lotro.maps.data.markers.index;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import delta.games.lotro.maps.data.markers.index.io.bin.MarkersIndexBinaryParser;
import delta.games.lotro.maps.data.markers.index.io.bin.MarkersIndexBinaryWriter;

/**
 * Manager for all marker indexes.
 * @author DAM
 */
public class MarkersIndexesManager
{
  private File _indexesDir;
  private Map<Integer,MarkersIndex> _didIndexes;
  private Map<Integer,MarkersIndex> _contentLayerIndexes;

  /**
   * Constructor.
   * @param indexesDir Root directory for indexes.
   */
  public MarkersIndexesManager(File indexesDir)
  {
    _indexesDir=indexesDir;
    _didIndexes=new HashMap<Integer,MarkersIndex>();
    _contentLayerIndexes=new HashMap<Integer,MarkersIndex>();
  }

  /**
   * Get an index for a DID.
   * @param did DID to use.
   * @return An index or <code>null</code>.
   */
  public MarkersIndex getDidIndex(int did)
  {
    Integer key=Integer.valueOf(did);
    MarkersIndex index=_didIndexes.get(key);
    if (index==null)
    {
      File from=getFileForDidIndex(did);
      if (from.exists())
      {
        index=loadBinaryIndex(from,did);
      }
      else
      {
        index=new MarkersIndex(did,new HashSet<Integer>());
      }
      _didIndexes.put(key,index);
    }
    return index;
  }

  /**
   * Get an index for a content layer.
   * @param contentLayerId Content layer to use.
   * @return An index or <code>null</code>.
   */
  public MarkersIndex getContentLayerIndex(int contentLayerId)
  {
    Integer key=Integer.valueOf(contentLayerId);
    MarkersIndex index=_contentLayerIndexes.get(key);
    if (index==null)
    {
      File from=getFileForContentLayerIndex(contentLayerId);
      if (from.exists())
      {
        index=loadBinaryIndex(from,contentLayerId);
      }
      else
      {
        index=new MarkersIndex(contentLayerId,new HashSet<Integer>());
      }
      _contentLayerIndexes.put(key,index);
    }
    return index;
  }

  /**
   * Set a DID index.
   * @param index Index to set.
   */
  public void setDidIndex(MarkersIndex index)
  {
    _didIndexes.put(Integer.valueOf(index.getKey()),index);
  }

  /**
   * Set a content layer index.
   * @param index Index to set.
   */
  public void setContentLayerIndex(MarkersIndex index)
  {
    _contentLayerIndexes.put(Integer.valueOf(index.getKey()),index);
  }

  /**
   * Write the managed indexes.
   */
  public void writeIndexes()
  {
    MarkersIndexBinaryWriter binaryWriter=new MarkersIndexBinaryWriter();
    // DID indexes
    for(Map.Entry<Integer,MarkersIndex> entry : _didIndexes.entrySet())
    {
      int did=entry.getKey().intValue();
      MarkersIndex index=entry.getValue();
      File to=getFileForDidIndex(did);
      binaryWriter.write(to,index);
    }
    // Content layers indexes
    for(Map.Entry<Integer,MarkersIndex> entry : _contentLayerIndexes.entrySet())
    {
      int contentLayerId=entry.getKey().intValue();
      MarkersIndex index=entry.getValue();
      File to=getFileForContentLayerIndex(contentLayerId);
      binaryWriter.write(to,index);
    }
  }

  private MarkersIndex loadBinaryIndex(File from, int key)
  {
    MarkersIndexBinaryParser parser=new MarkersIndexBinaryParser();
    MarkersIndex index=parser.parse(from,key);
    return index;
  }

  private File getFileForDidIndex(int did)
  {
    File didIndexsDir=new File(_indexesDir,"did");
    File binFile=new File(didIndexsDir,did+".bin");
    if (!binFile.exists())
    {
      File gzFile=new File(didIndexsDir,did+".bin.gz");
      if (gzFile.exists())
      {
        return gzFile;
      }
    }
    return binFile;
  }

  private File getFileForContentLayerIndex(int contentLayerId)
  {
    File layerIndexsDir=new File(_indexesDir,"layers");
    File binFile=new File(layerIndexsDir,contentLayerId+".bin");
    if (!binFile.exists())
    {
      File gzFile=new File(layerIndexsDir,contentLayerId+".bin.gz");
      if (gzFile.exists())
      {
        return gzFile;
      }
    }
    return binFile;
  }
}
