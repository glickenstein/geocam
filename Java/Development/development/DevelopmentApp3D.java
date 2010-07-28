package development;

import geoquant.*;
import inputOutput.TriangulationIO;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.pick.PickResult;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.AxisState;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

import triangulation.*;

public class DevelopmentApp3D {
  
  //position info
  private static Tetra source_tetra_;
  private static Vector source_point_;
  
  //camera stuff
  private static Viewer viewer_;
  
  private static Vector camera_position_; //should always be 0,0,0
  private static Vector camera_up_;
  private static Vector camera_forward_;
  private static Vector camera_right_;
  
  private static SceneGraphPath camera_source_;
  private static SceneGraphPath camera_free_;
  
  //options
  private static final int max_recursion_depth_ = 3;
  //private static final double movement_units_per_second_ = 1.0;
  private static final double movement_seconds_per_rotation_ = 2.0;
  //private static final double bounding_cube_side_length_ = 4.0;
  
  //data
  private static SceneGraphComponent sgc_root_; 
  private static SceneGraphComponent sgc_devmap_;
  private static SceneGraphComponent sgc_camera_;
  

  //USER INTERFACE
  //==============================
  static class UIPanel_CameraMode extends ViewShrinkPanelPlugin {

    private void makeUIComponents() {

      ButtonGroup group = new ButtonGroup();
      
      JRadioButton button;
      
      //source mode button
      button = new JRadioButton("Source Point");
      button.setSelected(true);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){ 
          viewer_.setCameraPath(camera_source_); 
          viewer_.render();
        }
      });
      shrinkPanel.add(button);
      group.add(button);
      
      //free mode button
      button = new JRadioButton("Outside Development");
      button.setSelected(false);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){ 
          viewer_.setCameraPath(camera_free_); 
          viewer_.render();
        }
      });
      shrinkPanel.add(button);
      group.add(button);
      
      //specify layout
      shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6)); //a little padding
      shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),BoxLayout.Y_AXIS));
    }
    
    @Override
    public void install(Controller c) throws Exception {
      makeUIComponents();
      super.install(c);
    }
    
    @Override
    public PluginInfo getPluginInfo(){
      PluginInfo info = new PluginInfo("Camera Mode", "");
      return info;
    }
  };
  
  //TOOL(S) FOR MOVEMENT
  //==============================
  /*static class ManifoldMovementTool extends AbstractTool {

    public ManifoldMovementTool() {
      super(InputSlot.LEFT_BUTTON);
      addCurrentSlot(InputSlot.getDevice("PointerTransformation"));
    }
    @Override
    public void activate(ToolContext tc) { perform(tc); }

    @Override
    public void perform(ToolContext tc) {
      
      //tc.
      
      if(pr==null){ return; }
      else if(pr.getPickType() == PickResult.PICK_TYPE_FACE){
        //if(source_face_ != pr.getIndex()){ System.out.printf("New source_face_: %d\n", pr.getIndex()); }
        source_face_ = facelist_[pr.getIndex()];
        source_local_coords_ = source_face_.getLocalCoordsFromWorldCoords(pr.getObjectCoordinates());
        computeVisibleGeometry();
      }
    }  
  };*/
  static class ManifoldMovementTool extends AbstractTool {
    
    private static long time; 
    private static final double radians_per_millisecond = Math.PI/(movement_seconds_per_rotation_*500);
    
    private static final InputSlot FORWARD_BACKWARD = InputSlot.getDevice("ForwardBackwardAxis");
    private static final InputSlot LEFT_RIGHT = InputSlot.getDevice("LeftRightAxis");
    private static final InputSlot SYSTEM_TIMER = InputSlot.SYSTEM_TIME;  
    
    public ManifoldMovementTool() {
      super(FORWARD_BACKWARD, LEFT_RIGHT); //'activate' tool on F/B or L/R
      addCurrentSlot(SYSTEM_TIMER); //'perform' tool on tick
    }
    
    @Override
    public void activate(ToolContext tc) {
      time = tc.getTime(); //set initial time
    }
    
    @Override
    public void perform(ToolContext tc) {
      
      //get axis state
      AxisState as_fb = tc.getAxisState(FORWARD_BACKWARD);
      AxisState as_lr = tc.getAxisState(LEFT_RIGHT);
      
      //get dt and update time
      long newtime = tc.getTime();
      long dt = newtime - time;
      time = newtime;

      //move forward/backward
      if(as_fb.isPressed()){
        rotateCameraRightAxis(radians_per_millisecond*dt*as_fb.doubleValue());
      }
      
      //move left/right
      if(as_lr.isPressed()){
        rotateCameraUpAxis(-radians_per_millisecond*dt*as_lr.doubleValue());
      }
      
      //recompute
      updateCamera();
    }  
  };
  
  
  //MAIN
  //==============================
  public static void main(String[] args){
    
    TriangulationIO.readTriangulation("Data/Triangulations/3DManifolds/pentachoron.xml");
    
    Iterator<Integer> i = null;
    
    //set edge lengths
    i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Edge e = Triangulation.edgeTable.get(key);
      double rand = Math.random(); //random return value is in [0,1)
      Length.at(e).setValue(1.5); 
    }
    
    //set up sgc_root
    sgc_root_ = new SceneGraphComponent();
    sgc_root_.addTool(new ManifoldMovementTool());
    
    //pick some arbitrary tetra and source point, and compute geometry
    i = Triangulation.tetraTable.keySet().iterator();
    source_tetra_ = Triangulation.tetraTable.get(i.next());
    
    source_point_ = new Vector(0,0,0);
    Iterator<Vertex> iv = source_tetra_.getLocalVertices().iterator();
    while(iv.hasNext()){
      source_point_.add(Coord3D.coordAt(iv.next(), source_tetra_));
    }
    source_point_.scale(1.0f/4.0f);

    computeDevelopmentMap();
    
    //make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(90);
    
    sgc_camera_ = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgc_root_.addChild(sgc_camera_);
    sgc_camera_.setCamera(camera);
    
    camera_position_ = new Vector(0,0,0);
    camera_up_ = new Vector(0,1,0);
    camera_forward_ = new Vector(0,0,-1);
    camera_right_ = new Vector(1,0,0);
    updateCamera();
    
    //jrviewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root_);
    //jrv.addVRSupport();
    //jrv.addContentSupport(ContentType.TerrainAligned);
    //jrv.registerPlugin(new ContentAppearance());
    //jrv.registerPlugin(new ContentLoader());
    //jrv.registerPlugin(new ContentTools());
    jrv.registerPlugin(new UIPanel_CameraMode());
    jrv.setShowPanelSlots(true,false,false,false);
    jrv.startup();
    
    //make jrviewer use the camera we set up
    viewer_ = jrv.getViewer();
    camera_free_ = viewer_.getCameraPath();
    camera_source_ = SceneGraphUtility.getPathsBetween(viewer_.getSceneRoot(), sgc_camera_).get(0);
    camera_source_.push(sgc_camera_.getCamera());
    viewer_.setCameraPath(camera_source_);
  }
  
  //ALGORITHM
  //==============================
  private static void computeDevelopmentMap(){

    //rotation matrix sending dir -> (0,1), rot_cw_pi/2(dir) -> (1,0)
    //double x = source_direction_.getComponent(0);
    //double y = source_direction_.getComponent(1);
    //Matrix M = new Matrix(new double[][]{ new double[] {y,-x}, new double[] {x,y} });
    AffineTransformation T = new AffineTransformation(Vector.scale(source_point_,-1));
    //T.leftMultiply(new AffineTransformation(M));
    
    sgc_root_.removeChild(sgc_devmap_);
    sgc_devmap_ = new SceneGraphComponent();
    
    //long t = System.currentTimeMillis();
    iterateDevelopment(sgc_devmap_,0,source_tetra_,null,null,T);
    //System.out.printf("Time to calculate: %d ms\n", System.currentTimeMillis() - t);
    
    sgc_root_.addChild(sgc_devmap_);

  }

  public static void iterateDevelopment(SceneGraphComponent sgc_devmap, int depth, Tetra cur_tetra, Face source_face, Frustum3D current_frustum, AffineTransformation current_trans){
    
    //note: source_face and current_frustum may be null
    
    //get new affine transformation
    AffineTransformation new_trans = new AffineTransformation(3);
    if(source_face != null){
      AffineTransformation coord_trans = CoordTrans3D.affineTransAt(cur_tetra, source_face);
      new_trans.leftMultiply(coord_trans);
    }
    new_trans.leftMultiply(current_trans);
    
    //apply new_trans to tetra and orient the faces
    OrientedTetra oriented_tetra = new OrientedTetra(cur_tetra, new_trans);
    
    //make sure we are within bounding box
    //if(isOutsideBoundingBox(oriented_tetra)){ return; }
    
    //clip the tetra by the current frustum
    ArrayList<EmbeddedFace> cliptetra = new ArrayList<EmbeddedFace>();
    
    if(current_frustum != null){
      //get list of EmbeddedFaces by clipping with Frustum3D and using ConvexHull3D
      ArrayList<Vector> cliptetra_points = current_frustum.clipFace(oriented_tetra);
      if(cliptetra_points.size() < 4){ return; } //quit this branch if cliptetra has no faces
      ConvexHull3D ch3d = new ConvexHull3D(cliptetra_points);
      cliptetra = ch3d.getFaces();
      
    }else{
      //get list of EmbeddedFaces directly from oriented_tetra
      for(int j=0; j<4; j++){
        OrientedFace of = oriented_tetra.getOrientedFace(j);
        cliptetra.add(new EmbeddedFace(of.getVector(0), of.getVector(1), of.getVector(2)));
      }
    }
    
    //add clipped tetra to display
    Color color = Color.getHSBColor((float)cur_tetra.getIndex()/(float)Triangulation.tetraTable.size(), 0.5f, 0.9f);
    if(cur_tetra == source_tetra_){ color = Color.WHITE; }
    SceneGraphComponent sgc_new_tetra = sgcFromEFList(cliptetra, color);
    sgc_devmap.addChild(sgc_new_tetra);
    
    //see which faces to continue developing on, if any
    if(depth >= max_recursion_depth_){ return; }
    
    for(int j=0; j<4; j++){
      OrientedFace of = oriented_tetra.getOrientedFace(j);
      
      Face f = of.getFace();
      if(f == source_face){ continue; }
      
      //find tetra incident to cur_tetra through this face
      Iterator<Tetra> it = f.getLocalTetras().iterator();
      Tetra next_tetra = it.next();
      if(next_tetra == cur_tetra){ next_tetra = it.next(); }
      
      //intersect frustums
      Frustum3D frust = new Frustum3D(of.getVector(0), of.getVector(1), of.getVector(2));
      Frustum3D new_frust = null;
      if(current_frustum == null){ new_frust = frust; }
      else{ new_frust = Frustum3D.intersect(frust, current_frustum); }
      if(new_frust == null){ continue; }
      
      //develop off of oriented_face
      iterateDevelopment(sgc_devmap, depth+1, next_tetra, f, new_frust, new_trans);
    }
  }
  
  //AUXILLIARY METHODS
  //==============================
  private static void rotateCameraRightAxis(double theta){
    
    //up = +theta, down = -theta
    double c = Math.cos(theta);
    double s = Math.sin(theta);

    //f'=fc+us, u'=-fs+uc
    Vector new_forward = Vector.add(Vector.scale(camera_forward_, c), Vector.scale(camera_up_, s));
    Vector new_up = Vector.add(Vector.scale(camera_forward_, -s), Vector.scale(camera_up_, c));
    camera_forward_ = Vector.normalize(new_forward);
    camera_up_ = Vector.normalize(new_up);
  }

  private static void rotateCameraUpAxis(double theta){
  
    //left = +theta, right = -theta
    double c = Math.cos(theta);
    double s = Math.sin(theta);
    
    //f'=fc-rs, r'=fs+rc
    Vector new_forward = Vector.add(Vector.scale(camera_forward_, c), Vector.scale(camera_right_, -s));
    Vector new_right = Vector.add(Vector.scale(camera_forward_, s), Vector.scale(camera_right_, c));
    camera_forward_ = Vector.normalize(new_forward);
    camera_right_ = Vector.normalize(new_right);
  }
  
  private static void updateCamera(){
    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        camera_right_.getComponent(0),camera_up_.getComponent(0),camera_forward_.getComponent(0),camera_position_.getComponent(0),
        camera_right_.getComponent(1),camera_up_.getComponent(1),camera_forward_.getComponent(1),camera_position_.getComponent(1),
        camera_right_.getComponent(2),camera_up_.getComponent(2),camera_forward_.getComponent(2),camera_position_.getComponent(2),
        0,0,0,1
    );
    M.assignTo(sgc_camera_);
  }
  
  public static SceneGraphComponent sgcFromEFList(ArrayList<EmbeddedFace> eflist, Color color){
    
    //create a sgc for the tetra, after applying specified affine transformation
    SceneGraphComponent sgc = new SceneGraphComponent();
    
    //create appearance
    Appearance app = new Appearance();
    
    //set some basic attributes
    app.setAttribute(CommonAttributes.FACE_DRAW, true);
    app.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    
    //set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app, true);
    
    //line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setLineWidth(0.0);
    dls.setDiffuseColor(Color.BLACK);
    
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(color);
    dps.setTransparency(0.92d);
    
    //set appearance
    sgc.setAppearance(app);

    Iterator<EmbeddedFace> ief = eflist.iterator();
    while(ief.hasNext()){
      EmbeddedFace ef = ief.next();
      SceneGraphComponent sgc_child = new SceneGraphComponent();
      sgc_child.setGeometry(ef.getGeometry(color));
      sgc.addChild(sgc_child);
    }
    
    return sgc;
  }
}
