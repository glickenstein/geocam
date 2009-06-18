#ifndef ETA_H_
#define ETA_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "edge.h"

class Eta;
typedef map<TriPosition, Eta*, TriPositionCompare> EtaIndex;

class Eta : public virtual GeoQuant {
private:
  static EtaIndex* Index;

protected:
  Eta( Edge& e );
  void recalculate();

public:
  ~Eta();
  static Eta* At( Edge& e );
  static void CleanUp();
};
EtaIndex* Eta::Index = NULL;

Eta::Eta( Edge& e ){}

void Eta::recalculate(){}

Eta::~Eta(){}

Eta* Eta::At( Edge& e ){
  TriPosition T( 1, e.getSerialNumber() );
  if( Index == NULL ) Index = new EtaIndex();
  EtaIndex::iterator iter = Index->find( T );

  if( iter == Index->end() ){
    Eta* val = new Eta( e );
    Index->insert( make_pair( T, val ) );
    return val;
  } else {
    return iter->second;
  }
}

void Eta::CleanUp(){
  if( Index == NULL) return;
  EtaIndex::iterator iter;
  for(iter = Index->begin(); iter != Index->end(); iter++)
    delete iter->second;
  delete Index;
}

#endif /* ETA_H_ */
