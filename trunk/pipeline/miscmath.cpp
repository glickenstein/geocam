/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Miscellaneous Math file holds the functions that perform
calculations on Points, Lines, and Circles. All functions are
done independently of any triangulation.
**************************************************************/
#include "miscmath.h"
#include <cstdio>

vector<int> listIntersection(vector<int>* list1, vector<int>* list2){
  vector<int> sameAs;
             
  for(int i = 0; i < (*list1).size(); i++){
    for(int j = 0; j < (*list2).size(); j++){
      if((*list1)[i] == (*list2)[j]) {
	sameAs.push_back((*list1)[i]);
	break;
      }
    }
  }
  return sameAs;
}

vector<int> listDifference(vector<int>* list1, vector<int>* list2){
  vector<int> diff;
            
  for(int i = 0; i < (*list1).size(); i++){
    for(int j = 0; j < (*list2).size(); j++){
      if((*list1)[i] == (*list2)[j]) break;
      if(j == (*list2).size() - 1) 
	diff.push_back((*list1)[i]);
    }
  }
  return diff;
}

bool containsVertex( Simplex& s, Vertex& v ){
  vector<int>* localVertices = s.getLocalVertices();

  int index = v.getIndex();
  for(int ii; ii < localVertices->size(); ii++)
    if( localVertices->at(ii) == index )
      return true;

  return false;
}

StdEdge labelEdge(Vertex& v, Edge& e){
  StdEdge retval;

  vector<int>* localVertices =  e.getLocalVertices();
  retval.v1 = v;
  
  for(int ii; ii < localVertices->size(); ii++){
    if( localVertices->at(ii) != v.getIndex() )
      retval.v2 = Triangulation::vertexTable[ localVertices->at( ii ) ];
  }

  return retval;
}

StdFace labelFace(Edge& e, Face& f){
  StdFace retval;

  vector<int>* localVertices = e.getLocalVertices();

  retval.v1 = Triangulation::vertexTable[ localVertices->at(0) ];
  retval.v2 = Triangulation::vertexTable[ localVertices->at(1) ];

  vector<int>* allVertices = f.getLocalVertices();
  int ii;
  for( ii = 0; ii < localVertices->size(); ii++ ){
    if( allVertices->at( ii ) != localVertices->at( 0 ) &&
	allVertices->at( ii ) != localVertices->at( 1 ) )
      break;
  }
  retval.v3 = Triangulation::vertexTable[ allVertices->at( ii ) ];
   
  vector<int>* localEdges = f.getLocalEdges();
  for( ii = 0; ii < localEdges->size(); ii++ ){
    Edge e = Triangulation::edgeTable[ localEdges->at( ii ) ];
    
    if( e.isAdjVertex( retval.v1.getIndex() ) ){
      if( e.isAdjVertex( retval.v2.getIndex() ) ){
	retval.e12 = e;
      } else{
	retval.e13 = e;
      }
    } else {
      retval.e23 = e;
    }
  }   
  
  return retval;
}

StdFace labelFace(Vertex& v, Face& f){
  StdFace retval;

  retval.v1 = v;

  int index = v.getIndex();
  vector<int> otherverts;
  vector<int>* localVertices = f.getLocalVertices();  
  int ii;
  for( ii = 0; ii < localVertices->size(); ii++ ){
    int jj = localVertices->at( ii );
    if( jj != index )
      otherverts.push_back( jj );
  }

  retval.v2 = Triangulation::vertexTable[ localVertices->at( 0 ) ];
  retval.v3 = Triangulation::vertexTable[ localVertices->at( 1 ) ];
       
  vector<int>* localEdges = f.getLocalEdges();
  for( ii = 0; ii < localEdges->size(); ii++ ){
    Edge e = Triangulation::edgeTable[ localEdges->at( ii ) ];
    
    bool b1 = e.isAdjVertex( retval.v1.getIndex() );
    bool b2 = e.isAdjVertex( retval.v2.getIndex() );
    bool b3 = e.isAdjVertex( retval.v3.getIndex() );
    
    if( b1 && b2 ){
      retval.e12 = e;
    } else if ( b1 && b3 ){
      retval.e13 = e;
    } else if ( b2 && b3 ){
      retval.e23 = e;
    } else {
      fprintf( stderr, "Error: Corrupt face data!\n");
    }
  }   
  
  return retval;
}

/*****************************************************/

void fixEdges( Tetra& t, StdTetra& st ){
  vector<int>* localEdges = t.getLocalEdges();
  for( int ii = 0; ii < localEdges->size(); ii++ ){
    Edge ed = Triangulation::edgeTable[ localEdges->at( ii ) ];
    
    bool b1 = ed.isAdjVertex( st.v1.getIndex() );
    bool b2 = ed.isAdjVertex( st.v2.getIndex() );
    bool b3 = ed.isAdjVertex( st.v3.getIndex() );
    bool b4 = ed.isAdjVertex( st.v4.getIndex() );

    if( b1 && b2 ){
      st.e12 = ed;
    } else if( b1 && b3 ){
      st.e13 = ed;
    } else if( b1 && b4 ){
      st.e14 = ed;
    } else if( b2 && b3 ){
      st.e23 = ed;
    } else if( b2 && b4 ){
      st.e24 = ed;
    } else if( b3 && b4 ){
      st.e34 = ed;
    } else {
      fprintf( stderr, "Error! Corrupt Tetra data!\n" );
    }
  }  
}

void fixFaces( Tetra& t, StdTetra& st ){
  vector<int>* localFaces = t.getLocalFaces();
  for( int ii = 0; ii < localFaces->size(); ii++ ){    
    Face f = Triangulation::faceTable[ localFaces->at(ii) ];

    bool b1 = f.isAdjEdge( st.e12.getIndex() );
    bool b2 = f.isAdjEdge( st.e13.getIndex() );
    bool b3 = f.isAdjEdge( st.e14.getIndex() );
    bool b4 = f.isAdjEdge( st.e34.getIndex() );
    
    if( b1 && b2 ){ st.f123 = f; }
    else if( b1 && b3 ){ st.f124 = f; }
    else if( b2 && b3 ){ st.f134 = f; }
    else if( b4 && ! b1){ st.f234 = f; }
    else {
      fprintf( stderr, "Error! Corrupt Tetra data!\n" );
    }
  }
}

StdTetra labelTetra(Edge& e, Tetra& t){
  StdTetra retval;

  vector<int>* localVertices = e.getLocalVertices();
  vector<int> verts; 

  retval.v1 = Triangulation::vertexTable[ localVertices->at(0) ];
  retval.v2 = Triangulation::vertexTable[ localVertices->at(1) ];

  localVertices = t.getLocalVertices();
  for( int ii = 0; ii < localVertices->size(); ii++ ){
    int jj =  localVertices->at( ii );
    if( jj != retval.v1.getIndex() &&
	jj != retval.v2.getIndex() ){
      verts.push_back( jj ); 
    }
  }

  retval.v3 = Triangulation::vertexTable[ verts[0] ];
  retval.v4 = Triangulation::vertexTable[ verts[1] ];
  
  fixEdges( t, retval );
  fixFaces( t, retval );
  
  return retval;
}

StdTetra labelTetra(Vertex& v, Tetra& t){
  StdTetra retval;

  retval.v1 = v; 

  vector<int>* localVertices = t.getLocalVertices();
  vector<int> verts; 
  int index = v.getIndex();

  for( int ii = 0; ii < localVertices->size(); ii++ ){
    int jj = localVertices->at( ii );
    if( jj != index ){
      verts.push_back( jj ); 
    }
  }
  
  retval.v2 = Triangulation::vertexTable[ verts[0] ];
  retval.v3 = Triangulation::vertexTable[ verts[1] ];
  retval.v4 = Triangulation::vertexTable[ verts[2] ];
  
  fixEdges( t, retval );
  fixFaces( t, retval );

  return retval;
}

StdTetra labelTetra( Face& f, Tetra& t ){
  StdTetra retval;
 
  vector<int>* verts = f.getLocalVertices();
  retval.v1 = verts->at(0);
  retval.v2 = verts->at(1);
  retval.v3 = verts->at(2);
  verts = listDifference( t.getLocalVertices(), f.getLocalVertices() );
  retval.v4 = verts->at(0);

  fixEdges( retval, t );
  fixFaces( retval, t );
}

StdTetra labelTetra( Tetra& t ){
  Vertex& v = Triangulation::vertexTable[ (*(t.getLocalVertices()))[0] ];
  return labelTetra( v, t );
}

