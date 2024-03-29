package marker;

import static de.jreality.shader.CommonAttributes.DIFFUSE_COLOR;
import static de.jreality.shader.CommonAttributes.EDGE_DRAW;
import static de.jreality.shader.CommonAttributes.FACE_DRAW;
import static de.jreality.shader.CommonAttributes.LIGHTING_ENABLED;
import static de.jreality.shader.CommonAttributes.LINE_SHADER;
import static de.jreality.shader.CommonAttributes.PICKABLE;
import static de.jreality.shader.CommonAttributes.POINT_RADIUS;
import static de.jreality.shader.CommonAttributes.POINT_SHADER;
import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;
import static de.jreality.shader.CommonAttributes.SMOOTH_SHADING;
import static de.jreality.shader.CommonAttributes.SPECULAR_COEFFICIENT;
import static de.jreality.shader.CommonAttributes.TRANSPARENCY_ENABLED;
import static de.jreality.shader.CommonAttributes.TUBE_RADIUS;
import static de.jreality.shader.CommonAttributes.VERTEX_DRAW;

import java.awt.Color;
import java.awt.Transparency;
import java.io.File;
import java.util.EnumMap;

import util.AssetManager;
import de.jreality.geometry.GeometryMergeFactory;
import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import de.jreality.shader.CommonAttributes;
import de.jreality.util.SceneGraphUtility;

/*********************************************************************************
 * MarkerAppearance
 * 
 * This class is responsible for loading and organizing the geometric (mesh)
 * data needed to represent markers in the simulation. In normal use, a marker
 * will be modeled in blender, then exported as a 3DS file. Since one marker
 * model might be used in several places, this class maintains a static mapping
 * of model types to scene graphs obtained by reading in 3DS files. This way, we
 * can simply copy the "template scene graph" rather than reading that data from
 * disk every time we need a new scene graph component for a marker.
 * 
 * In addition, a marker appearance object allows us to keep track of appearance
 * data specific to a particular model. For example, one can specify a
 * particular size (scaling), so that a given scene may have many ants of
 * different sizes.
 *********************************************************************************/

public class MarkerAppearance {
	/*********************************************************************************
	 * This enumeration provides a succinct description of which marker model
	 * this appearance represents.
	 *********************************************************************************/
	public static enum ModelType {
		ANT, APPLE, COOKIE, ROCKET, SATTELITE, CUBE, SPHERE, ARROWHEAD, ARROWBODY, LADYBUG, FLAG, CAR, TEXT
	};

	/*********************************************************************************
	 * This EnumMap and static code are responsible for maintaining a mapping
	 * from ModelTypes to template scene graph components.
	 *********************************************************************************/
	private static EnumMap<ModelType, SceneGraphComponent> templateSGCs;

	private static SceneGraphComponent loadSGC(String filename) {
		File ff = AssetManager.getAssetFile("marker/" + filename);
		return loadTemplateSGC(ff);
	}

	static {
		templateSGCs = new EnumMap<ModelType, SceneGraphComponent>(ModelType.class);
		templateSGCs.put(ModelType.ANT, loadSGC("ant.3ds"));
		templateSGCs.put(ModelType.APPLE, loadSGC("apple.3ds"));
		templateSGCs.put(ModelType.COOKIE, loadSGC("cookie.3ds"));
		templateSGCs.put(ModelType.ROCKET, loadSGC("rocket.3ds"));
		templateSGCs.put(ModelType.SATTELITE, loadSGC("sattelite.3ds"));
		templateSGCs.put(ModelType.CUBE, loadSGC("cube.3ds"));
		templateSGCs.put(ModelType.LADYBUG, loadSGC("ladybug.3ds"));
		templateSGCs.put(ModelType.CAR, loadSGC("car.3ds"));

		SceneGraphComponent sgc;
		sgc = Primitives.sphere(1.0, new double[] { 0.0, 0.0, 0.0 });
		templateSGCs.put(ModelType.SPHERE, sgc);

		templateSGCs.put(ModelType.FLAG, loadSGC("x.3ds"));

		sgc = new SceneGraphComponent();
		sgc.setGeometry(Primitives.arrow(0.0, 0.0, 1.0, 0.0, 0.25));
		templateSGCs.put(ModelType.ARROWHEAD, sgc);

		sgc = new SceneGraphComponent();
		sgc.setGeometry(Primitives.arrow(0.0, 0.0, 1.0, 0.0, 0.0));
		templateSGCs.put(ModelType.ARROWBODY, sgc);
	}

	/*********************************************************************************
	 * loadTemplateSGC
	 * 
	 * This method reads a scene graph component from the input file object.
	 * After loading the object, some of the properties of its geometry, like
	 * vertex normals, are calculated. This allows properties that are not
	 * necessarily stored in the mesh file (like smooth shading) to be applied
	 * to the model.
	 * 
	 * Note: Since reading from disk is expensive, this method is only supposed
	 * to be used to initialize the "templateSGCs" map. When a marker model is
	 * needed, we make a copy of one of the templates in this map.
	 *********************************************************************************/
	private static SceneGraphComponent loadTemplateSGC(File ff) {
		SceneGraphComponent sgc = null;

		try {
			sgc = Readers.read(ff);
		} catch (Exception ee) {
			System.err.println("Error: Unable to read model file " + ff);
			ee.printStackTrace();
			System.exit(1);
		}

		GeometryMergeFactory gmf = new GeometryMergeFactory();
		IndexedFaceSet ifs = gmf.mergeIndexedFaceSets(sgc);

		SceneGraphUtility.removeChildren(sgc);
		sgc.setGeometry(ifs);

		return sgc;
	}

	/*********************************************************************************
	 * copySceneGraph
	 * 
	 * This method makes a copy of the input scene graph based at root.
	 * Naturally, this involves recursively copying all scene graph components
	 * below root. Since we only need geometry and appearance attributes, these
	 * are what we copy. Future versions may require us to copy other data.
	 *********************************************************************************/
	private static SceneGraphComponent copySceneGraph(SceneGraphComponent root) {
		SceneGraphComponent sgc = new SceneGraphComponent();

		sgc.setAppearance(root.getAppearance());
		sgc.setGeometry(root.getGeometry());

		int numChildren = root.getChildComponentCount();
		for (int ii = 0; ii < numChildren; ii++) {
			SceneGraphComponent child = root.getChildComponent(ii);
			sgc.addChild(copySceneGraph(child));
		}

		return sgc;
	}

	/*********************************************************************************
	 * Private Data
	 * 
	 * These parameters allow us to set further appearance attributes for
	 * specific models.
	 *********************************************************************************/
	protected double scale = 1.0;
	protected double default_scale = 1.0;
	protected Color color = Color.BLUE;
	protected ModelType model = ModelType.ANT;

	/*********************************************************************************
	 * Constructors
	 * 
	 * Several different constructors are provided below. By default, a marker
	 * will have the ant appearance with scaling factor 1.0.
	 *********************************************************************************/

	public MarkerAppearance(ModelType mt, double scale) {
		this.setModelType(mt);
		this.setSize(scale);

		switch (mt) {
		case CUBE:
		case APPLE:
		case CAR:
			this.setDefaultScale(0.2);
			break;
		default:
			this.setDefaultScale(1.0);
			break;
		}
	}

	public MarkerAppearance(ModelType mt) {
		this.setModelType(mt);
		
		switch (mt) {
		case CUBE:
		case APPLE:
		case CAR:
			this.setSize(0.2);
			this.setDefaultScale(0.2);
			break;
		default:
			this.setSize(1.0);
			this.setDefaultScale(1.0);
			break;
		}
	}

	public MarkerAppearance() {
		this(ModelType.ANT, 1.0);
	}

	public void setModelType(ModelType mt) {
		this.model = mt;
	}

	public ModelType getModelType() {
		return this.model;
	}

	/*********************************************************************************
	 * setDefaultScale / getDefaultScale
	 * 
	 * These methods should be used to scale models so that they have a
	 * particular size relative to the other models in the scene. The setScale
	 * methods exist so users can dynamically resize models however they want,
	 * without losing the "default scale" data, which is chosen to make the
	 * model look reasonable in most scenes.
	 * 
	 *********************************************************************************/
	public void setDefaultScale(double scale) {
		this.default_scale = scale;
	}

	public double getDefaultScale() {
		return default_scale;
	}

	public void setSize(double scale) {
		this.scale = scale;
	}

	public double getScale() {
		return this.scale;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public boolean isTransparent() {
		return (color.getTransparency() == Transparency.TRANSLUCENT);
	}

	/*********************************************************************************
	 * prepareNewSceneGraphComponent
	 * 
	 * This method is the primary method called by users of MarkerAppearance.
	 * Given the appearance data provided to a given instance of
	 * MarkerAppearance, that instance can use this method to create a new scene
	 * graph component (not referenced in any other scene graph) representing
	 * that data. Thus, users of MarkerAppearance are freed from having to load
	 * model meshes, deal with their textures, etc.
	 *********************************************************************************/
	public SceneGraphComponent makeSceneGraphComponent() {
		SceneGraphComponent sgc = new SceneGraphComponent();
		sgc = copySceneGraph(templateSGCs.get(this.model));

		Appearance app = getBasicAppearance();		
		if (this.model == ModelType.ARROWHEAD || this.model == ModelType.ARROWBODY) {
			app.setAttribute(LINE_SHADER + "." + TUBE_RADIUS, .05);
			app.setAttribute(POINT_SHADER + "." + POINT_RADIUS, .05);
		} else if (this.model == ModelType.FLAG) {
			app.setAttribute(LINE_SHADER + "." + DIFFUSE_COLOR, Color.red);
			app.setAttribute(POINT_SHADER + "." + DIFFUSE_COLOR, Color.red);
			app.setAttribute(POLYGON_SHADER + "." + DIFFUSE_COLOR, Color.red);
		}
		sgc.setAppearance(app);
		sgc.setTransformation( getScalingTransformation() );
		sgc.setVisible(true);

		return sgc;
	}

	protected Appearance getBasicAppearance() {
		Appearance app = new Appearance();
		app.setAttribute(VERTEX_DRAW, false);
		app.setAttribute(EDGE_DRAW, false);
		app.setAttribute(FACE_DRAW, true);
		app.setAttribute(TRANSPARENCY_ENABLED, false);
		app.setAttribute(PICKABLE, false);
		app.setAttribute(LIGHTING_ENABLED, true);
		app.setAttribute(POLYGON_SHADER + "." + SMOOTH_SHADING, true);
		app.setAttribute(POLYGON_SHADER + "." + SPECULAR_COEFFICIENT, 0.15);
		app.setAttribute(LINE_SHADER + "." + DIFFUSE_COLOR, this.color);
		app.setAttribute(POINT_SHADER + "." + DIFFUSE_COLOR, this.color);
		app.setAttribute(POLYGON_SHADER + "." + DIFFUSE_COLOR, this.color);
		app.setAttribute(CommonAttributes.ANTIALIASING_ENABLED, true);
		return app;
	}
	
	protected Transformation getScalingTransformation(){
		double[] mat = MatrixBuilder.euclidean().scale(this.scale).getMatrix().getArray();
		return new Transformation(mat);
	}
}
