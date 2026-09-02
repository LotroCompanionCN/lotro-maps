package delta.games.lotro.maps.ui;

import java.awt.Dimension;

import delta.games.lotro.maps.data.GeoBox;
import delta.games.lotro.maps.data.GeoPoint;
import delta.games.lotro.maps.data.GeoReference;
import delta.games.lotro.maps.data.view.BoundedZoomFilter;
import delta.games.lotro.maps.data.view.ZoomFilter;
import delta.games.lotro.maps.ui.constraints.MapBoundsConstraint;

/**
 * Utility methods related to maps UI.
 * @author DAM
 */
public class MapUiUtils
{
  /**
   * Configure a map panel.
   * @param panel Map panel controller.
   * @param globalBBox Geographic bounding box to use.
   * @param maxSize Maximum size of the panel (pixels).
   */
  public static void configureMapPanel(MapPanelController panel, GeoBox globalBBox, Dimension maxSize)
  {
    GeoPoint start=new GeoPoint(globalBBox.getMin().getLongitude(),globalBBox.getMax().getLatitude());
    float geo2Pixel=computeZoom(maxSize,globalBBox);
    GeoReference viewRef=new GeoReference(start,geo2Pixel);
    Dimension fitSize=getFitSize(geo2Pixel,globalBBox);
    panel.setViewSize(fitSize);
    MapCanvas canvas=panel.getCanvas();
    canvas.setViewReference(viewRef);
    configureConstraints(canvas,globalBBox,geo2Pixel);
  }

  /**
   * Configure map constraints.
   * @param canvas Map canvas.
   * @param globalBBox Geographic bounding box.
   * @param geo2Pixel Initial zoom level.
   */
  public static void configureConstraints(MapCanvas canvas, GeoBox globalBBox, float geo2Pixel)
  {
    // Constraints
    MapBoundsConstraint constraints=new MapBoundsConstraint(canvas,globalBBox);
    canvas.setMapConstraint(constraints);
    // Filter
    ZoomFilter zoomFilter=new BoundedZoomFilter(Float.valueOf(geo2Pixel),Float.valueOf(geo2Pixel*16));
    canvas.setZoomFilter(zoomFilter);
  }

  private static Dimension getFitSize(float geo2Pixel, GeoBox geoBounds)
  {
    float deltaLon=geoBounds.getMax().getLongitude()-geoBounds.getMin().getLongitude();
    float deltaLat=geoBounds.getMax().getLatitude()-geoBounds.getMin().getLatitude();
    int width=(int)(deltaLon*geo2Pixel);
    int height=(int)(deltaLat*geo2Pixel);
    return new Dimension(width,height);
  }

  private static float computeZoom(Dimension pixelMaxSize, GeoBox geoBounds)
  {
    float deltaLon=geoBounds.getMax().getLongitude()-geoBounds.getMin().getLongitude();
    float deltaLat=geoBounds.getMax().getLatitude()-geoBounds.getMin().getLatitude();

    float xGeo2Pixel=pixelMaxSize.width/deltaLon;
    float yGeo2Pixel=pixelMaxSize.height/deltaLat;
    return Math.min(xGeo2Pixel,yGeo2Pixel);
  }

  /**
   * Compute the view reference that fits the given geographic bounds into the given pixel size.
   * @param geoBounds Geographic bounds to fit.
   * @param pixelSize Target pixel size.
   * @return A view reference that shows the whole bounds in the given pixel size.
   */
  public static GeoReference computeFitViewReference(GeoBox geoBounds, Dimension pixelSize)
  {
    GeoPoint start=new GeoPoint(geoBounds.getMin().getLongitude(),geoBounds.getMax().getLatitude());
    float geo2Pixel=computeZoom(pixelSize,geoBounds);
    return new GeoReference(start,geo2Pixel);
  }
}
