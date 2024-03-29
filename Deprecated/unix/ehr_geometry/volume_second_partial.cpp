#include "volume_second_partial.h"
#include "math/miscmath.h"

#include <stdio.h>

typedef map<TriPosition, VolumeSecondPartial*, TriPositionCompare> VolumeSecondPartialIndex;
static VolumeSecondPartialIndex* Index = NULL;

VolumeSecondPartial::VolumeSecondPartial( Vertex& v, Vertex& w, Tetra& t ){
  StdTetra st;

  if( v.getIndex() == w.getIndex() ){
    st = labelTetra( t, v );
    sameVertices = true;
  } else {
    sameVertices = false;
    int vI = v.getIndex();
    int wI = w.getIndex();

    vector<int>* localVertices;
    vector<int>* localEdges = t.getLocalEdges();
    for(int ii = 0; ii < localEdges->size(); ii++){
      Edge& e = Triangulation::edgeTable[ localEdges->at(ii) ];
      
      localVertices = e.getLocalVertices();
      int v0 = localVertices->at(0);
      int v1 = localVertices->at(1);

      if( (v0 == vI && v1 == wI) || (v0 == wI && v1 == vI) ){
	st = labelTetra( t, e );
	break;
      }
    }
  }

  rad[0] = Radius::At( Triangulation::vertexTable[ st.v1 ] );
  rad[1] = Radius::At( Triangulation::vertexTable[ st.v2 ] );
  rad[2] = Radius::At( Triangulation::vertexTable[ st.v3 ] );
  rad[3] = Radius::At( Triangulation::vertexTable[ st.v4 ] );

  for(int ii = 0; ii < 4; ii++) rad[ii]->addDependent( this );
  
  eta[0] = Eta::At( Triangulation::edgeTable[ st.e12 ] );
  eta[1] = Eta::At( Triangulation::edgeTable[ st.e13 ] );
  eta[2] = Eta::At( Triangulation::edgeTable[ st.e14 ] );
  eta[3] = Eta::At( Triangulation::edgeTable[ st.e23 ] );
  eta[4] = Eta::At( Triangulation::edgeTable[ st.e24 ] );
  eta[5] = Eta::At( Triangulation::edgeTable[ st.e34 ] );

  for(int ii = 0; ii < 6; ii++) eta[ii]->addDependent( this );
}

void VolumeSecondPartial::recalculate(){
  double r1 = rad[0]->getValue();
  double r2 = rad[1]->getValue();
  double r3 = rad[2]->getValue();
  double r4 = rad[3]->getValue();
       
  double Eta12 = eta[0]->getValue();  
  double Eta13 = eta[1]->getValue();
  double Eta14 = eta[2]->getValue(); 
  double Eta23 = eta[3]->getValue();
  double Eta24 = eta[4]->getValue();  
  double Eta34 = eta[5]->getValue();

  if( sameVertices ){
    value = (r1*(-(r1*pow(2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
				     Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3\
				    + ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
					Eta12*(Eta23 + Eta24*Eta34))*r2 + 
				       (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
					Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
			2*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
				 pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
			      2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
					Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2\
				       + (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + 
					  Eta23*Eta34 + Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			      ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
				pow(Eta24,2))*pow(r2,2) - 
			       2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + 
				  Eta24*Eta34 + Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
			       (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
				pow(Eta34,2))*pow(r3,2))*pow(r4,2)),2)) + 
	       4*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
			pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
		     2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
			       Eta34 - pow(Eta12,2)*Eta34)*r2 + 
			      (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
			       Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
		     ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
		       pow(Eta24,2))*pow(r2,2) - 
		      2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			 Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
		      (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
		       pow(Eta34,2))*pow(r3,2))*pow(r4,2))*
	       (-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
		   pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
		2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
				Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			       ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
				 Eta12*(Eta23 + Eta24*Eta34))*r2 + 
				(Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
				 Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
			      2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
			   2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
				     Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
				    (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
				     Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			   ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			     pow(Eta24,2))*pow(r2,2) - 
			    2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			       Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
			    (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
			     pow(Eta34,2))*pow(r3,2))*pow(r4,2))) + 
	       2*(2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
			       Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			      ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
				Eta12*(Eta23 + Eta24*Eta34))*r2 + 
			       (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
				Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		  2*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
			   pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
			2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
				  Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
				 (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
				  Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			  pow(Eta24,2))*pow(r2,2) - 
			 2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			    Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
			 (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
			  pow(Eta34,2))*pow(r3,2))*pow(r4,2)))*
	       (-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
		   pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
		2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
				Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			       ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
				 Eta12*(Eta23 + Eta24*Eta34))*r2 + 
				(Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
				 Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
			      2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
			   2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
				     Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
				    (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
				     Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			   ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			     pow(Eta24,2))*pow(r2,2) - 
			    2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			       Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
			    (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
			     pow(Eta34,2))*pow(r3,2))*pow(r4,2)))))/
    (24.*pow(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
		pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
	     2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
			     Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			    ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
			      Eta12*(Eta23 + Eta24*Eta34))*r2 + 
			     (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
			      Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
	     pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
			   pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
			2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
				  Eta34 - pow(Eta12,2)*Eta34)*r2 + 
				 (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
				  Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			  pow(Eta24,2))*pow(r2,2) - 
			 2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			    Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
			 (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
			  pow(Eta34,2))*pow(r3,2))*pow(r4,2)),1.5));
  } else {

  value= r2*(-(r1*(-2*(-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
		       pow(Eta34,2))*r2*pow(r3,2)*pow(r4,2) + 
		   2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
				   Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r3 + 
				  (Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
				   Eta12*(Eta23 + Eta24*Eta34))*r4) + 
		   2*r1*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
				Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			       ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
				 Eta12*(Eta23 + Eta24*Eta34))*r2 + 
				(Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
				 Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		   pow(r1,2)*(-2*(-1 + pow(Eta12,2) + pow(Eta13,2) + 
				  2*Eta12*Eta13*Eta23 + pow(Eta23,2))*r2*pow(r3,2) + 
			      2*(Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
				 Eta34 - pow(Eta12,2)*Eta34)*r2*r3*r4 + 
			      2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
				     Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2\
				    + (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + 
				       Eta23*Eta34 + Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			      (2*(-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
				  pow(Eta24,2))*r2 - 
			       2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + 
				  Eta24*Eta34 + Eta12*(Eta13 + Eta14*Eta34))*r3)*pow(r4,2)))*
	       (2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
			     Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			    ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
			      Eta12*(Eta23 + Eta24*Eta34))*r2 + 
			     (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
			      Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		2*r1*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
			 pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
		      2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
				Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2\
			       + (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + 
				  Eta23*Eta34 + Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
		      ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			pow(Eta24,2))*pow(r2,2) - 
		       2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + 
			  Eta24*Eta34 + Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
		       (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
			pow(Eta34,2))*pow(r3,2))*pow(r4,2))))/
	     (24.*pow(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
			 pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
		      2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
				      Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
				     ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
				       Eta12*(Eta23 + Eta24*Eta34))*r2 + 
				      (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
				       Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		      pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
				    2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
				 2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
					   Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
					  (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
					   Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
				 ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
				   pow(Eta24,2))*pow(r2,2) - 
				  2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
				     Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
				  (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
				   pow(Eta34,2))*pow(r3,2))*pow(r4,2)),1.5)) + 
	     (r1*(2*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
			       Eta12*(Eta24 + Eta23*Eta34))*r3 + 
			      (Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
			       Eta12*(Eta23 + Eta24*Eta34))*r4) + 
		  2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + Eta13*(Eta23*Eta24 + Eta34) + 
			    Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
			   ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
			     Eta12*(Eta23 + Eta24*Eta34))*r2 + 
			    (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
			     Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		  2*r1*(-2*(-1 + pow(Eta12,2) + pow(Eta13,2) + 2*Eta12*Eta13*Eta23 + 
			    pow(Eta23,2))*r2*pow(r3,2) + 
			2*(Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
			   Eta34 - pow(Eta12,2)*Eta34)*r2*r3*r4 + 
			2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + Eta13*(Eta14 + Eta12*Eta24) + 
			       Eta34 - pow(Eta12,2)*Eta34)*r2 + 
			      (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
			       Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
			(2*(-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
			    pow(Eta24,2))*r2 - 
			 2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
			    Eta12*(Eta13 + Eta14*Eta34))*r3)*pow(r4,2))))/
	     (12.*sqrt(-((-1 + pow(Eta23,2) + pow(Eta24,2) + 2*Eta23*Eta24*Eta34 + 
			  pow(Eta34,2))*pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
		       2*r1*r2*r3*r4*((Eta14 - Eta14*pow(Eta23,2) + 
				       Eta13*(Eta23*Eta24 + Eta34) + Eta12*(Eta24 + Eta23*Eta34))*r2*r3 + 
				      ((Eta13 - Eta13*pow(Eta24,2) + Eta14*(Eta23*Eta24 + Eta34) + 
					Eta12*(Eta23 + Eta24*Eta34))*r2 + 
				       (Eta12 - Eta12*pow(Eta34,2) + Eta14*(Eta24 + Eta23*Eta34) + 
					Eta13*(Eta23 + Eta24*Eta34))*r3)*r4) + 
		       pow(r1,2)*(-((-1 + pow(Eta12,2) + pow(Eta13,2) + 
				     2*Eta12*Eta13*Eta23 + pow(Eta23,2))*pow(r2,2)*pow(r3,2)) + 
				  2*r2*r3*((Eta12*Eta14*Eta23 + Eta23*Eta24 + 
					    Eta13*(Eta14 + Eta12*Eta24) + Eta34 - pow(Eta12,2)*Eta34)*r2 + 
					   (Eta13*Eta14*Eta23 + Eta24 - pow(Eta13,2)*Eta24 + Eta23*Eta34 + 
					    Eta12*(Eta14 + Eta13*Eta34))*r3)*r4 - 
				  ((-1 + pow(Eta12,2) + pow(Eta14,2) + 2*Eta12*Eta14*Eta24 + 
				    pow(Eta24,2))*pow(r2,2) - 
				   2*(Eta23 - pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta24*Eta34 + 
				      Eta12*(Eta13 + Eta14*Eta34))*r2*r3 + 
				   (-1 + pow(Eta13,2) + pow(Eta14,2) + 2*Eta13*Eta14*Eta34 + 
				    pow(Eta34,2))*pow(r3,2))*pow(r4,2)))));
  }
}

VolumeSecondPartial::~VolumeSecondPartial(){}

void VolumeSecondPartial::remove() {
  deleteDependents();
  rad[0]->removeDependent(this);
  rad[1]->removeDependent(this);
  rad[2]->removeDependent(this);
  rad[3]->removeDependent(this);
  
  eta[0]->removeDependent(this);
  eta[1]->removeDependent(this);
  eta[2]->removeDependent(this);
  eta[3]->removeDependent(this);
  eta[4]->removeDependent(this);
  eta[5]->removeDependent(this);     
  Index->erase(pos);
  delete this;
}

VolumeSecondPartial* VolumeSecondPartial::At( Vertex& v, Vertex& w, Tetra& t ){
  TriPosition T( 3, v.getSerialNumber(), w.getSerialNumber(), t.getSerialNumber() );
  if( Index == NULL ) Index = new VolumeSecondPartialIndex();
  VolumeSecondPartialIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    VolumeSecondPartial* val = new VolumeSecondPartial( v, w, t );
    val->pos = T;
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void VolumeSecondPartial::CleanUp(){
  if( Index == NULL ) return;
  VolumeSecondPartialIndex::iterator iter;
  VolumeSecondPartialIndex copy = *Index;
  for(iter = copy.begin(); iter != copy.end(); iter++) {
    iter->second->remove();
  }
    
  delete Index;
  Index = NULL;
}

void VolumeSecondPartial::Record( char* filename ){
  FILE* output = fopen( filename, "a+" );

  VolumeSecondPartialIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    fprintf( output, "%lf ", iter->second->getValue() );
  fprintf( output, "\n");

  fclose( output );
}


