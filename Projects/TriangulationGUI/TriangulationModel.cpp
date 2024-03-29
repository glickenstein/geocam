#include "TriangulationModel.h"


vector<double> TriangulationModel::weights;
vector<double> TriangulationModel::curvs;
int TriangulationModel::numSteps;
double TriangulationModel::stepSize;
double TriangulationModel::acc;
bool TriangulationModel::loaded;
bool TriangulationModel::flow;
bool TriangulationModel::smart;
Approximator* TriangulationModel::app = new EulerApprox(StdRicci);;

TriangulationModel::TriangulationModel()
{
    loaded = false;
    flow = false;
    smart = false;
}
TriangulationModel::~TriangulationModel()
{
   clearSystem();
   delete app;
}
void TriangulationModel::clearSystem()
{
   clearData();
   loaded = false;
   Geometry::reset();
   Triangulation::reset();
}
void TriangulationModel::clearData()
{
    app->clearHistories();
    numSteps = 0;
    stepSize = 0.0;
}
void TriangulationModel::setNumSteps(int steps)
{
    numSteps = steps;
}
void TriangulationModel::setStepSize(double size)
{
    stepSize = size;
}
void TriangulationModel::setAccuracy(double accuracy)
{
     acc = accuracy;
}
void TriangulationModel::setFlowFunction(bool flowF)
{
     flow = flowF;
}
void TriangulationModel::setSmartFlow(bool smartF)
{
     smart = smartF;
}
void TriangulationModel::setRadii(vector<double> *weightsVec) {
     if(weightsVec->size() != Triangulation::vertexTable.size()) {
        return;
     }
     double weightArr[weightsVec->size()];
     for(int i = 0; i < weightsVec->size(); i++) {
         weightArr[i] = (*weightsVec)[i];
     }
     Geometry::setRadii(weightArr);
}
void TriangulationModel::setRadius(int vertex, double weight) {
     Geometry::setRadius(Triangulation::vertexTable[vertex], weight);
}
void TriangulationModel::setEta(int edge, double eta) {
     Geometry::setEta(Triangulation::edgeTable[edge], eta);
}
bool TriangulationModel::isLoaded()
{
     return loaded;
}
bool TriangulationModel::runFlow(int type)
{
     if(!loaded)
     {
        return false;
     }
     switch(type)
     {
        case ID_RUN_FLOW_EUCLIDEAN:
        {
             if(flow) {
                app->local_derivs = *AdjRicci;
             } else {
                app->local_derivs = *StdRicci;
             }
             if(smart) {
                app->run(acc, acc, stepSize);
             }
             else{
                app->run(numSteps, stepSize);
             }
             //calcFlow(&weights, &curvs, stepSize, weightArr, numSteps, flow);
        }
        break;
        case ID_RUN_FLOW_SPHERICAL:
        {
             if(flow) {
                app->local_derivs = *AdjSpherRicci;
             } else {
                app->local_derivs = *SpherRicci;
             }
             if(smart) {
                app->run(acc, acc, stepSize);
             }
             else{
                app->run(numSteps, stepSize);
             }
             //sphericalCalcFlow(&weights, &curvs, stepSize, weightArr, numSteps, flow);
        }
        break;
        case ID_RUN_FLOW_HYPERBOLIC:
        {
             if(flow) {
                app->local_derivs = *AdjHypRicci;
             } else {
                app->local_derivs = *HypRicci;
             }
             if(smart) {
                app->run(acc, acc, stepSize);
             }
             else{
                app->run(numSteps, stepSize);
             }
             //hyperbolicCalcFlow(&weights, &curvs, stepSize, weightArr, numSteps, flow);
        }
        break;
        case ID_RUN_FLOW_YAMABE:
        {
             app->local_derivs = *Yamabe;
             if(smart) {
                app->run(acc, acc, stepSize);
             }
             else{
                app->run(numSteps, stepSize);
             }
//             if(smart) {
//               yamabeFlow(&weights, &curvs, stepSize, acc, acc, flow);
//             } else { 
//               yamabeFlow(&weights, &curvs, stepSize, numSteps, flow);
//             }
        }
        break;
        default: return false;
     }
     return true;
}

bool TriangulationModel::loadFile(char* filename, int format)
{
    switch(format) 
    {
       case IDSTANDARD:
       {
            readTriangulationFile(filename);
            loaded = true;
       }
       break;
       case IDLUTZ:
       {
            makeTriangulationFile(filename, "manifoldConverted.txt");
            readTriangulationFile("manifoldConverted.txt");
            remove("manifoldConverted.txt");
            loaded = true;
       }
       break;
       default: return false;
    }
    Geometry::setDimension(TwoD);
    Geometry::build();
    return true;
}
bool TriangulationModel::load3DFile(char* filename, int format)
{
    switch(format) 
    {
       case IDSTANDARD:
       {
            read3DTriangulationFile(filename);
            loaded = true;
       }
       break;
       case IDLUTZ:
       {
            make3DTriangulationFile(filename, "manifoldConverted.txt");
            read3DTriangulationFile("manifoldConverted.txt");
            remove("manifoldConverted.txt");
            loaded = true;
       }
       break;
       default: return false;
    }
    Geometry::setDimension(ThreeD);
    Geometry::build();
    return true;
}
bool TriangulationModel::printResults(int printType)
{
     if(!loaded) 
     {
        return false;
     }
     switch(printType)
     {
         case IDPRINTSTEP:
         {
              printResultsStep("GUIResult.txt", &(app->radiiHistory), &(app->curvHistory));
         }
         break;
         case IDPRINTVERTEX:
         {
              printResultsVertex("GUIResult.txt", &(app->radiiHistory), &(app->curvHistory));
         }
         break;
         case IDPRINTNUM:
         {
              printResultsNum("GUIResult.txt", &(app->radiiHistory), &(app->curvHistory));
         }
         break;
         case IDPRINTNUMSTEP:
         {
              printResultsNumSteps("GUIResult.txt", &(app->radiiHistory), &(app->curvHistory));
         }
         break;
         default: return false;
     }
     return true;
}

