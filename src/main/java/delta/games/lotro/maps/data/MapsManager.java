package delta.games.lotro.maps.data;

import java.io.File;

import delta.common.utils.i18n.SingleLocaleLabelsManager;
import delta.games.lotro.maps.data.basemaps.GeoreferencedBasemapsManager;
import delta.games.lotro.maps.data.categories.CategoriesManager;
import delta.games.lotro.maps.data.links.LinksManager;
import delta.games.lotro.maps.data.markers.GlobalMarkersManager;
import delta.games.lotro.maps.data.markers.MarkersFinder;
import delta.games.lotro.maps.data.markers.index.MarkersIndexesManager;
import delta.games.lotro.maps.utils.i18n.I18nFacade;

/**
 * Maps manager.
 * @author DAM
 */
public class MapsManager
{
  private File _rootDir;
  private File _categoriesDir;
  private File _indexesDir;
  private File _mapsDirs;
  private File _markersDir;
  private File _linksFile;
   // I18n
  private I18nFacade _i18n;
  // Basemaps
  private GeoreferencedBasemapsManager _basemapsManager;
  // Categories
  private CategoriesManager _categoriesManager;
  // Markers
  private GlobalMarkersManager _markersManager;
  private MarkersFinder _markersFinder;
  // Links
  private LinksManager _linksManager;

  /**
   * Constructor.
   * @param rootDir Root directory for maps data.
   */
  public MapsManager(File rootDir)
  {
    this(rootDir,"en");
  }

  /**
   * Constructor.
   * @param rootDir Root directory for maps data.
   * @param weak Use weak map for markers managers (default) or not.
   */
  public MapsManager(File rootDir, boolean weak)
  {
    this(rootDir,"en",weak);
  }

  /**
   * Constructor.
   * @param rootDir Root directory for maps data.
   * @param localeKey Locale key.
   */
  public MapsManager(File rootDir, String localeKey)
  {
    this(rootDir,localeKey,true);
  }

  /**
   * Constructor.
   * @param rootDir Root directory for maps data.
   * @param localeKey Locale key.
   * @param weak Use weak map for markers managers (default) or not.
   */
  public MapsManager(File rootDir, String localeKey, boolean weak)
  {
    _rootDir=rootDir;
    // I18n
    File labelsDir=getLabelsDir();
    _i18n=new I18nFacade(labelsDir,localeKey);
    // Basemaps
    _mapsDirs=new File(_rootDir,"maps");
    _basemapsManager=new GeoreferencedBasemapsManager(_mapsDirs);
    SingleLocaleLabelsManager baseMapsLabelsMgr=_i18n.getLabelsMgr("basemaps");
    _basemapsManager.load(baseMapsLabelsMgr);
    // Categories
    _categoriesDir=new File(_rootDir,"categories");
    _categoriesManager=new CategoriesManager(_categoriesDir);
    // Markers
    _markersDir=new File(_rootDir,"markers");
    SingleLocaleLabelsManager markersLabelsMgr=_i18n.getLabelsMgr("markers");
    _markersManager=new GlobalMarkersManager(_markersDir,markersLabelsMgr,weak);
    _indexesDir=new File(_rootDir,"indexes");
    MarkersIndexesManager indexsMgr=new MarkersIndexesManager(_indexesDir);
    _markersFinder=new MarkersFinder(indexsMgr,_markersManager);
    // Links
    _linksFile=new File(rootDir,"links.xml");
    if (!_linksFile.exists())
    {
      File gzFile=new File(rootDir,"links.xml.gz");
      if (gzFile.exists())
      {
        _linksFile=gzFile;
      }
    }
    _linksManager=new LinksManager(_linksFile);
  }

  /**
   * Get the root directory.
   * @return the root directory.
   */
  public File getRootDir()
  {
    return _rootDir;
  }

  /**
   * Get the directory for categories.
   * @return A directory.
   */
  public File getCategoriesDir()
  {
    return _categoriesDir;
  }

  /**
   * Get the directory for indexes.
   * @return A directory.
   */
  public File getIndexesDir()
  {
    return _indexesDir;
  }

  /**
   * Get the directory for maps.
   * @return A directory.
   */
  public File getMapsDir()
  {
    return _mapsDirs;
  }

  /**
   * Get the directory for markers.
   * @return A directory.
   */
  public File getMarkersDir()
  {
    return _markersDir;
  }

  /**
   * Get the file for map links.
   * @return A file.
   */
  public File getLinksFile()
  {
    return _linksFile;
  }

  /**
   * Get the labels directory.
   * @return the labels directory.
   */
  public File getLabelsDir()
  {
    return new File(_rootDir,"labels");
  }

  /**
   * Get the basemaps manager.
   * @return the basemaps manager.
   */
  public GeoreferencedBasemapsManager getBasemapsManager()
  {
    return _basemapsManager;
  }

  /**
   * Get the categories manager.
   * @return the categories manager.
   */
  public CategoriesManager getCategories()
  {
    return _categoriesManager;
  }

  /**
   * Get the markers manager.
   * @return the markers manager.
   */
  public GlobalMarkersManager getMarkersManager()
  {
    return _markersManager;
  }

  /**
   * Get the markers finder.
   * @return the markers finder.
   */
  public MarkersFinder getMarkersFinder()
  {
    return _markersFinder;
  }

  /**
   * Get the links manager.
   * @return the links manager.
   */
  public LinksManager getLinksManager()
  {
    return _linksManager;
  }
}
