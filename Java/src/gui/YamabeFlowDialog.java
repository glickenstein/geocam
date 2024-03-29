package gui;

import geoquant.GeoRecorder;
import geoquant.Geometry;
import geoquant.Geometry.Dimension;
import geoquant.Radius;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import solvers.Solver;
import solvers.implemented.Yamabe2DFlow;
import solvers.implemented.Yamabe3DFlow;
import triangulation.Triangulation;



/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class YamabeFlowDialog extends JDialog {
	private JButton cancelButton;
	private JRadioButton numStepsButton;
	private JPanel stoppingCondPanel;
	private ButtonGroup stoppingCondButtonGroup;
	private JTextField precisionTextField;
	private JTextField numStepsTextField;
	private JRadioButton precisionButton;
	private JButton runButton;
	private GeoquantViewer owner;
	private Dimension dim;
	private JLabel stepsizeLabel;
	private JTextField stepsizeTextField;
	private static GeoRecorder rec;
	private int numSteps;

	public YamabeFlowDialog(GeoquantViewer owner, Dimension dim) {
	  super(owner);
	  this.owner = owner;
	  this.dim = dim;
	  rec = new GeoRecorder();
	  initGUI();
	}
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			getContentPane().setLayout(thisLayout);
			{
				cancelButton = new JButton();
				cancelButton.setText("Cancel");
				cancelButton.setAction(new RunAction("Cancel"));
			}
			{
				runButton = new JButton();
				runButton.setText("Run");
				runButton.setAction(new RunAction("Run"));
			}
			{
				stoppingCondPanel = new JPanel();
				GroupLayout jPanel1Layout = new GroupLayout((JComponent)stoppingCondPanel);
				stoppingCondPanel.setLayout(jPanel1Layout);
				stoppingCondPanel.setBorder(BorderFactory.createTitledBorder(null, "Choose Stopping Condition", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
				{
					numStepsTextField = new JTextField();
					numStepsTextField.setText("100");
					numStepsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					numStepsButton = new JRadioButton();
					numStepsButton.setText("Number of Steps");
					numStepsButton.setSelected(true);
					numStepsButton.setActionCommand("NS");
					numStepsButton.addActionListener(new StoppingCondListener());
					getStoppingCondButtonGroup().add(numStepsButton);
				}
				{
					precisionButton = new JRadioButton();
					precisionButton.setText("Precision");
					precisionButton.setActionCommand("P");
					precisionButton.addActionListener(new StoppingCondListener());
					getStoppingCondButtonGroup().add(precisionButton);
				}
				{
					precisionTextField = new JTextField();
					precisionTextField.setText("0.001");
					precisionTextField.setEnabled(false);
					precisionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
		      stepsizeTextField = new JTextField();
		      stepsizeTextField.setText("0.1");
		      stepsizeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
				}
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(stoppingCondPanel, 0, 99, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(cancelButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
				    .addComponent(stepsizeTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
				    .addComponent(runButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
				    .addComponent(getStepsizeLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(15, 15));
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(getStepsizeLabel(), GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				        .addComponent(stepsizeTextField, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
				        .addGap(28)
				        .addComponent(runButton, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE))
				    .addGroup(thisLayout.createSequentialGroup()
				        .addComponent(stoppingCondPanel, GroupLayout.PREFERRED_SIZE, 425, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap(35, Short.MAX_VALUE));
			thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {stepsizeTextField, runButton});
			thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {cancelButton, runButton});
				jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
				.addGroup(jPanel1Layout.createParallelGroup()
				    .addComponent(numStepsTextField, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE)
				    .addComponent(numStepsButton, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 183, GroupLayout.PREFERRED_SIZE))
				.addGap(44)
				.addGroup(jPanel1Layout.createParallelGroup()
				    .addGroup(jPanel1Layout.createSequentialGroup()
				        .addComponent(precisionButton, GroupLayout.PREFERRED_SIZE, 178, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 0, Short.MAX_VALUE))
				    .addGroup(jPanel1Layout.createSequentialGroup()
				        .addGap(0, 0, Short.MAX_VALUE)
				        .addComponent(precisionTextField, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap());
				jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
				.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(precisionButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
				    .addComponent(numStepsButton, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
				.addGap(14)
				.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(precisionTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
				    .addComponent(numStepsTextField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED));
			}
			{
				this.setSize(488, 200);
				switch(dim) {
				case twoD :
				  this.setTitle("Yamabe2DFlow");
				  break;
				case threeD:
				  this.setTitle("Yamabe3DFlow");
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private ButtonGroup getStoppingCondButtonGroup() {
		if(stoppingCondButtonGroup == null) {
			stoppingCondButtonGroup = new ButtonGroup();
		}
		return stoppingCondButtonGroup;
	}
	
	
	private JLabel getStepsizeLabel() {
		if(stepsizeLabel == null) {
			stepsizeLabel = new JLabel();
			stepsizeLabel.setText("Stepsize:");
		}
		return stepsizeLabel;
	}

	private class StoppingCondListener implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
       String name = evt.getActionCommand();
       if(name.equals("NS")) {
         precisionTextField.setEnabled(false);
         numStepsTextField.setEnabled(true);
       } else if(name.equals("P")) {
         precisionTextField.setEnabled(true);
         numStepsTextField.setEnabled(false);
       }
    }
	}
	
	private class RunAction extends AbstractAction {
    public RunAction(String text) {
      super(text, null);
    }    
    public void actionPerformed(ActionEvent evt) {
      if(evt.getSource().equals(runButton)) {

        Solver solver = null;
        switch(dim) {
        case twoD:
          solver = new Yamabe2DFlow();
          break;
        case threeD:
          solver = new Yamabe3DFlow();
          break;
        }
        
        double[] radii = new double[Triangulation.vertexTable.size()];
        int i = 0;
        for(Radius r : Geometry.getRadii()) {
          radii[i] = r.getValue();
          i++;
        }
        if(numStepsButton.isSelected()) {
          try{
            numSteps = Integer.parseInt(numStepsTextField.getText());
            double stepsize = Double.parseDouble(stepsizeTextField.getText());
            solver.setStepsize(stepsize);
            rec = owner.getRecorder();
            solver.addObserver(rec);
            solver.run(radii, numSteps);
            solver.deleteObserver(rec);
            
            owner.getPolygonPanel().setRecorder(rec);
            owner.newFlow();
            owner.getPolygonPanel().repaint();
          } catch(NumberFormatException ex) {
            
          }
        } else {
          try{
            double precision = Double.parseDouble(precisionTextField.getText());
            double stepsize = Double.parseDouble(stepsizeTextField.getText());
            solver.setStepsize(stepsize);
            solver.setStoppingCondition(precision);
            rec = owner.getRecorder();
            solver.addObserver(rec);
            solver.run(radii);
            solver.deleteObserver(rec);
            
            owner.getPolygonPanel().setRecorder(rec);
            owner.newFlow();
            owner.getPolygonPanel().repaint();
          } catch(NumberFormatException ex) {
            
          }
        }
      }
      YamabeFlowDialog.this.dispose();
    }
	  
	}
}
