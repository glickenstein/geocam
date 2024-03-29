package tests;

import geoquant.Alpha;
import geoquant.Angle;
import geoquant.Area;
import geoquant.ConeAngle;
import geoquant.Curvature2D;
import geoquant.Curvature3D;
import geoquant.DihedralAngle;
import geoquant.DualArea;
import geoquant.EdgeHeight;
import geoquant.Eta;
import geoquant.FaceHeight;
import geoquant.Geometry;
import geoquant.LCSC;
import geoquant.LEHR;
import geoquant.LEinstein;
import geoquant.Length;
import geoquant.PartialEdge;
import geoquant.Radius;
import geoquant.SectionalCurvature;
import geoquant.VCSC;
import geoquant.VEHR;
import geoquant.VEinstein;
import geoquant.Volume;
import inputOutput.TriangulationIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Tetra;
import triangulation.Triangulation;
import triangulation.Vertex;


public class GeoquantTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    initializeQuantities();
   
    testLengths();
    testRadii();
    testAlpha();
    testEta();
    testAngle();
    testArea();
    testConeAngle();
    testCurvature2D();
    testCurvature3D();
    testDihedralAngle();
    testDualArea();
    testEdgeHeight();
    testFaceHeight();
    testSectionalCurvature();
    testVolume();
    testPartialEdge();
    testNEHR();
    testVolumePartial();
    testVolumeSecondPartial();
    testDihedralAnglePartial();
    testDihedralAngleSecondPartial();
    testPartialEdgePartial();
    testPartialEdgeSecondPartial();
    testCurvature3DPartial();
    testCurvature3DSecondPartial();
   // testRadiiPartial();
    testNEHRPartial();
    testNEHRSecondPartial();
    testTotalVolumeSecondPartial();
    testTotalVolumePartial();
    
    testLCSC();
    testVCSC();
    testLEinstein();
    testVEinstein();
    testLEHR();
  }

  private static void initializeQuantities() {
    //TriangulationIO.readTriangulation("Data/Conversion/VertexTransitiveTriangulations/3_manifolds/manifold_3_10_1_1.xml");
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_regular.xml");
  }
  
  private static void testLengths() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Lengths.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    for(Length l : Geometry.getLengths()) {
      out.println(l);
    }
    out.close();
    System.out.println("Done with Lengths.");
  }

  private static void testRadii() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Radii.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    for (Radius r : Geometry.getRadii()){
      out.println(r);
    }
    out.close();
    System.out.println("Done with Radii.");
  }
  private static void testAlpha() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Alphas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Alpha al;
    for (Vertex v: Triangulation.vertexTable.values()){
      al = Alpha. at(v);
      out.println(al);
    }
    out.close();
    System.out.println("Done with Alphas.");
  }
  private static void testEta() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Etas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Eta et;
    for (Edge e : Triangulation.edgeTable.values()){
      et = Eta.at(e);
      out.println(et);
    }
    out.close();
    System.out.println("Done with Etas.");
  }
  private static void testAngle() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Angles.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Angle a;
    for (Face f : Triangulation.faceTable.values()){
      for (Vertex v : f.getLocalVertices()) {
        a = Angle.at(v, f);
        out.println(a);
      }
    }
    out.close();
    System.out.println("Done with Angles.");
  }
  private static void testArea() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Areas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Area ar;
    for (Face f : Triangulation.faceTable.values()){
      ar = Area.at(f);
      out.println(ar);
    }
    out.close();
    System.out.println("Done with Areas.");
  }
  private static void testConeAngle() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/ConeAngles.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    ConeAngle c;
    for (Edge e : Triangulation.edgeTable.values()){
      c = ConeAngle.at(e);
      out.println(c);
    }
    out.close();
    System.out.println("Done with ConeAngles.");
  }
  private static void testCurvature2D() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature2D.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature2D cu2;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu2 = Curvature2D.at(v);
      out.println(cu2);
    }
    out.close();
    System.out.println("Done with Curvature2Ds.");
  }
  private static void testCurvature3D() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature3D.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature3D cu3;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu3 = Curvature3D.at(v);
      out.println(cu3);
    }
    out.close();
    System.out.println("Done with Curvature3Ds.");
  }
  private static void testDihedralAngle() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DihedralAngles.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DihedralAngle d;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
      d = DihedralAngle.at(e, t);
      out.println(d);
    }
   }

    out.close();
    System.out.println("Done with DihedralAngles.");
  }
  private static void testDualArea() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DualAreas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DualArea du;
    for (Edge e : Triangulation.edgeTable.values()){
      du = DualArea.at(e);
      out.println(du);
    }
    out.close();
    System.out.println("Done with DualAreas.");
  }
  
  private static void testEdgeHeight() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/EdgeHeights.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    EdgeHeight eh;
    for (Face f : Triangulation.faceTable.values()){
      for (Edge e : f.getLocalEdges()) {
        eh = EdgeHeight.at(e, f);
        out.println(eh);
      }
    }
    out.close();
    System.out.println("Done with EdgeHeights.");
  }
  private static void testFaceHeight() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/FaceHeights.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    FaceHeight fh;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Face f : t.getLocalFaces()) {
        fh = FaceHeight.at(f, t);
        out.println(fh);
      }
    }
    out.close();
    System.out.println("Done with FaceHeights.");
  }
  private static void testSectionalCurvature(){
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/SectionalCurvatures.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }    
    SectionalCurvature sc;
    for (Edge e : Triangulation.edgeTable.values()){
      sc = SectionalCurvature.at(e);
      out.println(sc);
    }
    out.close();
    System.out.println("Done with SectionalCurvatures.");
  }
  private static void testVolume() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Volumes.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Volume vol;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.at(t);
      out.println(vol);
    }
    out.close();
    System.out.println("Done with Volumes.");
  }
  private static void testPartialEdge() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/PartialEdges.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    PartialEdge pe;
    for (Edge edge : Triangulation.edgeTable.values()){
      for (Vertex vertex : edge.getLocalVertices()) {
        pe = PartialEdge.at(vertex, edge);
        out.println(pe);
      }
    }
    out.close();
    System.out.println("Done with PartialEdges.");
  }
  
  private static void testNEHR() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/NEHR.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    out.println(VEHR.getInstance());
    out.close();
    System.out.println("Done with NEHR.");
  }
  
  private static void testVolumePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Volume_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Volume vol;
    Volume.Partial partial;
    for(Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.at(t);
      for (Vertex v : t.getLocalVertices()){
       partial = vol.partialAt(v); 
       out.println(partial);
      }
      for (Edge e : t.getLocalEdges()){
        partial = vol.partialAt(e);
        out.println(partial);
      }
    }
    out.close();
    System.out.println("Done with VolumePartials.");
  }
  private static void testVolumeSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Volume_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Volume vol;
    Volume.SecondPartial secondPartial;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.at(t);
      for (Vertex v : t.getLocalVertices()){
        for (Vertex w : t.getLocalVertices()){
          secondPartial = vol.secondPartialAt(v, w);
          out.println(secondPartial);
        }
        for (Edge e : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(v, e);
          out.println(secondPartial);
        }
      }
      for (Edge e : t.getLocalEdges()){
        for (Edge f : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(e, f);
          out.println(secondPartial);
        }
      }
    }
    out.close();
    System.out.println("Done with VolumeSecondPartials.");
  }
  private static void testDihedralAnglePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DihedralAngle_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DihedralAngle d;
    DihedralAngle.Partial partial;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
        d = DihedralAngle.at(e, t);
        for (Edge f : Triangulation.edgeTable.values()) {
          partial = d.partialAt(f);
          out.println(partial);
       }
     }
   }
    out.close();
    System.out.println("Done with DihedralAnglePartials.");
  }
  private static void testDihedralAngleSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DihedralAngle_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DihedralAngle d;
    DihedralAngle.SecondPartial secondPartial;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
        d = DihedralAngle.at(e, t);
        for (Edge nm : t.getLocalEdges()) {
          for (Edge op : t.getLocalEdges()) {
            secondPartial = d.secondPartialAt(nm, op);
            out.println(secondPartial);
          }
        }
      }
    }
    out.close();
    System.out.println("Done with DihedralAngleSecondPartials.");
  }
  

  private static void testPartialEdgePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/PartialEdge_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    PartialEdge pe;
    PartialEdge.Partial partial;
    for(Edge e : Triangulation.edgeTable.values()) {
      for(Vertex v : e.getLocalVertices()) {
        pe = PartialEdge.at(v, e);
        for(Edge f: Triangulation.edgeTable.values()) {
          partial = pe.partialAt(f);
          out.println(partial);
        }
      }
    }
    out.close();
    System.out.println("Done with PartialEdgePartials.");
  }
  
  private static void testPartialEdgeSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/PartialEdge_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    PartialEdge pe;
    PartialEdge.SecondPartial secondPartial;
    for(Edge e : Triangulation.edgeTable.values()) {
      for(Vertex v : e.getLocalVertices()) {
        pe = PartialEdge.at(v, e);
        secondPartial = pe.secondPartialAt(e, e);
        out.println(secondPartial);
      }
    }
    out.close();
    System.out.println("Done with PartialEdgeSecondPartials.");
  }
  
  private static void testCurvature3DPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature3D_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature3D c;
    Curvature3D.Partial partial;
    for (Vertex v : Triangulation.vertexTable.values()){
      c = Curvature3D.at(v);
      for (Vertex w : Triangulation.vertexTable.values()) {
        partial = c.partialAt(w);
        out.println(partial);
      }
      for (Edge e : Triangulation.edgeTable.values()) {
        partial = c.partialAt(e);
        out.println(partial);
      }
    }
    out.close();
    System.out.println("Done with CurvaturePartials.");
  }
  private static void testCurvature3DSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature3D_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature3D c;
    Curvature3D.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      c = Curvature3D.at(v);
      for(Edge e : Triangulation.edgeTable.values()) {
        for(Edge f : Triangulation.edgeTable.values()) {
          secondPartial = c.secondPartialAt(e, f);
          out.println(secondPartial);
        }
      }
    }
    out.close();
    System.out.println("Done with CurvatureSecondPartials.");
  }
  private static void testRadiiPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Radius_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    Radius r;
    Radius.Partial partial;
    for (Vertex v : Triangulation.vertexTable.values()){
      r = Radius.at(v);
      for (Edge e : Triangulation.edgeTable.values()){
        partial = r.partialAt(e);
        out.println(partial);
      }
    }
    out.close();
    System.out.println("Done with RadiiPartials.");
  }
  private static void testNEHRPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/NEHR_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    VEHR.Partial partial;
    for (Vertex  v : Triangulation.vertexTable.values()) {
      partial = VEHR.partialAt(v);
      out.println(partial);
    }
    for (Edge e : Triangulation.edgeTable.values()) {
      partial = VEHR.partialAt(e);
      out.println(partial);
    }
    out.close();
    System.out.println("Done with NEHRPartials.");
  }
  
  private static void testNEHRSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/NEHR_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    VEHR.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      for (Vertex w : Triangulation.vertexTable.values()) {
        secondPartial = VEHR.secondPartialAt(v, w);
        out.println(secondPartial);
      }
      for (Edge e : Triangulation.edgeTable.values()) {
        secondPartial = VEHR.secondPartialAt(v, e);
        out.println(secondPartial);
      }
    }
   for (Edge e : Triangulation.edgeTable.values()) {
     for (Edge f : Triangulation.edgeTable.values()) { 
       secondPartial = VEHR.secondPartialAt(e, f);
       out.println(secondPartial);
     }
   }
   out.close();
   System.out.println("Done with NEHRSecondPartials.");
  }
  
  private static void testTotalVolumeSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/TotalVolume_SecondPartial.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(Volume.SecondPartialSum p : Geometry.getVolumeSecondPartialSums()) {
      out.println(p);
    }
    out.close();
    System.out.println("Done with TotalVolumeSecondPartials.");
  }
  
  private static void testTotalVolumePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/TotalVolume_Partial.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(Volume.PartialSum p : Geometry.getVolumePartialSums()) {
      out.println(p);
    }
    out.close();
    System.out.println("Done with TotalVolumePartials.");
  }
  
  private static void testLEHR() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/LEHR.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    out.println(LEHR.getInstance());
    out.close();
  }

  private static void testVEinstein() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/VEinstein.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(LEinstein le : Geometry.getLEinsteins()) {
      out.println(le);
    }
    out.close(); 
  }

  private static void testLEinstein() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/LEinstein.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(VEinstein ve : Geometry.getVEinsteins()) {
      out.println(ve);
    }
    out.close();
  }

  private static void testVCSC() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/VCSC.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(VCSC vcsc : Geometry.getVCSC()) {
      out.println(vcsc);
    }
    out.close(); 
  }

  private static void testLCSC() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/LCSC.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(LCSC lcsc : Geometry.getLCSC()) {
      out.println(lcsc);
    }
    
    out.close();
  }
  
}
