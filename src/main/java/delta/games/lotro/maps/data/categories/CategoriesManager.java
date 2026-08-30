package delta.games.lotro.maps.data.categories;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import delta.common.utils.text.EncodingNames;
import delta.games.lotro.maps.data.categories.io.xml.CategoriesXMLParser;
import delta.games.lotro.maps.data.categories.io.xml.CategoriesXMLWriter;

/**
 * Marker categories manager.
 * @author DAM
 */
public class CategoriesManager
{
  private File _categoriesDir;
  private HashMap<Integer,Category> _mapByCode;

  /**
   * Constructor.
   * @param categoriesDir Categories directory.
   */
  public CategoriesManager(File categoriesDir)
  {
    _categoriesDir=categoriesDir;
    _mapByCode=new HashMap<Integer,Category>();
    load();
  }

  /**
   * Get the root directory for categories data.
   * @return a directory.
   */
  public File getCategoriesDir()
  {
    return _categoriesDir;
  }

  /**
   * (Re-)load categories.
   */
  public void load()
  {
    _mapByCode.clear();
    File categoriesFile=getCategoriesFile();
    List<Category> categories=CategoriesXMLParser.load(categoriesFile);
    for(Category category : categories)
    {
      addCategory(category);
    }
  }

  /**
   * Get the categories file.
   * @return the categories file.
   */
  public File getCategoriesFile()
  {
    File xmlFile=new File(_categoriesDir,"categories.xml");
    if (!xmlFile.exists())
    {
      File gzFile=new File(_categoriesDir,"categories.xml.gz");
      if (gzFile.exists())
      {
        return gzFile;
      }
    }
    return xmlFile;
  }

  /**
   * Get the icon file for a marker category.
   * @param category Category.
   * @return An icon file.
   */
  public File getIconFile(Category category)
  {
    String iconName=category.getIcon();
    String pathName=iconName+".png";
    return new File(_categoriesDir,pathName);
  }

  /**
   * Get a category using its code.
   * @param code A code.
   * @return A category or <code>null</code> if not found.
   */
  public Category getByCode(int code)
  {
    return _mapByCode.get(Integer.valueOf(code));
  }

  /**
   * Get all the category codes.
   * @return A set of integer codes.
   */
  public Set<Integer> getCodes()
  {
    return new HashSet<Integer>(_mapByCode.keySet());
  }

  /**
   * Get all categories.
   * @return a list of categories, sorted by code.
   */
  public List<Category> getAllSortedByCode()
  {
    List<Category> ret=new ArrayList<Category>();
    List<Integer> codes=new ArrayList<Integer>(_mapByCode.keySet());
    Collections.sort(codes);
    for(Integer code : codes)
    {
      Category category=_mapByCode.get(code);
      ret.add(category);
    }
    return ret;
  }

  /**
   * Add a new category.
   * @param category Category to add.
   */
  public void addCategory(Category category)
  {
    Integer key=Integer.valueOf(category.getCode());
    _mapByCode.put(key,category);
  }

  /**
   * Remove a category.
   * @param code Code of the targeted category.
   */
  public void removeCategory(int code)
  {
    _mapByCode.remove(Integer.valueOf(code));
  }

  /**
   * Save categories.
   */
  public void save()
  {
    CategoriesXMLWriter writer=new CategoriesXMLWriter();
    File to=getCategoriesFile();
    writer.write(to,this,EncodingNames.UTF_8);
  }
}
