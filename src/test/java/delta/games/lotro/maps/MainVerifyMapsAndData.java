package delta.games.lotro.maps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import delta.common.ui.ImageUtils;
import delta.games.lotro.maps.data.MapsManager;
import delta.games.lotro.maps.data.basemaps.GeoreferencedBasemap;
import delta.games.lotro.maps.data.basemaps.GeoreferencedBasemapsManager;

public class MainVerifyMapsAndData
{
  public static void main(String[] args)
  {
    System.out.println("=== Verifying Maps and Image Loading ===");

    File rootDir = new File("M:/CODE/LotroCompanion/lotro-companion-release/target/app/data/lore/maps");
    MapsManager mapsManager = new MapsManager(rootDir);
    GeoreferencedBasemapsManager basemapsManager = mapsManager.getBasemapsManager();

    List<GeoreferencedBasemap> basemaps = basemapsManager.getBasemaps();
    System.out.println("Total basemaps registered: " + basemaps.size());
    if (basemaps.isEmpty())
    {
      throw new RuntimeException("No basemaps loaded from " + rootDir);
    }

    int jpgCount = 0;
    int pngCount = 0;
    int imageLoadSuccess = 0;

    int missingCount = 0;

    for (int i = 0; i < basemaps.size(); i++)
    {
      GeoreferencedBasemap basemap = basemaps.get(i);
      File imgFile = basemap.getImageFile();
      if (imgFile == null || !imgFile.exists())
      {
        missingCount++;
        continue;
      }
      if (imgFile.getName().endsWith(".jpg"))
      {
        jpgCount++;
      }
      else if (imgFile.getName().endsWith(".png"))
      {
        pngCount++;
      }

      BufferedImage img = ImageUtils.loadImage(imgFile);
      if (img == null)
      {
        throw new RuntimeException("Image failed to decode for basemap ID=" + basemap.getIdentifier() + ": " + imgFile);
      }
      imageLoadSuccess++;
    }

    System.out.println("Successfully verified all " + imageLoadSuccess + " basemap images!");
    System.out.println("  - JPEG images: " + jpgCount);
    System.out.println("  - PNG images: " + pngCount);
    System.out.println("  - Missing images (upstream): " + missingCount);

    System.out.println("=== Maps Verification Completed Successfully! ===");
  }
}
