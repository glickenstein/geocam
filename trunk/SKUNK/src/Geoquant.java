import java.util.ArrayList;
import java.util.Iterator;


public abstract class Geoquant {
  protected ArrayList<Geoquant> dependents;
  protected double value;
  private boolean valid;
	
  protected abstract void recalculate();
  protected abstract void remove();
	
  private Geoquant() {
    dependents = new ArrayList<Geoquant>();
    valid = false;
  }
  

  protected void addDependent(Geoquant q) {
    dependents.add(q);
  }
  
  protected void removeDependent(Geoquant q) {
    dependents.remove(q);
  }
  
  public double getValue() {
    if(! valid){
      recalculate();
      valid = true;
    }
    return value;		
  }
  
  public void setValue(double val) {
    value = val; 
    invalidate();
    valid = true;
  }
  
  private void invalidate() {
    if(valid){ 
      valid = false;
      notifyDependents();
    }
  }
  
  private void notifyDependents() {
    Iterator<Geoquant> it = dependents.iterator();
    while(it.hasNext()) {
      it.next().invalidate();
    }
  }
}
