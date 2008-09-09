/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Triangulation Math file holds the functions that perform
calculations on the triangulation.
**************************************************************/
#include <cstdlib>
#include <iostream>
#include <cmath>
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include <algorithm>
#include "triangulation.h"
#include "triangulationmath.h"
#include <fstream>
#include <iomanip>
#define PI 	3.141592653589793238

double angle(double lengthA, double lengthB, double lengthC)
{
       //               a^2 + b^2 - c^2
       //  (/) = acos( ----------------- )
       //                     2ab
       return acos((lengthA*lengthA + lengthB*lengthB - lengthC*lengthC)
                                    / (2*lengthA*lengthB));
}

double angle(Vertex v, Face f)
{
     vector<int> sameAs, diff;
     sameAs = listIntersection(v.getLocalEdges(), f.getLocalEdges());
     Edge e1 = Triangulation::edgeTable[sameAs[0]];
     Edge e2 = Triangulation::edgeTable[sameAs[1]];
     diff = listDifference(f.getLocalEdges(), v.getLocalEdges());
     Edge e3 = Triangulation::edgeTable[diff[0]];
     
     return angle(e1.getLength(), e2.getLength(), e3.getLength());       
}

double curvature(Vertex v)
{
     double sum = 0;
     vector<int>::iterator it;
     vector<int>* vp = v.getLocalFaces();
     for(it = (*vp).begin(); it < (*vp).end(); it++)
     {
            sum += angle(v, Triangulation::faceTable[*it]);
     }
     return 2*PI - sum;
}

vector<int> listIntersection(vector<int>* list1, vector<int>* list2)
{
             vector<int> sameAs;
             
             for(int i = 0; i < (*list1).size(); i++)
             {
                     for(int j = 0; j < (*list2).size(); j++)
                     {
                             if((*list1)[i] == (*list2)[j])
                             {
                             sameAs.push_back((*list1)[i]);
                             break;
                             }
                     }
             }
             return sameAs;
}

vector<int> listDifference(vector<int>* list1, vector<int>* list2)
{
            vector<int> diff;
            
            for(int i = 0; i < (*list1).size(); i++)
            {
                    for(int j = 0; j < (*list2).size(); j++)
                    {
                            if((*list1)[i] == (*list2)[j])
                            break;
                            if(j == (*list2).size() - 1)
                            diff.push_back((*list1)[i]);
                    }
            }
            return diff;
}




/*
 * Calculates the Ricci flow of the current Triangulation using the 
 * Runge-Kutta method. Results from the steps are written into vectors of
 * doubles provided. The parameters are:
 *          
 *      vector<double>* weights-
 *                           A vector of doubles to append the results of
 *                           weights, grouped by step, with a total size of
 *                           numSteps * numVertices.
 *      vector<double>* curvatures-
 *                           A vector of doubles to append the results of
 *                           curvatures, grouped by step, with a total size
 *                           of numSteps * numVertices.
 *      double dt -          The time step size. Initial and ending
 *                           times not needed since diff. equations are
 *                           independent of time.
 *      double* initWeights- Array of initial weights of the Vertices 
 *                           in order.
 *      int numSteps -       The number of steps to take. 
 *                           (dt = (tf - ti)/numSteps)
 *      bool adjF -          Boolean of whether or not to use adjusted
 *                           differential equation. True to use adjusted.
 * 
 * The information placed in the vectors are the weights and curvatures for
 * each Vertex at each step point. The data is grouped by steps, so the first
 * vertex of the first step is the beginning element. After n doubles are
 * placed, for an n-vertex triangulation, the first vertex of the next step
 * follows. If the vectors passed in are not empty, the data is added to the
 * end of the vector and the original information is not cleared.
 *
 *            ***Credit for the algorithm goes to J-P Moreau.***
 */

void calcFlow(vector<double>* weights, vector<double>* curvatures,double dt ,double *initWeights,int numSteps, bool adjF)  
{
  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.
                                         
  double ta[p],tb[p],tc[p],td[p],z[p]; // Temporary arrays to hold data for 
                                      // the intermediate steps in.
  int    i,k; // ints used for "for loops". i is the step number,
              // k is the kth vertex for the arrays.
  map<int, Vertex>::iterator vit; // Iterator to traverse through the vertices.
  // Beginning and Ending pointers. (Used for "for" loops)
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  double net = 0; // Net and prev hold the current and previous
  double prev;    //  net curvatures, repsectively.
   for (k=0; k<p; k++) {
    z[k]=initWeights[k]; // z[k] holds the current weights.
   }
   for (i=1; i<numSteps+1; i++) // This is the main loop through each step.
   {
    prev = net; // Set prev to net.
    net = 0;    // Reset net.
    
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the weights of the Triangulation.
           vit->second.setWeight(z[k]);
       }
       if(i == 1) // If first time through, use static method to calculate total
       {           // cuvature.
            prev = Triangulation::netCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++) 
       {  // First "for loop"in whole step calculates everything manually.
           (*weights).push_back( z[k]); // Adds the data to the vector.
           double curv = curvature(vit->second);
           if(curv < 0.00005 && curv > -0.00005) // Adjusted for small numbers.
           {                                     // We want it to print nicely.
             (*curvatures).push_back(0.); // Adds the data to the vector.
           }
           else {
               (*curvatures).push_back(curv);
           }
           net += curv; // Calculating the net curvature.
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) ta[k]= dt * ((-1) * curv 
                           * vit->second.getWeight() +
                           prev /  p
                           * vit->second.getWeight());
           else     ta[k] = dt * (-1) * curv 
                           * vit->second.getWeight();
           
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       { // Set the new weights to our triangulation.
           vit->second.setWeight(z[k]+ta[k]/2);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            // Again calculates the differential equation, but we still need
            // the data in ta[] so we use tb[] now.
            if(adjF) tb[k]=dt*adjDiffEQ(vit->first, net);
            else     tb[k]=dt*stdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tb[k]/2);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            if(adjF) tc[k]=dt*adjDiffEQ(vit->first, net);
            else     tc[k]=dt*stdDiffEQ(vit->first);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  // Set the new weights.
       {
           vit->second.setWeight(z[k]+tc[k]);
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
            if(adjF) td[k]=dt*adjDiffEQ(vit->first, net);
            else     td[k]=dt*stdDiffEQ(vit->first);
       }
       for (k=0; k<p; k++) // Adjust z[k] according to algorithm.
       {
         z[k]=z[k]+(ta[k]+2*tb[k]+2*tc[k]+td[k])/6;
         
       }
   }
}

double stdDiffEQ(int vertex) 
{
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight();
}

double adjDiffEQ(int vertex, double totalCurv)
{
       return (-1) * curvature(Triangulation::vertexTable[vertex])
                   * Triangulation::vertexTable[vertex].getWeight() +
                   totalCurv /  Triangulation::vertexTable.size()
                   * Triangulation::vertexTable[vertex].getWeight();
}


double inRadius(Face f)
{
    if(f.getLocalVertices()->size() != 3) // Make sure the face has 3 vertices.
    {
        return -1;
    }
    double sum = 0;
    double product = 1;
    for(int i = 0; i < 3; i++)
    {     int index =(*(f.getLocalVertices()))[i];
          double w = Triangulation::vertexTable[index].getWeight();
          sum += w;
          product *= w; 
    }
    return sqrt(product / sum);
}

double dualLength(Edge e)
{
       vector<int> localFaces = *(e.getLocalFaces());
       return inRadius(Triangulation::faceTable[localFaces[0]])
              + inRadius(Triangulation::faceTable[localFaces[1]]);
}

double dualArea(Vertex v)
{
       vector<int> localEdges = (*v.getLocalEdges());
       double areaSum = 0;
       for(int i = 0; i < localEdges.size(); i++)
       {
          // Area of one of the triangles in this polygon.
          areaSum += v.getWeight() * dualLength(Triangulation::edgeTable[localEdges[i]]) / 2;
       }
       return areaSum;
}

