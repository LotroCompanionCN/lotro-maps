package delta.games.lotro.maps.data.basemaps;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import delta.common.utils.i18n.SingleLocaleLabelsManager;
import delta.common.utils.id.IdentifiableComparator;
import delta.common.utils.text.EncodingNames;
import delta.games.lotro.maps.data.basemaps.io.xml.GeoreferencedBasemapXMLParser;
import delta.games.lotro.maps.data.basemaps.io.xml.GeoreferencedBasemapXMLWriter;

/**
 * Manager for all georeferenced basemaps.
 * @author DAM
 */
public class GeoreferencedBasemapsManager
{
  private File _rootDir;
  private Map<Integer,GeoreferencedBasemap> _maps;

  /**
   * Constructor.
   * @param rootDir Root directory for basemaps data.
   */
  public GeoreferencedBasemapsManager(File rootDir)
  {
    _rootDir=rootDir;
    _maps=new HashMap<Integer,GeoreferencedBasemap>();
  }

  /**
   * Load basemaps using the given labels.
   * @param labelsMgr Labels manager.
   */
  public void load(SingleLocaleLabelsManager labelsMgr)
  {
    File from=getMapsFile();
    if (from.exists())
    {
      List<GeoreferencedBasemap> basemaps=new GeoreferencedBasemapXMLParser(labelsMgr).parseXML(from);
      for(GeoreferencedBasemap basemap : basemaps)
      {
        addBasemap(basemap);
      }
    }
  }

  /**
   * Write basemaps to the dedicated XML file.
   */
  public void write()
  {
    File to=getMapsFile();
    List<GeoreferencedBasemap> basemaps=getBasemaps();
    GeoreferencedBasemapXMLWriter.writeBasemapsFile(to,basemaps,EncodingNames.UTF_8);
  }

  private File getMapsFile()
  {
    File xmlFile=new File(_rootDir,"maps.xml");
    if (!xmlFile.exists())
    {
      File gzFile=new File(_rootDir,"maps.xml.gz");
      if (gzFile.exists())
      {
        return gzFile;
      }
    }
    return xmlFile;
  }

  /**
   * Add a basemap.
   * @param basemap Basemap to add.
   */
  public void addBasemap(GeoreferencedBasemap basemap)
  {
    File imageFile=getBasemapImageFile(basemap.getIdentifier());
    basemap.setImageFile(imageFile);
    Integer key=Integer.valueOf(basemap.getIdentifier());
    _maps.put(key,basemap);
  }

  /**
   * Get a basemap using its identifier.
   * @param id Basemap identifier.
   * @return A basemap or <code>null</code> if not found.
   */
  public GeoreferencedBasemap getMapById(int id)
  {
    return _maps.get(Integer.valueOf(id));
  }

  /**
   * Get all basemaps.
   * @return a list of basemaps.
   */
  public List<GeoreferencedBasemap> getBasemaps()
  {
    List<GeoreferencedBasemap> ret=new ArrayList<GeoreferencedBasemap>();
    ret.addAll(_maps.values());
    Collections.sort(ret,new IdentifiableComparator<GeoreferencedBasemap>());
    return ret;
  }

  /**
   * Get the file for the image of a basemap.
   * @param basemapId Basemap identifier.
   * @return An image file.
   */
  public File getBasemapImageFile(int basemapId)
  {
    File pngFile=new File(_rootDir,basemapId+".png");
    if (!pngFile.exists())
    {
      File jpgFile=new File(_rootDir,basemapId+".jpg");
      if (jpgFile.exists())
      {
        return jpgFile;
      }
    }
    return pngFile;
  }

  /**
   * Dump the contents of this manager.
   * @param out Output stream.
   */
  public void dump(PrintStream out)
  {
    List<GeoreferencedBasemap> maps=getBasemaps();
    out.println("Basemaps: ("+maps.size()+")");
    for(GeoreferencedBasemap map : maps)
    {
      out.println("* "+map);
    }
  }
}
