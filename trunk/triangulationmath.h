/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 17, 2008
***************************************************************
The Triangulation Math file holds the functions that perform
calculations on the triangulation.
**************************************************************/
 
#include "simplex/vertex.h"
#include "simplex/edge.h"
#include "simplex/face.h"

/*
 * Calculates the angle between two edges of a triangle, where the
 * first two doubles represent the lengths of those edges and the
 * third represents the length of the edge opposite the angle.
 */
double angle(double, double, double);

/*
 * Calculates the angle between two edges of a triangle, where the
 * last edge given is the edge opposite the angle.
 */
double angle(Edge, Edge, Edge);

/*
 * Calculates the angle between two edges of a triangle, given the
 * vertex where the two edges meet and the face of the triangle.
 */
double angle(Vertex, Face);

/*
 * Calculates the curvature at a vertex i. The curvature is calculated by
 * subtracting the sum of all the angles at i from 2*PI.
 */
double curvature(Vertex);

/*
 * Returns a list of simplex indices that are common between the two lists
 * given.
 */
vector<int> listIntersection(vector<int>*, vector<int>*);

/*
 * Calculates the Ricci flow of the current Triangulation using the 
 * Runge-Kutta method. Results from the steps are written into the file 
 * "ODE Result" for viewing. calcFlow takes a number of paramters:
 *       
 *      double dt -          The time step size. Initial and ending
 *                           times not needed since diff. equations are
 *                           independent of time.
 *      double* initWeights- Array of initial weights of the Vertices 
 *                           in order.
 *      int numSteps -       The number of steps to take. 
 *                           (dt = (tf - ti)/numSteps)
 *      bool adjF -          Boolean of whether or not to use adjusted
 *                           differential equation. True to use adjusted.
 *      int fi -             The finesse, or number of intermediary points.
 *                           Default of 1.
 * 
 * The information printed in the file are the weights and curvatures for
 * each Vertex at each step point. The file is cleared at the beginning of
 * every call to calcFlow.
 *
 *            ***Credit for the algorithm goes to J-P Moreau.***
 */
void calcFlow(double, double*, int, bool, int fi = 1);

/*
 * Calculates the dr/dt of the vertex given by its index according to the
 * formula:
           dr_i/dt = -K_i * r_i
 */
double stdDiffEQ(int);

/*
 * Calculates the dr/dt of the vertex given by its index according to the
 * adjusted formula:
           dr_i/dt = -K_i * r_i + total K / # vertices * r_i.
 */
double adjDiffEQ(int);
