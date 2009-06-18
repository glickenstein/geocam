#ifndef GEOQUANTS_H_
#define GEOQUANTS_H_

#include "simplex/vertex.h"
#include "simplex/edge.h"
#include "simplex/face.h"
#include "simplex/tetra.h"

#include "triangulation/triangulation.h"

#include "geoquant.h"
#include "triposition.h"

#include <vector>
#include <cmath>

#define PI 	3.141592653589793238

/***** SPHERICAL ANGLE *****/
class SphericalAngle : public virtual GeoQuant {
 private:
  GeoQuant* lengthA;
  GeoQuant* lengthB;
  GeoQuant* lengthC;
  
 public:  
  SphericalAngle(Vertex& v, Face& f, GQIndex& gqi);

  void recalculate();
};
void Init_SphericalAngles(GQIndex& gqi);
/***************************/

/***** HYPERBOLIC ANGLE *****/
class HyperbolicAngle : public virtual GeoQuant {
 private:
  GeoQuant* lengthA;
  GeoQuant* lengthB;
  GeoQuant* lengthC;
  
 public:  
  HyperbolicAngle(Vertex& v, Face& f, GQIndex& gqi);

  void recalculate();
};
void Init_HyperbolicAngles(GQIndex& gqi);
/***************************/


/***** 2D CURVATURE *****/
class Curvature2D : public virtual GeoQuant {
 private:
  vector<GeoQuant*>* angles;

 public:
  Curvature2D(Vertex& v, GQIndex& gqi);

  ~Curvature2D(){ delete angles; }

  void recalculate();
};
void Init_Curvature2Ds(GQIndex& gqi);
/***********************/

/***** DIHEDRAL ANGLE *****/
class DihedralAngle : public virtual GeoQuant {
 private:
  GeoQuant* angles[3];

 public:
  DihedralAngle(Edge& e, Tetra& t, GQIndex& gqi);

  void recalculate();
};
void Init_DihedralAngles(GQIndex& gqi);
/*************************/

/***** VOLUME *****/
class Volume : public virtual GeoQuant {
 private:
  GeoQuant* len[6];
  
 public:
  Volume(Tetra& t, GQIndex& gqi);

  void recalculate();
};
void Init_Volumes(GQIndex& gqi);
/******************/

/***** EDGE CURVATURE *****/
class EdgeCurvature : public virtual GeoQuant {
 private:
  vector<GeoQuant*>* dih_angles;

 public:
  EdgeCurvature(Edge& e, GQIndex& gqi);

  ~EdgeCurvature(){ delete dih_angles; }

  void recalculate();
};
void Init_EdgeCurvatures(GQIndex& gqi);
/*************************/

/***** PARTIAL EDGE *****/
class PartialEdge : public virtual GeoQuant {
 private:
   GeoQuant* len;
   GeoQuant* radA;
   GeoQuant* radB;

 public:
  PartialEdge( Vertex& v, Edge& e , GQIndex& gqi);

  void recalculate();
};
void Init_PartialEdges(GQIndex& gqi);
/************************/

/***** 3D CURVATURE *****/
class Curvature3D : public virtual GeoQuant {
 private:
  vector<GeoQuant*>* edgeCurvs;
  vector<GeoQuant*>* partials;

 public:
  Curvature3D(Vertex& v, GQIndex& gqi);

  ~Curvature3D(){ delete edgeCurvs; delete partials;}

  void recalculate();
};
void Init_Curvature3Ds(GQIndex& gqi);
/***********************/



#endif /* GEOQUANTS_H_ */