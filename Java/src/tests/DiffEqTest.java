package tests;

import geo.Curvature2D;
import geo.Eta;
import geo.GeoRecorder;
import geo.Geometry;
import geo.Geoquant;
import geo.Radius;
import inputOutput.TriangulationIO;

import java.util.LinkedList;
import java.util.List;

import triangulation.Triangulation;

import Solvers.DESystem;
import Solvers.EulerSolver;
import Solvers.Yamabe2DFlow;

public class DiffEqTest {

  public static void main(String[] args) {
    initializeQuantities();
    testYamabe2DFlow();

  }
  
  private static void initializeQuantities() {
    TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/tetrahedron.xml");
    for(Radius r : Geometry.getRadii()) {
      r.setValue(1.0);
    }
    for(Eta e : Geometry.getEtas()) {
      e.setValue(1.0);
    }
  }

  private static void testYamabe2DFlow() {
    DESystem sys = new Yamabe2DFlow();
    EulerSolver solver = new EulerSolver(sys);
    
    List<Class<? extends Geoquant>> list = new LinkedList<Class<? extends Geoquant>>();
    list.add(Radius.class);
    list.add(Curvature2D.class);
    GeoRecorder rec = new GeoRecorder(list);
    solver.addObserver(rec);
    double[] radii = new double[Triangulation.vertexTable.size()];
    int i = 0;
    for(Radius r : Geometry.getRadii()) {
      radii[i] = r.getValue() + i/5.0;
      i++;
    }
    
    radii = solver.run(radii, 0.1, 50);
    
    System.out.println("RADII:");
    for(List<Double> values : rec.getValueHistory(Radius.class)) {
      System.out.println(values);
    }
    
    System.out.println("CURVATURES:");
    for(List<Double> values : rec.getValueHistory(Curvature2D.class)) {
      System.out.println(values);
    }
  }
}
