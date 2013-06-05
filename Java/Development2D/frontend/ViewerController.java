package frontend;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ViewerController extends JFrame {

  private static final long serialVersionUID = 1L;

  /********************************************************************************
   * Viewer Controller Data
   * 
   * These references allow us to adjust the main UI object, an instance of
   * DevelopmentUI. currentSurfacePath keeps track of the filename for the
   * surface we currently display to the user.
   ********************************************************************************/
  private DevelopmentUI currentSim;
  private String currentSurfacePath;
  private boolean sessionEnded = false;

  public ViewerController(DevelopmentUI dui) {
    currentSim = dui;
    layoutGUI();
    synchronizeSettings();
    this.setVisible(true);
    //this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  }

  public String getPath() {
    return currentSurfacePath;
  }

  public void setSimulation(DevelopmentUI dui) {
    currentSim = dui;
    synchronizeSettings();
  }

  public boolean sessionEnded() {
    return sessionEnded;
  }

  public void endSession() {
    sessionEnded = true;
    currentSim.terminate();
    this.setVisible(false);
  }

  /********************************************************************************
   * synchronizeSettings
   * 
   * This method is used to push all the settings currently contained in
   * ViewerController onto the current DevelopmentUI object. This method is only
   * used when we're given a fresh DevelopmentUI object and want to make sure
   * its state matches the state of the buttons on the panel. Usually, each
   * button will have its own action listener object for pushing individual
   * changes onto the current DevelopmentUI object.
   ********************************************************************************/
  private void synchronizeSettings() {
    currentSim.setRecursionDepth(Integer.parseInt(recDepth.getValue()
        .toString()));
    currentSim.setMovingMarkerCount(Integer.parseInt(numObjects.getValue()
        .toString()));

    double sliderValue = speedSlider.getValue() / 1000.0;
    double newSpeed = 0.05 * Math.pow(Math.E, sliderValue);
    currentSim.setMovingMarkerSpeed(newSpeed);

    double newScale = scalingSlider.getValue() / 10.0;
    currentSim.setMovingMarkerScale(newScale);

    currentSim.setGeodesicLength(geoSlider.getValue());

    currentSim.clearGeodesic();

    boolean bb = stopStartButton.getText().equals("Stop");
    currentSim.setMarkerMobility(bb);

    currentSim.setExponentialView(showView2DBox.isSelected());
    currentSim.setEmbeddedView(showEmbeddedBox.isSelected());
    currentSim.setFirstPersonView(showView3DBox.isSelected());

    currentSim.setEmbeddedZoom(embeddedZoomSlider.getValue() / 100.0);
    currentSim.setExponentialZoom(Math.pow(10,
        (exponentialZoomSlider.getValue() - 100) / 100.0));

    currentSim.setDrawEdges(drawEdgesBox.isSelected());
    currentSim.setDrawFaces(drawFacesBox.isSelected());
    currentSim.setSourceVisible(drawAvatarBox.isSelected());
  }

  /********************************************************************************
   * JFrame Data
   * 
   * This data is required for laying out the window.
   ********************************************************************************/
  private JMenuBar menuBar;
  private JMenu file;
  private JPanel textBoxPanel;
  private JPanel recDepthPanel;
  private JFormattedTextField recDepth;
  private JLabel recDepthLabel;
  private JPanel numObjectsPanel;
  private JFormattedTextField numObjects;
  private JLabel numObjectsLabel;
  private JPanel sliderPanel;
  private JSlider speedSlider;
  private JSlider scalingSlider;
  private JSlider geoSlider;
  private JSlider exponentialZoomSlider;
  private JSlider embeddedZoomSlider;
  private JPanel buttonPanel;
  private JButton stopStartButton;
  private JButton clearGeosButton;
  private JPanel viewerPanel;
  private JCheckBox showView2DBox;
  private JCheckBox showView3DBox;
  private JCheckBox showEmbeddedBox;
  private JPanel drawOptionsPanel;
  private JCheckBox drawEdgesBox;
  private JCheckBox drawFacesBox;
  private JCheckBox drawAvatarBox;
  private JCheckBox TextureEnabledBox;
  public static JCheckBox allowMarkerBox;
  
  private JButton flowButton;

  private static int MAX_SPEED = 4000;
  private static int MAX_SIZE = 10;

  TitledBorder depthBorder = BorderFactory.createTitledBorder("");
  TitledBorder pointBorder = BorderFactory.createTitledBorder("");
  TitledBorder speedBorder = BorderFactory.createTitledBorder("");
  TitledBorder objectsBorder = BorderFactory.createTitledBorder("");
  TitledBorder geoBorder = BorderFactory.createTitledBorder("");
  TitledBorder zoomBorder = BorderFactory.createTitledBorder("");
  TitledBorder zoom2Border = BorderFactory.createTitledBorder("");

  private class SurfaceLoader implements ActionListener {
    private ViewerController vc;
    private String path;

    public SurfaceLoader(ViewerController vc, String surfaceFilePath) {
      this.vc = vc;
      this.path = surfaceFilePath;
    }

    public void actionPerformed(ActionEvent arg0) {
      currentSurfacePath = path;
      File file = null;
      try {
        file = new File(path);
      } catch (Exception ex) {
        System.err.println("Error: Could not locate file " + path + ".");
        System.exit(1);
      }

      // First, lock the interface, so the user can't give data to
      // a non-operational DevelopmentUI.
      vc.currentSurfacePath = file.getAbsolutePath();
      vc.setEnabled(false);
      // Next, signal the current simulation to terminate.
      currentSim.terminate();
    }
  }

  private void layoutGUI() {

    this.setSize(220, 680);
    this.setResizable(false);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
//    this.setTitle("Development View");
    this.setTitle("Controls");
    this.setLayout(new FlowLayout());

    menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    {
      file = new JMenu();
      menuBar.add(file);
      file.setText("File");
      {
        String[][] namesAndPaths = new String[][] {
            { "Regular Tetrahedron", "Data/surfaces/tetra3.off" },
            { "Regular Tetrahedron - One Color", "Data/surfaces/tetra.off" },            
            { "Irregular Tetrahedron", "Data/surfaces/tetra2.off" },
            { "Cube", "Data/surfaces/cube_surf.off" },
            { "Dodecahedron", "Data/surfaces/dodec2.off" },
            { "Icosahedron", "Data/off/icosa.off" },
            { "Cone", "Data/surfaces/scaledCone.off" },
            { "Neckpinch", "Data/surfaces/large_neckpinch.off" },
            { "Irregular Tetrahedron 2 (xml)", "Data/Triangulations/2DManifolds/tetrahedron4.xml"},
            { "Triangular Prism (xml)", "Data/Triangulations/2DManifolds/triangularPrism.xml"},
//            { "Saddle", "Data/off/saddle.off" } 
            };

        for (String[] pair : namesAndPaths) {
          String name = pair[0];
          String path = pair[1];
          JMenuItem jmi = new JMenuItem();
          file.add(jmi);
          jmi.setText(name);
          SurfaceLoader sl = new SurfaceLoader(this, path);
          jmi.addActionListener(sl);
        }
      }

      JMenuItem jmi = new JMenuItem();
      jmi.setText("Quit Explorer");
      jmi.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          endSession();
        }
      });

      file.add(jmi);

    }

    /*********************************************************************************
     * Text Box Panel
     * 
     * Contains: - Recursion depth text box. - Number of moving objects text
     * box.
     *********************************************************************************/
    textBoxPanel = new JPanel();
    getContentPane().add(textBoxPanel);
    textBoxPanel.setBorder(BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED));
    textBoxPanel.setLayout(new GridLayout(2, 1));
    {
      recDepth = new JFormattedTextField(NumberFormat.getIntegerInstance());
      recDepth.setColumns(3);
      recDepth.setValue(5);

      recDepthLabel = new JLabel("Recursion Depth");
      recDepthPanel = new JPanel();
      recDepthPanel.setLayout(new FlowLayout());
      recDepthPanel.add(recDepthLabel);
      recDepthPanel.add(recDepth);
      textBoxPanel.add(recDepthPanel);

      numObjects = new JFormattedTextField(NumberFormat.getIntegerInstance());
      numObjects.setColumns(3);
      numObjects.setValue(0);

      numObjectsLabel = new JLabel("Number of objects");
      numObjectsPanel = new JPanel();
      numObjectsPanel.setLayout(new FlowLayout());
      numObjectsPanel.add(numObjectsLabel);
      numObjectsPanel.add(numObjects);
      textBoxPanel.add(numObjectsPanel);

      // Recursion depth action listener
      ActionListener recDepthListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String value = recDepth.getValue().toString();
          int newDepth = Integer.parseInt(value);
          if (newDepth < 1) {
            newDepth = 1;
          } else if (newDepth > 20) {
            newDepth = 20;
          }
          currentSim.setRecursionDepth(newDepth);
        }
      };
      recDepth.addActionListener(recDepthListener);

      // Number of objects action listener
      ActionListener numObjectsListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          Integer numMarkers = Integer.parseInt(numObjects.getValue()
              .toString());
          if (numMarkers < 0) {
            numMarkers = 0;
          } else if (numMarkers > 20) {
            numMarkers = 20;
          }
          currentSim.setMovingMarkerCount(numMarkers);
        }
      };
      numObjects.addActionListener(numObjectsListener);
    }

    /*********************************************************************************
     * Slider Panel
     * 
     * Contains: - Speed slider. - Scale slider. - Geodesic length slider - Zoom
     * Slider
     * 
     * Note: Speed slider is using the exponential function y = 0.05*e^x to
     * convert input from the slider to a speed. This gives you more control
     * over low speeds on the slider.
     * 
     * Note: The Exponential Zoom Slider uses the simple exponential function y
     * = 10^x to allow the user to have greater control over their ability to
     * zoom in on the surface.
     *********************************************************************************/
    sliderPanel = new JPanel();
    getContentPane().add(sliderPanel);
    sliderPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
    {
      speedSlider = new JSlider();
      sliderPanel.add(speedSlider);
      speedSlider.setMaximum(MAX_SPEED);
      speedSlider.setValue(0);

      final DecimalFormat speedFormat = new DecimalFormat("0.00");
      speedSlider.setBorder(speedBorder);
      speedBorder.setTitle("Speed (" + speedFormat.format(.05) + ")");

      ChangeListener speedSliderListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          double sliderValue = speedSlider.getValue() / 1000.0;
          double newSpeed = 0.05 * Math.pow(Math.E, sliderValue);
          currentSim.setMovingMarkerSpeed(newSpeed);
          speedBorder.setTitle("Speed (" + speedFormat.format(newSpeed) + ")");

          // Prevent keyboard arrow input from varying this slider
          // by moving the focus to a button.
          stopStartButton.requestFocus();
        }
      };
      speedSlider.addChangeListener(speedSliderListener);
    }

    {
      scalingSlider = new JSlider();
      sliderPanel.add(scalingSlider);
      scalingSlider.setMaximum(MAX_SIZE);
      scalingSlider.setValue((int) 1.0 * 10);
      scalingSlider.setBorder(pointBorder);
      pointBorder.setTitle("Object scaling (" + scalingSlider.getValue() / 10.0
          + ")");

      // Scaling slider change listener
      ChangeListener scalingSliderListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          double newScale = scalingSlider.getValue() / 10.0;
          currentSim.setMovingMarkerScale(newScale);
          pointBorder.setTitle("Object scaling (" + newScale + ")");

          // Prevent keyboard arrow input from varying this slider
          // by moving the focus to a button.
          stopStartButton.requestFocus();
        }
      };
      scalingSlider.addChangeListener(scalingSliderListener);
    }

    {
      geoSlider = new JSlider();
      sliderPanel.add(geoSlider);
      geoSlider.setMaximum(30);
      geoSlider.setValue(5);
      geoSlider.setBorder(geoBorder);
      geoBorder.setTitle("Geodesic length (" + geoSlider.getValue() + ")");

      ChangeListener geoSliderListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
          int newLength = geoSlider.getValue();
          currentSim.setGeodesicLength(newLength);
          geoBorder.setTitle("Geodesic length (" + newLength + ")");

          // Prevent keyboard arrow input from varying this slider
          // by moving the focus to a button.
          stopStartButton.requestFocus();
        }

      };
      geoSlider.addChangeListener(geoSliderListener);
    }
    {
      exponentialZoomSlider = new JSlider();
      sliderPanel.add(exponentialZoomSlider);
      exponentialZoomSlider.setMaximum(200);
      exponentialZoomSlider.setValue(100);
      exponentialZoomSlider.setBorder(zoomBorder);
      DecimalFormat percentFormat = new DecimalFormat("0%");
      double sliderValue = ((exponentialZoomSlider.getValue() / 100.0));
      double percentZoom = (1 / sliderValue);
      zoomBorder.setTitle("Exponential Zoom ("
          + percentFormat.format(percentZoom) + ")");

      ChangeListener exponentialZoomListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          DecimalFormat percentFormat = new DecimalFormat("0%");
          double zoom = (exponentialZoomSlider.getValue() - 100.0) / 100.0;
          currentSim.setExponentialZoom(Math.pow(10, zoom));
          double zoomToSet = (1.0 / (exponentialZoomSlider.getValue() / 100.0));
          zoomBorder.setTitle("Exponential Zoom ("
              + percentFormat.format(zoomToSet) + ")");

          // Prevent keyboard arrow input from varying this slider
          // by moving the focus to a button.
          stopStartButton.requestFocus();
        }
      };
      exponentialZoomSlider.addChangeListener(exponentialZoomListener);
    }
    {
      embeddedZoomSlider = new JSlider();
      sliderPanel.add(embeddedZoomSlider);
      embeddedZoomSlider.setMaximum(1000);
      embeddedZoomSlider.setValue(300);
      embeddedZoomSlider.setBorder(zoom2Border);
      DecimalFormat percentFormat = new DecimalFormat("0%");
      double zoomToSet = (1.0 / (embeddedZoomSlider.getValue() / 300.0));
      zoom2Border.setTitle("Embedded Zoom (" + percentFormat.format(zoomToSet)
          + ")");

      ChangeListener embeddedZoomListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          DecimalFormat percentFormat = new DecimalFormat("0%");
          double zoomToSet = (1.0 / (embeddedZoomSlider.getValue() / 300.0));
          zoom2Border.setTitle("Embedded Zoom ("
              + percentFormat.format(zoomToSet) + ")");
          double zoom = embeddedZoomSlider.getValue() / 100.0;
          currentSim.setEmbeddedZoom(zoom);

          // Prevent keyboard arrow input from varying this slider
          // by moving the focus to a button.
          stopStartButton.requestFocus();
        }
      };
      embeddedZoomSlider.addChangeListener(embeddedZoomListener);
    }

    /********************************************************************************
     * Button Panel
     * 
     * Contains: - Start/stop button. - Clear geodesics button.
     ********************************************************************************/
    buttonPanel = new JPanel();
    getContentPane().add(buttonPanel);
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

    {
      clearGeosButton = new JButton();
      clearGeosButton.setText("Clear geodesics");
      buttonPanel.add(clearGeosButton);
      
      flowButton = new JButton();
      flowButton.setText("2D Yamabe Flow");
      buttonPanel.add(flowButton);
      
      ActionListener FlowButtonListener = new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {       	  
        	  // As of now, the yamabe flow only works with xml files and only xml files with length data
        	  if(currentSurfacePath.substring(currentSurfacePath.length()-3, currentSurfacePath.length()).equals("off"))
      				JOptionPane.showMessageDialog(null, "Only works with xml type manifolds");
        	  else
        		  currentSim.runFlow();
          }
      };
      flowButton.addActionListener(FlowButtonListener);

      ActionListener geosButtonListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          currentSim.clearGeodesic();
        }
      };
      clearGeosButton.addActionListener(geosButtonListener);

    }

    {
      stopStartButton = new JButton();
      stopStartButton.setText("Stop");
      buttonPanel.add(stopStartButton);

      ActionListener stopStartButtonListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean bb = stopStartButton.getText().equals("Stop");
          currentSim.setMarkerMobility(!bb);
          if (bb) {
            stopStartButton.setText("Start");
          } else {
            stopStartButton.setText("Stop");
          }
        }
      };
      stopStartButton.addActionListener(stopStartButtonListener);
    }

    /*********************************************************************************
     * ViewerPanel
     * 
     * Contains: - Checkboxes for each of the three views. - ShowAvatar
     * checkbox. - ShowTexture checkbox.
     *********************************************************************************/
    viewerPanel = new JPanel();
    getContentPane().add(viewerPanel);
    viewerPanel.setBorder(BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED));
    viewerPanel.setLayout(new BoxLayout(viewerPanel, BoxLayout.Y_AXIS));

    {
      showView2DBox = new JCheckBox();
      viewerPanel.add(showView2DBox);
      showView2DBox.setText("Show Exponential View");
      showView2DBox.setSelected(true);
      ActionListener view2DListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean checkBox = showView2DBox.isSelected();
          currentSim.setExponentialView(checkBox);
        }
      };
      showView2DBox.addActionListener(view2DListener);
    }

    {
      showView3DBox = new JCheckBox();
      viewerPanel.add(showView3DBox);
      showView3DBox.setText("Show First Person View");
      showView3DBox.setSelected(true);
      ActionListener view3DListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean checkBox = showView3DBox.isSelected();
          currentSim.setFirstPersonView(checkBox);
        }
      };
      showView3DBox.addActionListener(view3DListener);
    }

    {
      showEmbeddedBox = new JCheckBox();
      viewerPanel.add(showEmbeddedBox);
      showEmbeddedBox.setText("Show Embedded View");
      showEmbeddedBox.setSelected(true);
      ActionListener viewEmbeddedListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean checkBox = showEmbeddedBox.isSelected();
          currentSim.setEmbeddedView(checkBox);
        }
      };
      showEmbeddedBox.addActionListener(viewEmbeddedListener);
    }

    {
      TextureEnabledBox = new JCheckBox();
      viewerPanel.add(TextureEnabledBox);
      TextureEnabledBox.setText("Display Texture");
      TextureEnabledBox.setSelected(true);
      ActionListener TextureEnabledListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean textureEnabled = TextureEnabledBox.isSelected();
          currentSim.setDrawTextures(textureEnabled);
        }
      };
      TextureEnabledBox.addActionListener(TextureEnabledListener);
    }
    
    allowMarkerBox = new JCheckBox();
    viewerPanel.add(allowMarkerBox);
    allowMarkerBox.setText("Allow Markers");
    TextureEnabledBox.setEnabled(true);

    /*********************************************************************************
     * DrawOptionsPanel
     * 
     * Contains: - ShowEdges checkbox. - ShowFaces checkbox. - ShowTexture
     * checkbox.
     *********************************************************************************/

    drawOptionsPanel = new JPanel();
    getContentPane().add(drawOptionsPanel);
    drawOptionsPanel.setBorder(BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED));
    drawOptionsPanel
        .setLayout(new BoxLayout(drawOptionsPanel, BoxLayout.Y_AXIS));

    {
      drawEdgesBox = new JCheckBox();
      drawOptionsPanel.add(drawEdgesBox);
      drawEdgesBox.setText("Draw edges");
      drawEdgesBox.setSelected(false);

      ActionListener drawEdgesListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean boxChecked = drawEdgesBox.isSelected();
          currentSim.setDrawEdges(boxChecked);
        }
      };
      drawEdgesBox.addActionListener(drawEdgesListener);
    }

    {
      drawFacesBox = new JCheckBox();
      drawOptionsPanel.add(drawFacesBox);
      drawFacesBox.setText("Draw faces");
      drawFacesBox.setSelected(true);

      ActionListener drawFacesListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean boxChecked = drawFacesBox.isSelected();
          currentSim.setDrawFaces(boxChecked);
        }
      };
      drawFacesBox.addActionListener(drawFacesListener);
    }

    {
      drawAvatarBox = new JCheckBox();
      drawOptionsPanel.add(drawAvatarBox);
      drawAvatarBox.setText("Draw avatar");
      drawAvatarBox.setSelected(true);

      // Draw avatar action listener
      ActionListener drawAvatar = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean showAvatar = drawAvatarBox.isSelected();
          currentSim.setSourceVisible(showAvatar);
        }
      };
      drawAvatarBox.addActionListener(drawAvatar);
    }
  }
}
