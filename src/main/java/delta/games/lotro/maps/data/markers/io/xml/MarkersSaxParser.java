package delta.games.lotro.maps.data.markers.io.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import delta.common.utils.NumericTools;
import delta.common.utils.i18n.SingleLocaleLabelsManager;
import delta.common.utils.xml.SAXParsingTools;
import delta.games.lotro.maps.data.GeoPoint;
import delta.games.lotro.maps.data.Marker;

/**
 * SAX parser for markers files.
 * @author DAM
 */
public final class MarkersSaxParser extends DefaultHandler
{
  private static final Logger LOGGER=LoggerFactory.getLogger(MarkersSaxParser.class);

  private SingleLocaleLabelsManager _i18n;
  private List<Marker> _parsedMarkers;

  /**
   * Constructor.
   * @param i18n Labels manager.
   */
  public MarkersSaxParser(SingleLocaleLabelsManager i18n)
  {
    _i18n=i18n;
    _parsedMarkers=new ArrayList<Marker>();
  }

  /**
   * Parse the XML file.
   * @param source Source file.
   * @return List of parsed items.
   */
  public List<Marker> parseMarkersFile(File source)
  {
    try
    {
      // Use the default (non-validating) parser
      SAXParserFactory factory=SAXParserFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      SAXParser saxParser=factory.newSAXParser();
      if (source.getName().endsWith(".gz"))
      {
        try (InputStream is=new GZIPInputStream(new FileInputStream(source)))
        {
          saxParser.parse(is,this);
        }
      }
      else
      {
        saxParser.parse(source,this);
      }
      saxParser.reset();
      return _parsedMarkers;
    }
    catch (Exception e)
    {
      LOGGER.error("Error when loading markers file "+source,e);
    }
    return new ArrayList<Marker>();
  }

  @Override
  public void startElement(String uri, String localName, String qualifiedName, Attributes attributes) throws SAXException
  {
    if (MarkersXMLConstants.MARKER_TAG.equals(qualifiedName))
    {
      Marker marker=new Marker();
      // Identifier
      String idStr=attributes.getValue(MarkersXMLConstants.ID_ATTR);
      int id=NumericTools.parseInt(idStr,0);
      marker.setId(id);
      // DID
      String didStr=attributes.getValue(MarkersXMLConstants.DID_ATTR);
      int did=NumericTools.parseInt(didStr,0);
      marker.setDid(did);
      // Label
      String label=_i18n.getLabel(String.valueOf(did));
      if (label==null)
      {
        label=SAXParsingTools.getStringAttribute(attributes,MarkersXMLConstants.LABEL_ATTR,"");
      }
      marker.setLabel(label);
      // Category code
      String categoryCodeStr=attributes.getValue(MarkersXMLConstants.CATEGORY_ATTR);
      int categoryCode=NumericTools.parseInt(categoryCodeStr,0);
      marker.setCategoryCode(categoryCode);
      // Parent zone ID
      String parentZoneIdStr=attributes.getValue(MarkersXMLConstants.PARENT_ZONE_ID_ATTR);
      int parentZoneId=NumericTools.parseInt(parentZoneIdStr,0);
      marker.setParentZoneId(parentZoneId);
      // Position
      float latitude=NumericTools.parseFloat(attributes.getValue(MarkersXMLConstants.LATITUDE_ATTR),0);
      float longitude=NumericTools.parseFloat(attributes.getValue(MarkersXMLConstants.LONGITUDE_ATTR),0);
      GeoPoint position=new GeoPoint(longitude,latitude);
      marker.setPosition(position);
      _parsedMarkers.add(marker);
    }
  }
}
