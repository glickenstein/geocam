#include <cmath>
#include <iostream>
#include "delaunay.h"
#include "triangulation/triangulationmorph.h"
#include "triangulation/triangulationInputOutput.h"
#include "triangulation/TriangulationCoordinateSystem.h"

class TriangulationDisplay {

    TriangulationCoordinateSystem coordSystem;

    //this string should be the path to the file that contains the triangulation information
    char* fileName;

    vector<triangle_parts> listOfTriangles;

    map<int,Edge>::iterator selectedEdge;

    public:
    TriangulationDisplay(void);
    TriangulationDisplay(char* f);
    ~TriangulationDisplay(void);

    //changes the file that the triangulation represents and should reset everything
    //within this class to represent that change (i.e. the coordSystem should be updated)
    void changeFileTo(char * f);

    char* getCurrentFile(void);

    //the line object representing the currently selected edge
    Line currentEdgeToLine();
    
    //returns the currently selected edge
    Edge currentEdge(void);

    //increments the currently selected edge to another edge in the triangulations
    Edge nextEdge(void);

    //decrements the currently selected edge, returning to the previously selected edge
    Edge previousEdge(void);

    //calls the flip function on the currently selected edge
    void flipCurrentEdge(void);

    //returns a vector of structs containing the information necessary for
    //displaying the triangulation in opengl
    vector<triangle_parts> getTriangles(void);
    
    //updates the underlying coordinateSystem object to represent the current state
    //of the triangulation
    void update();
};