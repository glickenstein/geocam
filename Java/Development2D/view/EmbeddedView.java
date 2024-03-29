package view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.Set;

import beta.AnimatedTexture;

import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import triangulation.Face;
import triangulation.Triangulation;
import util.Vector;
import view.TextureLibrary.TextureDescriptor;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import development.Development;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.TextureCoords;

/*********************************************************************************
 * EmbeddedView
 * 
 * Given a triangulated surface embedded in 3-dimensional Euclidean space, this
 * View can present a visualization of the embedding. Specifically, the camera
 * will be positioned so that it either displays a fixed view of the surface
 * sitting in R^3, or else can be configured so that the camera hovers above a
 * designated point on the surface.
 *********************************************************************************/

public class EmbeddedView extends View {

  private HashMap<Marker, SceneGraphComponent> sgcpools;

  // private SceneGraphComponent sgcLight;

  /*********************************************************************************
   * EmbeddedView
   * 
   * Given a development object and a color scheme, this constructor initializes
   * an EmbeddedView object to display the specified surface.
   *********************************************************************************/
  public EmbeddedView(Development d, MarkerHandler mh) {
    super(d, mh);
    sgcpools = new HashMap<Marker, SceneGraphComponent>();
    updateCamera();
//
//    double[][] light_psns = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 },
//        { -1, 0, 0 }, { 0, -1, 0 }, { 0, 0, -1 } };
//
//    Vector origin = new Vector(0, 0, 0);
//    for (double[] psn : light_psns) {
//      SceneGraphComponent sgcLight = new SceneGraphComponent();
//      DirectionalLight light = new DirectionalLight();
//      light.setIntensity(1.0);
//      sgcLight.setLight(light);
//
//      MatrixBuilder m = View.lookAt(new Vector(psn), origin);
//      m.assignTo(sgcLight);
//
//      sgcDevelopment.addChild(sgcLight);
//    }

    
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.5);
    sgcLight.setLight(light);
    sgcCamera.addChild(sgcLight);
    
    Camera cam = sgcCamera.getCamera();    
    System.out.println("Focal Length:" + cam.getFocalLength());
    System.out.println("Field of View:" + cam.getFieldOfView());
  }

  /*********************************************************************************
   * updateCamera
   * 
   * TODO: Implement this method, so that the camera can either hover over a
   * fixed point or take a more "global view" of the embedded surface.
   *********************************************************************************/
  protected void updateCamera() {
    ManifoldPosition pos = development.getSource();
    Vector embPsn = EmbeddedTriangulation.getCoord3D(pos.getFace(),
        pos.getPosition());

    Vector forward, left, normal;
    forward = EmbeddedTriangulation.embedVector(pos.getFace(),
        pos.getDirectionForward());
    left = EmbeddedTriangulation.embedVector(pos.getFace(),
        pos.getDirectionLeft());
    normal = EmbeddedTriangulation.getEmbeddedNormal(pos.getFace());

    forward.normalize();
    left.normalize();
    normal.normalize();
 
    //la
    Matrix rot = MatrixBuilder.euclidean()
//        .rotate(-Math.PI / 4, forward.getVectorAsArray())
        .rotate(-Math.PI / 8, left.getVectorAsArray())
        .getMatrix();

    Vector adjustedNormal = new Vector(rot.multiplyVector(normal
        .getVectorAsArray()));
    Vector adjustedForward = new Vector(rot.multiplyVector(forward
        .getVectorAsArray()));
    Vector adjustedLeft = new Vector(rot.multiplyVector(left
        .getVectorAsArray()));
//    Vector adjustedNormal = new Vector(normal
//        .getVectorAsArray());
//    Vector adjustedForward = new Vector(forward
//        .getVectorAsArray());
//    Vector adjustedLeft = new Vector(left
//        .getVectorAsArray());

//    System.out.println(adjustedLeft);
//    System.out.println(adjustedForward);
//    System.out.println(adjustedNormal);
    
    double matrix[] = new double[16];

    matrix[0 * 4 + 0] = adjustedForward.getComponent(0);
    matrix[0 * 4 + 1] = adjustedLeft.getComponent(0);
    matrix[0 * 4 + 2] = adjustedNormal.getComponent(0);
    matrix[0 * 4 + 3] = 0.0;

    matrix[1 * 4 + 0] = adjustedForward.getComponent(1);
    matrix[1 * 4 + 1] = adjustedLeft.getComponent(1);
    matrix[1 * 4 + 2] = adjustedNormal.getComponent(1);
    matrix[1 * 4 + 3] = 0.0;

    matrix[2 * 4 + 0] = adjustedForward.getComponent(2);
    matrix[2 * 4 + 1] = adjustedLeft.getComponent(2);
    matrix[2 * 4 + 2] = adjustedNormal.getComponent(2);
    matrix[2 * 4 + 3] = 0.0;

    matrix[3 * 4 + 0] = 0.0;
    matrix[3 * 4 + 1] = 0.0;
    matrix[3 * 4 + 2] = 0.0;
    matrix[3 * 4 + 3] = 1.0;
    
    /*Scaling the adjustedNormal vector by the amount "zoom" has the effect of translating the camera
     * in space. This feature is used to create the illusion that one is zooming in or out on the surface.
     */

    adjustedNormal.scale(zoom);

    MatrixBuilder.euclidean()
        .translate(adjustedNormal.getVectorAsArray())
        .translate(embPsn.getVectorAsArray())
        .times(matrix)
        .rotateZ(-Math.PI / 2)
        .assignTo(sgcCamera);
  }

  /*********************************************************************************
   * initializeNewManifold
   * 
   * See the documentation in View.
   *********************************************************************************/
  public void initializeNewManifold() {
    for (SceneGraphComponent sgc : sgcpools.values()) {
      sgcMarkers.removeChild(sgc);
    }
    sgcpools.clear();

    int[][] ifsf_faces = new int[][] { { 0, 1, 2 } };
    
    faceSGCs = new HashMap<Face, SceneGraphComponent>();

    for (Face f : Triangulation.faceTable.values()) {
      double[][] verts = EmbeddedTriangulation.getFaceGeometry(f);
      double[][] texCoords = TextureCoords.getCoordsAsArray(f);

      IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
      ifsf.setVertexCount(verts.length);
      ifsf.setVertexCoordinates(verts);
      ifsf.setFaceCount(ifsf_faces.length);
      ifsf.setFaceIndices(ifsf_faces);
      ifsf.setGenerateEdgesFromFaces(true);
      ifsf.setGenerateFaceNormals(true);
      ifsf.setVertexAttribute(Attribute.TEXTURE_COORDINATES, texCoords);
      if (showFaceLabels) {
			String[] faceLabels = { f.getIndex() + "" };
			ifsf.setFaceLabels(faceLabels);
	  }
      ifsf.update();
      Appearance app;
      
      if (showTexture) {
    	if(f.hasAppearance()){
    		app = f.getAppearance();
    	} else {    	  
    		TextureDescriptor td = FaceAppearanceScheme.getTextureDescriptor(f);
    		app = TextureLibrary.getAppearance(td);
    	}
      } else
        app = TextureLibrary.getAppearance(FaceAppearanceScheme.getColor(f));

      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(ifsf.getGeometry());
      sgc.setAppearance(app);
      sgcDevelopment.addChild(sgc);
      faceSGCs.put(f, sgc);
    }
    updateCamera();
  }

  /*********************************************************************************
   * generateManifoldGeometry
   * 
   * See the documentation in View. In this particular view, we don't ever need
   * to change the (computer-graphics related) geometry of the polygons we use
   * to represent the surface, so this method is empty.
   *********************************************************************************/
  protected void generateManifoldGeometry() {
  }

  /*********************************************************************************
   * generateMarkerGeometry
   * 
   * See the documentation in View. In this particular view, we need to position
   * and orient each marker in R^3, based upon its coordinates on the surface.
   *********************************************************************************/
  protected void generateMarkerGeometry() {
    HashMap<Marker, Vector[]> objectImages = new HashMap<Marker, Vector[]>();

    // get objects and paths for each face
    HashMap<Integer, Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for (Integer i : faceIndices) {
      Face f = faceTable.get(i);
      getMarkerPlacementData(f, objectImages);
    }
    Set<Marker> allMarkers = markers.getAllMarkers();

    for (Marker vo : allMarkers) {
      SceneGraphComponent sgc = sgcpools.get(vo);

      if (sgc == null) {
        MarkerAppearance oa = vo.getAppearance();
        sgc = oa.makeSceneGraphComponent();
        sgcpools.put(vo, sgc);
        sgcMarkers.addChild(sgc);
      }

      Vector[] tuple = objectImages.get(vo);
      if (tuple == null) {
        sgcpools.get(vo).setVisible(false);
        continue;
      }

      Vector pos = tuple[0];
      Vector forward = Vector.normalize(tuple[1]);
      Vector left = Vector.normalize(tuple[2]);
      Vector normal = Vector.normalize(tuple[3]);

      double[] matrix = new double[16];

      matrix[0 * 4 + 0] = forward.getComponent(0);
      matrix[0 * 4 + 1] = left.getComponent(0);
      matrix[0 * 4 + 2] = normal.getComponent(0);
      matrix[0 * 4 + 3] = 0.0;

      matrix[1 * 4 + 0] = forward.getComponent(1);
      matrix[1 * 4 + 1] = left.getComponent(1);
      matrix[1 * 4 + 2] = normal.getComponent(1);
      matrix[1 * 4 + 3] = 0.0;

      matrix[2 * 4 + 0] = forward.getComponent(2);
      matrix[2 * 4 + 1] = left.getComponent(2);
      matrix[2 * 4 + 2] = normal.getComponent(2);
      matrix[2 * 4 + 3] = 0.0;

      matrix[3 * 4 + 0] = 0.0;
      matrix[3 * 4 + 1] = 0.0;
      matrix[3 * 4 + 2] = 0.0;
      matrix[3 * 4 + 3] = 1.0;

      MatrixBuilder
          .euclidean()
          .translate(pos.getComponent(0), pos.getComponent(1),
              pos.getComponent(2)).times(matrix)
          .scale(vo.getAppearance().getScale()).assignTo(sgc);
      sgc.setVisible(true);
    }
  }

  /*********************************************************************************
   * getMarkerPlacementData
   * 
   * This helper method determines, for each marker, its coordinates and
   * orientation in R^3 based upon its coordinates and orientation on the 2D
   * surface.
   *********************************************************************************/
  private void getMarkerPlacementData(Face f,
      HashMap<Marker, Vector[]> markerImages) {

    Collection<Marker> markers = this.markers.getMarkers(f);

    synchronized (markers) {
      for (Marker m : markers) {
        if (!m.isVisible())
          continue;

        Vector[] tuple = new Vector[4];

        ManifoldPosition pos = m.getPosition();
        tuple[0] = EmbeddedTriangulation.getCoord3D(f, pos.getPosition());
        tuple[1] = EmbeddedTriangulation.embedVector(f,
            pos.getDirectionForward());
        tuple[2] = EmbeddedTriangulation.embedVector(f, pos.getDirectionLeft());
        tuple[3] = EmbeddedTriangulation.getEmbeddedNormal(f);

        markerImages.put(m, tuple);
      }
    }
  }

  @Override
  public void removeMarker(Marker m) {
    SceneGraphComponent sgc = sgcpools.get(m);
    sgcMarkers.removeChild(sgc);
  }

  /*********************************************************************************
   * setTexture
   * 
   * This method is overridden from View, because in the case of an EmbeddedView
   * we cannot simply "redevelop" the manifold to force an appearance change.
   *********************************************************************************/
  public void setDrawTextures(boolean texture) {
    showTexture = texture;
    
    int[][] ifsf_faces = new int[][] { { 0, 1, 2 } };
    faceSGCs = new HashMap<Face, SceneGraphComponent>();
    
    for (Face f : Triangulation.faceTable.values()) {
      double[][] verts = EmbeddedTriangulation.getFaceGeometry(f);
      double[][] texCoords = TextureCoords.getCoordsAsArray(f);

      IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
      ifsf.setVertexCount(verts.length);
      ifsf.setVertexCoordinates(verts);
      ifsf.setFaceCount(ifsf_faces.length);
      ifsf.setFaceIndices(ifsf_faces);
      ifsf.setGenerateEdgesFromFaces(true);
      ifsf.setGenerateFaceNormals(true);
      ifsf.setVertexAttribute(Attribute.TEXTURE_COORDINATES, texCoords);
      if (showFaceLabels) {
			String[] faceLabels = { f.getIndex() + "" };
			ifsf.setFaceLabels(faceLabels);
	  }
      ifsf.update();
      Appearance app;
      if (showTexture) {
        TextureDescriptor td = FaceAppearanceScheme.getTextureDescriptor(f);
        app = TextureLibrary.getAppearance(td);
      } else
        app = TextureLibrary.getAppearance(FaceAppearanceScheme.getColor(f));

      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(ifsf.getGeometry());
      sgc.setAppearance(app);
      sgcDevelopment.addChild(sgc);
      faceSGCs.put(f, sgc);
    }
    super.update(null,null);
  }
  
public void setLabelFaces(boolean drawFaceLabels) {
	// showTexture = texture;

	int[][] ifsf_faces = new int[][] { { 0, 1, 2 } };
	faceSGCs = new HashMap<Face, SceneGraphComponent>();
	for (Face f : Triangulation.faceTable.values()) {
		double[][] verts = EmbeddedTriangulation.getFaceGeometry(f);
		double[][] texCoords = TextureCoords.getCoordsAsArray(f);

		IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
		ifsf.setVertexCount(verts.length);
		ifsf.setVertexCoordinates(verts);
		ifsf.setFaceCount(ifsf_faces.length);
		ifsf.setFaceIndices(ifsf_faces);
		ifsf.setGenerateEdgesFromFaces(true);
		ifsf.setGenerateFaceNormals(true);
		ifsf.setVertexAttribute(Attribute.TEXTURE_COORDINATES, texCoords);
		if (drawFaceLabels) {
			String[] faceLabels = { f.getIndex() + "" };
			ifsf.setFaceLabels(faceLabels);
		}
		ifsf.update();
		
		Appearance app;
		if (showTexture) {
			TextureDescriptor td = FaceAppearanceScheme.getTextureDescriptor(f);
			app = TextureLibrary.getAppearance(td);
		} else
			app = TextureLibrary.getAppearance(FaceAppearanceScheme.getColor(f));

		SceneGraphComponent sgc = new SceneGraphComponent();
		sgc.setGeometry(ifsf.getGeometry());
		sgc.setAppearance(app);
		sgcDevelopment.addChild(sgc);
		faceSGCs.put(f, sgc);
	}
	super.update(null, null);
}

public void update(Observable o, Object arg) {
	// When observing an AnimatedTexture, update each face on the manifold with
    // the current appearance from the AnimatedTexture
    if (o instanceof AnimatedTexture) {
      AnimatedTexture tex = (AnimatedTexture) o;
      for(Face f : faceSGCs.keySet()) {
        faceSGCs.get(f).setAppearance(tex.getCurrentAppearance(f));
      }
    }
	
    // Ensure View.update() is called
	super.update(o, arg);
}
  public void setZoom(double newZoom){
    zoom = newZoom;
  }
}
