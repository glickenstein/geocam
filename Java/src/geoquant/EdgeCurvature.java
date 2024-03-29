package geoquant;

import java.util.HashMap;

import triangulation.Edge;


public class EdgeCurvature extends Geoquant {
  private static HashMap<TriPosition, EdgeCurvature> Index = new HashMap<TriPosition, EdgeCurvature>();
  private SectionalCurvature sc;
  private Length len;
  
  private EdgeCurvature(Edge e) {
    super(e);
    
    sc = SectionalCurvature.at(e);
    sc.addObserver(this);
    len = Length.at(e);
    len.addObserver(this);
  }
  
  protected void recalculate() {
    value = sc.getValue() * len.getValue();
  }

  public void remove() {
    deleteDependents();
    sc.deleteObserver(this);
    len.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static EdgeCurvature at(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    EdgeCurvature q = Index.get(T);
    if(q == null) {
      q = new EdgeCurvature(e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static EdgeCurvature At(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    EdgeCurvature q = Index.get(T);
    if(q == null) {
      q = new EdgeCurvature(e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e) {
    return at(e).getValue();
  }

}
