#ifndef hinge_flip
#define hinge_flip

#include <cmath>
#include <iostream>
#include "triangulationInputOutput.h"
#include "TriangulationDevelopment.h"
#include "miscmath.h"
#define PI 	3.141592653589793238

//geoquants
#include "length.h"
#include "euc_angle.h"

//a simps struct is supposed to hold all the important values that are
//necessary for performing the flip
//the names of the various values correspond to the pictures below
/*       v2
         /|\
        / | \
  e2   /  |  \ e3
      /   |   \
     /    |e0  \
    /     |     \
v1  \ f0  | f1  / v3
     \    |    /
      \   |   /
  e1   \  |  / e4
        \ | /
         \|/
         v0

         v2
         / \
        /   \
  e2   /     \ e3
      /       \
     /    f1   \
    /           \
    -------------
v1  \    e0     / v3
     \         /
      \  f0   /
  e1   \     / e4
        \   /
         \ /
         v0
*/
struct simps {
      int v0, v1, v2, v3, e0, e1, e2, e3, e4, f0, f1;
      double e0_len, e1_len, e2_len, e3_len, e4_len;
      double a0, a2;
      bool f0neg, f1neg;
      double e0_len_after;
};

//performs a topological flip and determines the geometric configuration
//of the adjacent triangles of Edge e so that e's length can be changed appropriately
Edge
flip(Edge e);

//performs the recalculation of the length of edge e for a flip that starts
//as positive positive
void
flipPP(struct simps &h);

//performs the recalculation of the length of edge e for a flip that starts
//as positive negative
void
flipPN(struct simps &h);

//performs the recalculation of the length of edge e for a flip that starts
//as a negative negative
void
flipNN(struct simps &h);

//performas the topological changes for the flip
void
topoFlip(Edge, struct simps);

//helper function that constructs the struct to hold all the important values
bool
prepForFlip(Edge, struct simps*);

bool
isDegenerate(struct simps &h);

//the edge being flipped is zero
void degenerateFlippedIsZero(simps b);

//the edge being flipped is NONzero but one of the other four edges is zero
void degenerateNonFlippedIsZero(simps b);

//all the edges have nonzero length, but one of the angles is zero
void degenerateOnlyAngleIsZero(simps b);

#endif
