package delta.games.lotro.maps.data.markers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import delta.common.utils.i18n.SingleLocaleLabelsManager;
import delta.games.lotro.maps.data.Marker;

/**
 * Facade to access to all markers.
 * @author DAM
 */
public class GlobalMarkersManager
{
  private static final int BLOCK_SIZE=16;

  private Map<String,BlockMarkersManager> _cache;
  private File _rootDir;
  private SingleLocaleLabelsManager _labelsMgr;

  /**
   * Constructor.
   * @param rootDir Root directory.
   * @param labelsMgr Labels manager.
   */
  public GlobalMarkersManager(File rootDir, SingleLocaleLabelsManager labelsMgr)
  {
    this(rootDir,labelsMgr,true);
  }

  /**
   * Constructor.
   * @param rootDir Root directory.
   * @param labelsMgr Labels manager.
   * @param weak Use weak map for markers managers (default) or not.
   */
  public GlobalMarkersManager(File rootDir, SingleLocaleLabelsManager labelsMgr, boolean weak)
  {
    _rootDir=rootDir;
    if (weak)
    {
      _cache=new WeakHashMap<String,BlockMarkersManager>();
    }
    else
    {
      _cache=new HashMap<String,BlockMarkersManager>();
    }
    _labelsMgr=labelsMgr;
  }

  /**
   * Get the landblock manager for the given landblock.
   * @param region Region.
   * @param landblockX Landblock X.
   * @param landblockY Landblock Y.
   * @return A landblock manager or <code>null</code> if not found.
   */
  public LandblockMarkersManager getLandblockMarkersManager(int region, int landblockX, int landblockY)
  {
    BlockMarkersManager blockManager=getBlockManager(region,landblockX/BLOCK_SIZE,landblockY/BLOCK_SIZE);
    return blockManager.getLandblockMarkersManager(landblockX%BLOCK_SIZE,landblockY%BLOCK_SIZE);
  }

  /**
   * Get all the landblock markers managers.
   * @return a list of these.
   */
  public List<LandblockMarkersManager> getAllManagers()
  {
    List<LandblockMarkersManager> ret=new ArrayList<LandblockMarkersManager>();
    for(BlockMarkersManager blockManager : _cache.values())
    {
      ret.addAll(blockManager.getManagers());
    }
    return ret;
  }

  /**
   * Get the block manager for a given zone.
   * @param region Region.
   * @param bigXBlock X big block coordinate.
   * @param bigYBlock Y big block coordinate.
   * @return A markers manager.
   */
  public BlockMarkersManager getBlockManager(int region, int bigXBlock, int bigYBlock)
  {
    String key=getKey(region,bigXBlock,bigYBlock);
    BlockMarkersManager blockManager=_cache.get(key);
    if (blockManager==null)
    {
      File blockFile=getBlockFile(region,bigXBlock,bigYBlock);
      int firstId=getFirstId(region,bigXBlock,bigYBlock);
      blockManager=new BlockMarkersManager(blockFile,firstId);
      blockManager.load(_labelsMgr);
      _cache.put(key,blockManager);
    }
    return blockManager;
  }

  /**
   * Get a marker using its identifier.
   * @param markerId Marker identifier.
   * @return A marker or <code>null</code> if not found.
   */
  public Marker getMarkerById(int markerId)
  {
    BlockMarkersManager blockManager=getBlockForMarker(markerId);
    return blockManager.getMarkerById(markerId);
  }

  /**
   * Register a marker.
   * @param marker Marker to add.
   * @param region Region code.
   * @param landblockX Landblock X.
   * @param landblockY Landblock Y.
   */
  public void registerMarker(Marker marker, int region, int landblockX, int landblockY)
  {
    BlockMarkersManager blockManager=getBlockManager(region,landblockX/BLOCK_SIZE,landblockY/BLOCK_SIZE);
    blockManager.allocateMarker(marker,landblockX%BLOCK_SIZE,landblockY%BLOCK_SIZE);
  }

  private static int getFirstId(int region, int bigXBlock, int bigYBlock)
  {
    int id=region<<28;
    id+=(bigXBlock<<24);
    id+=(bigYBlock<<20);
    return id;
  }

  private BlockMarkersManager getBlockForMarker(int markerId)
  {
    int region=((markerId&0xF0000000)>>28)&0xF;
    int bigXBlock=(markerId&0xF000000)>>24;
    int bigYBlock=(markerId&0xF00000)>>20;
    BlockMarkersManager blockManager=getBlockManager(region,bigXBlock,bigYBlock);
    return blockManager;
  }

  /**
   * Write the markers files.
   */
  public void write()
  {
    for(BlockMarkersManager blockManager : _cache.values())
    {
      blockManager.write();
    }
  }

  private File getBlockFile(int region, int xBlock, int yBlock)
  {
    String fileName="markers-"+getKey(region,xBlock,yBlock)+".xml";
    File xmlFile=new File(_rootDir,fileName);
    if (!xmlFile.exists())
    {
      File gzFile=new File(_rootDir,fileName+".gz");
      if (gzFile.exists())
      {
        return gzFile;
      }
    }
    return xmlFile;
  }

  private String getKey(int region, int xBlock, int yBlock)
  {
    return region+"-"+xBlock+"-"+yBlock;
  }
}
