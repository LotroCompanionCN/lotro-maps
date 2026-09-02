package delta.games.lotro.maps.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import delta.common.ui.swing.GuiFactory;
import delta.common.ui.swing.labels.LabelWithHalo;
import delta.common.ui.swing.panels.AbstractPanelController;
import delta.games.lotro.maps.ui.controllers.SelectionController;
import delta.games.lotro.maps.ui.layers.Layer;
import delta.games.lotro.maps.ui.layers.MarkersLayer;
import delta.games.lotro.maps.ui.layersmgr.LayersButtonController;
import delta.games.lotro.maps.ui.layersmgr.LayersDisplayController;
import delta.games.lotro.maps.ui.layersmgr.LayersManagerListener;
import delta.games.lotro.maps.ui.location.MapLocationController;
import delta.games.lotro.maps.ui.location.MapLocationPanelController;
import delta.games.lotro.maps.ui.selection.SelectionManager;

/**
 * Controller for a map panel.
 * <p>This includes:
 * <ul>
 * <li>a map view panel,
 * <li>a location display,
 * <li>a 'labeled' checkbox,
 * <li>zoom/pan controllers,
 * <li>layers management,
 * <li>support for pluggable buttons.
 * </ul>
 * @author DAM
 */
public class MapPanelController extends AbstractPanelController
{
  // Data
  private MapCanvas _canvas;
  // Controllers
  private MapLocationController _locationController;
  private MapLocationPanelController _locationDisplay;
  private SelectionManager _selectionManager;
  private LayersButtonController _layersButton;
  // UI
  private JLayeredPane _layers;
  private JPanel _labeled;
  private List<JButton> _buttons; 
  private MouseAdapter _zoomListener;
  private MouseAdapter _panListener;
  private ComponentAdapter _resizeListener;

  /**
   * Constructor.
   */
  public MapPanelController()
  {
    _canvas=new MapCanvas();
    // Init zoom controller
    initZoomController();
    // Init pan controller
    initPanController();
    // Init selection controller
    initSelectionController();
    // Location
    _locationController=new MapLocationController(_canvas);
    // Location display
    _locationDisplay=new MapLocationPanelController();
    _locationController.addListener(_locationDisplay);
    // Assembly components in a layered pane
    _layers=new JLayeredPane();
    // - canvas
    _layers.add(_canvas,Integer.valueOf(0),0);
    _canvas.setLocation(0,0);
    // - location
    JPanel locationPanel=_locationDisplay.getPanel();
    _layers.add(locationPanel,Integer.valueOf(1),0);
    // - labeled checkbox
    _labeled=buildLabeledCheckboxPanel();
    _layers.add(_labeled,Integer.valueOf(1),0);
    // Buttons
    _buttons=new ArrayList<JButton>();
    // Layers manager
    addLayersButton();
    // Resize handling: when the layered pane is resized (resizable window),
    // re-layout the canvas and the overlay controls to fit the new size.
    _resizeListener=new ComponentAdapter()
    {
      @Override
      public void componentResized(ComponentEvent e)
      {
        Dimension size=_layers.getSize();
        layoutChildren(size);
      }
    };
    _layers.addComponentListener(_resizeListener);
    setPanel(_canvas);
  }

  /**
   * Get the map location controller.
   * @return  the map location controller.
   */
  public MapLocationController getLocationController()
  {
    return _locationController;
  }

  /**
   * Set the visibility of the location display.
   * @param show <code>true</code> to show it, <code>false</code> to hide it.
   */
  public void setLocationVisible(boolean show)
  {
    _locationDisplay.getPanel().setVisible(show);
  }

  /**
   * Add the layers button.
   */
  private void addLayersButton()
  {
    LayersDisplayController layersMgrController=new LayersDisplayController(_canvas.getLayers());
    LayersManagerListener listener=new LayersManagerListener()
    {
      @Override
      public void layersManagerUpdated(LayersDisplayController controller)
      {
        _canvas.repaint();
      }
    };
    layersMgrController.setListener(listener);
    _layersButton=new LayersButtonController(layersMgrController);
    addButton(_layersButton.getTriggerButton());
  }

  private void initZoomController()
  {
    // Zoom with mouse wheel
    MouseAdapter adapter=new MouseAdapter()
    {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e)
      {
        int rotation=e.getWheelRotation();
        if (rotation<0)
        {
          _canvas.zoom((float)Math.sqrt(2));
        }
        else
        {
          _canvas.zoom(1/(float)Math.sqrt(2));
        }
      }
    };
    _canvas.addMouseWheelListener(adapter);
    _zoomListener=adapter;
  }

  private void initPanController()
  {
    // Pan on mouse drag or shift/mouse click
    MouseAdapter adapter=new MouseAdapter()
    {
      private int _xCenter;
      private int _yCenter;
      private int _lastX;
      private int _lastY;

      @Override
      public void mouseDragged(MouseEvent e)
      {
        int deltaX=e.getX()-_lastX;
        int deltaY=e.getY()-_lastY;
        _canvas.pan(_xCenter-deltaX,_yCenter-deltaY);
        _lastX=e.getX();
        _lastY=e.getY();
      }

      @Override
      public void mousePressed(MouseEvent e)
      {
        _xCenter=_canvas.getWidth()/2;
        _yCenter=_canvas.getHeight()/2;
        _lastX=e.getX();
        _lastY=e.getY();
      }

      @Override
      public void mouseClicked(MouseEvent event)
      {
        int button=event.getButton();
        int modifiers=event.getModifiersEx();
        int x=event.getX();
        int y=event.getY();

        if ((button==MouseEvent.BUTTON1) && ((modifiers&InputEvent.SHIFT_DOWN_MASK)!=0))
        {
          _canvas.pan(x,y);
        }
      }
    };
    _canvas.addMouseListener(adapter);
    _canvas.addMouseMotionListener(adapter);
    _panListener=adapter;
  }

  private void initSelectionController()
  {
    _selectionManager=new SelectionManager();
    SelectionController ctrl=new SelectionController(_canvas,_selectionManager);
    _canvas.getInputsManager().addInputController(ctrl);
  }

  private JPanel buildLabeledCheckboxPanel()
  {
    JPanel panel=GuiFactory.buildPanel(new FlowLayout(FlowLayout.LEADING));
    final JCheckBox labeled=GuiFactory.buildCheckbox("");
    panel.add(labeled);
    labeled.setFocusPainted(false);
    LabelWithHalo haloLabel=new LabelWithHalo();
    haloLabel.setForeground(Color.WHITE);
    haloLabel.setText("Labeled");
    panel.add(haloLabel);
    ActionListener al=new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        useLabels(labeled.isSelected());
        _canvas.repaint();
      }
    };
    labeled.addActionListener(al);
    return panel;
  }

  private void useLabels(boolean useLabels)
  {
    for(Layer layer : _canvas.getLayers())
    {
      if (layer instanceof MarkersLayer)
      {
        MarkersLayer markersLayer=(MarkersLayer)layer;
        markersLayer.useLabels(useLabels);
      }
    }
  }

  /**
   * Get the managed canvas.
   * @return the managed canvas.
   */
  public MapCanvas getCanvas()
  {
    return _canvas;
  }

  /**
   * Get the managed layered pane.
   * @return the managed layered pane.
   */
  public JLayeredPane getLayers()
  {
    return _layers;
  }

  /**
   * Get the selection manager.
   * @return the selection manager.
   */
  public SelectionManager getSelectionManager()
  {
    return _selectionManager;
  }

  /**
   * Add a button.
   * @param triggerButton Button to add.
   */
  public void addButton(JButton triggerButton)
  {
    _layers.add(triggerButton,Integer.valueOf(1),0);
    _buttons.add(triggerButton);
  }

  /**
   * Set the size of the managed view.
   * @param viewSize Size to set.
   */
  public void setViewSize(Dimension viewSize)
  {
    _layers.setPreferredSize(viewSize);
    layoutChildren(viewSize);
  }

  /**
   * Lay out the canvas and the overlay controls for the given size.
   * @param size Size of the view (pixels).
   */
  private void layoutChildren(Dimension size)
  {
    _canvas.setSize(size);
    int width=size.width;
    int height=size.height;
    // Place location display (lower left)
    JPanel locationPanel=_locationDisplay.getPanel();
    locationPanel.setSize(100,40);
    int locationY=Math.max(0,height-locationPanel.getHeight());
    locationPanel.setLocation(0,locationY);
    // Place 'labeled' checkbox
    _labeled.setSize(_labeled.getPreferredSize());
    _labeled.setLocation(Math.max(0,width-_labeled.getWidth()-10),17);
    // Place the 'layers' button
    int x=10;
    for(JButton button : _buttons)
    {
      button.setLocation(x,17);
      button.setSize(button.getPreferredSize());
      x+=button.getPreferredSize().width+10;
    }
    _canvas.repaint();
  }

  @Override
  public void dispose()
  {
    super.dispose();
    // Data
    if (_canvas!=null)
    {
      if (_zoomListener!=null)
      {
        _canvas.removeMouseWheelListener(_zoomListener);
        _zoomListener=null;
      }
      if (_panListener!=null)
      {
        _canvas.removeMouseMotionListener(_panListener);
        _canvas.removeMouseListener(_panListener);
        _panListener=null;
      }
      _canvas.dispose();
      _canvas=null;
    }
    // Controllers
    if (_locationDisplay!=null)
    {
      if (_locationController!=null)
      {
        _locationController.removeListener(_locationDisplay);
      }
      _locationDisplay.dispose();
      _locationDisplay=null;
    }
    if (_selectionManager!=null)
    {
      _selectionManager.dispose();
      _selectionManager=null;
    }
    if (_locationController!=null)
    {
      _locationController.dispose();
      _locationController=null;
    }
    if (_layersButton!=null)
    {
      _layersButton.dispose();
      _layersButton=null;
    }
    // UI
    if (_resizeListener!=null)
    {
      if (_layers!=null)
      {
        _layers.removeComponentListener(_resizeListener);
      }
      _resizeListener=null;
    }
    _layers=null;
    _labeled=null;
    _buttons=null;
  }
}
