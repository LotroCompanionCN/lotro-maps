package delta.games.lotro.maps.ui.constraints;

import delta.games.lotro.maps.data.GeoBox;
import delta.games.lotro.maps.data.GeoPoint;
import delta.games.lotro.maps.ui.MapCanvas;

/**
 * Constraints the map to stay within the given geographic bounds.
 * @author DAM
 */
public class MapBoundsConstraint implements MapConstraint
{
  private MapCanvas _canvas;
  private GeoBox _bounds;

  /**
   * Constructor.
   * @param canvas Map canvas.
   * @param bounds Geographic bounds constraint.
   */
  public MapBoundsConstraint(MapCanvas canvas, GeoBox bounds)
  {
    _canvas=canvas;
    _bounds=bounds;
  }

  /**
   * Get the geographic bounds.
   * @return the geographic bounds.
   */
  public GeoBox getBounds()
  {
    return _bounds;
  }

  @Override
  public GeoPoint checkNewStart(GeoPoint newStart, float geo2Pixel)
  {
    // 1) Check top left
    GeoPoint mapStart=new GeoPoint(_bounds.getMin().getLongitude(),_bounds.getMax().getLatitude());
    // Longitude
    float newLongitude=newStart.getLongitude();
    if (newStart.getLongitude()<mapStart.getLongitude())
    {
      newLongitude=mapStart.getLongitude();
    }
    // Latitude
    float newLatitude=newStart.getLatitude();
    if (newStart.getLatitude()>mapStart.getLatitude())
    {
      newLatitude=mapStart.getLatitude();
    }

    // 2) Check bottom right
    int width=_canvas.getWidth();
    float deltaLon=width/geo2Pixel;
    int height=_canvas.getHeight();
    float deltaLat=height/geo2Pixel;
    GeoPoint newEnd=new GeoPoint(newLongitude+deltaLon,newLatitude-deltaLat);
    GeoPoint mapEnd=new GeoPoint(_bounds.getMax().getLongitude(),_bounds.getMin().getLatitude());
    // Longitude
    if (newEnd.getLongitude()>mapEnd.getLongitude())
    {
      newLongitude=mapEnd.getLongitude()-deltaLon;
    }
    // Latitude
    if (newEnd.getLatitude()<mapEnd.getLatitude())
    {
      newLatitude=mapEnd.getLatitude()+deltaLat;
    }
    return new GeoPoint(newLongitude,newLatitude);
  }
}
