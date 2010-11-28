package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import triangulation.Face;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import development.Development;
import development.Development.DevelopmentNode;
import development.EmbeddedFace;
import development.Frustum2D;
import development.StopWatch;
import development.Vector;

public class SGCTree {
  private SGCNode root;
  private ColorScheme colorScheme;
  private int dimension;
  private final double simulated3DHeight = 0.08;
  private Vector sourcePoint;
  private SceneGraphComponent objects = new SceneGraphComponent();

  public SGCTree(Development d, ColorScheme scheme, int dim) {
    sourcePoint = d.getSourcePoint();
    dimension = dim;
    colorScheme = scheme;
    double[][] faceVerts = vertsFromFace(d.getRoot().getEmbeddedFace());
    root = new SGCNode(d.getRoot(), faceVerts, dimension);
    buildTree(root, d.getRoot());
    setVisibleDepth(d.getDesiredDepth());
  }
  
  public SceneGraphComponent getObjects() {
    return objects;
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    changeColors(root);
  }

  private void changeColors(SGCNode node) {
    node.updateColor();
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      changeColors(itr.next());
    }
  }

  public void setVisibleDepth(int depth) {
    setVisibility(root, depth);
  }

  private void setVisibility(SGCNode node, int depth) {
    if (node.getDepth() <= depth)
      node.getSGC().setVisible(true);
    else
      node.getSGC().setVisible(false);
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      setVisibility(itr.next(), depth);
    }
  }

  private void buildTree(SGCNode parent, DevelopmentNode node) {
    SGCNode newNode = new SGCNode(node, vertsFromFace(node.getEmbeddedFace()), dimension);
    parent.addChild(newNode);
    Iterator<DevelopmentNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      buildTree(newNode, itr.next());
    }
  }
  
  public Geometry getGeometry() {
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    ArrayList<Color> colors = new ArrayList<Color>();
    computeDevelopment(root, colors, geometry);
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

    Color[] colorList = new Color[colors.size()];
    for (int i = 0; i < colors.size(); i++) {
      colorList[i] = colors.get(i);
    }

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();

    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setFaceColors(colorList);
    ifsf.update();
    return ifsf.getGeometry();
  }
  
  private void computeDevelopment(SGCNode node, ArrayList<Color> colors,
      DevelopmentGeometry geometry) {
    if(!node.getSGC().isVisible()) return;
    double[][] face = node.getVertices();
    geometry.addFace(face);
    colors.add(node.getColor());
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      computeDevelopment(itr.next(), colors, geometry);
    }
  }

  // class designed to make it easy to use an IndexedFaceSetFactory
  public class DevelopmentGeometry {

    private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
    private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();

    public void addFace(double[][] faceverts) {

      int nverts = faceverts.length;
      int vi = geometry_verts.size();

      int[] newface = new int[nverts];
      for (int k = 0; k < nverts; k++) {
        double[] newvert = new double[3];
        newvert[0] = faceverts[k][0];
        newvert[1] = faceverts[k][1];
        newvert[2] = 1.0;
        geometry_verts.add(newvert);
        newface[k] = vi++;
      }
      geometry_faces.add(newface);
    }

    public double[][] getVerts() {
      return (double[][]) geometry_verts.toArray(new double[0][0]);
    }

    public int[][] getFaces() {
      return (int[][]) geometry_faces.toArray(new int[0][0]);
    }
  };

  public SGCNode getRoot() {
    return root;
  }
  
  private double[][] vertsFromFace(EmbeddedFace face) {
    double[][] verts = new double[face.getNumberVertices()][3];
    for(int i = 0; i < face.getNumberVertices(); i++) {
      verts[i] = face.getVectorAt(i).getVectorAsArray();
    }
    return verts;
  }
  
  

///////////////////////////////////////////////
// SGCNode
///////////////////////////////////////////////
  public class SGCNode {
    private SceneGraphComponent sgc;
    private DevelopmentNode node;
    private ArrayList<SGCNode> children;
    private int dimension;
    private double[][] verts;

    public SGCNode(DevelopmentNode n, double[][] vertList, int dim) {
      verts = vertList;
      dimension = dim;
      node = n;
      sgc = new SceneGraphComponent();
      StopWatch s = new StopWatch();
      s.start();
      Vector transSourcePoint2d = null;
      Vector transSourcePoint = null;
      if (node.faceIsSource()) {
        Vector newSource = new Vector(sourcePoint.getComponent(0),
            sourcePoint.getComponent(1), 1);
        transSourcePoint = node.getAffineTransformation()
            .transformVector(newSource);
        transSourcePoint2d = new Vector(transSourcePoint.getComponent(0),
            transSourcePoint.getComponent(1));
      }

      if (dimension == 3) {
        sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
            colorScheme.getColor(node), simulated3DHeight));
        sgc.setAppearance(SGCMethods.getFaceAppearance(0.5f));

        if (node.faceIsSource()
            && contains(node.getEmbeddedFace(), transSourcePoint2d)) {
          objects.addChild(SGCMethods.sgcFromPoint(transSourcePoint2d));
        }

      } else if (dimension == 2) {
        sgc.setGeometry(node.getEmbeddedFace().getGeometry(
            colorScheme.getColor(node)));
        sgc.setAppearance(SGCMethods.getFaceAppearance(0.5f));
        if (node.faceIsSource()
            && contains(node.getEmbeddedFace(), transSourcePoint2d)) {
          objects.addChild(SGCMethods.sgcFromPoint(transSourcePoint));
        } else if (node.isRoot()) {// containment algorithm doesn't work for
                                   // root face
          objects.addChild(SGCMethods.sgcFromPoint(transSourcePoint));
        }
      }
      children = new ArrayList<SGCNode>();
      s.stop();
      //System.out.println("time to build SGCNode: " + s.getElapsedTime());
    }

    public int getDepth() {
      return node.getDepth();
    }

    public void updateColor() {
      if (dimension == 3)
        sgc.setGeometry(node.getEmbeddedFace().getGeometry3D(
            colorScheme.getColor(node), simulated3DHeight));
      else
        sgc.setGeometry(node.getEmbeddedFace().getGeometry(
            colorScheme.getColor(node)));
    }

    public void addChild(SGCNode node) {
      children.add(node);
    }

    public Face getFace() {
      return node.getFace();
    }

    public ArrayList<SGCNode> getChildren() {
      return children;
    }

    public SceneGraphComponent getSGC() {
      return sgc;
    }
    
    public double[][] getVertices() {
      return verts;
    }
    
    public Color getColor() {
      return colorScheme.getColor(node);
    }

    private boolean contains(EmbeddedFace face, Vector point) {
      List<Vector> vertices = face.getVectors();
      for (int i = 0; i < vertices.size(); i++) {
        Vector v1 = vertices.get(i);
        Vector v2 = vertices.get((i + 1) % vertices.size());
        Frustum2D frustum = new Frustum2D(v1, v2);
        if (frustum.checkInterior(point))
          return true;
      }
      return false;
    }
  }
}
