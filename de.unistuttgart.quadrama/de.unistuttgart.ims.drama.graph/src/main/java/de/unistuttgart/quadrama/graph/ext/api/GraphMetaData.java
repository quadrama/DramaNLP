

/* First created by JCasGen Tue Mar 01 14:15:04 CET 2016 */
package de.unistuttgart.quadrama.graph.ext.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Mar 01 14:15:04 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.graph/src/main/java/de/unistuttgart/quadrama/graph/ext/Types.xml
 * @generated */
public class GraphMetaData extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(GraphMetaData.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected GraphMetaData() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public GraphMetaData(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public GraphMetaData(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public GraphMetaData(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: GraphClassName

  /** getter for GraphClassName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getGraphClassName() {
    if (GraphMetaData_Type.featOkTst && ((GraphMetaData_Type)jcasType).casFeat_GraphClassName == null)
      jcasType.jcas.throwFeatMissing("GraphClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GraphMetaData_Type)jcasType).casFeatCode_GraphClassName);}
    
  /** setter for GraphClassName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setGraphClassName(String v) {
    if (GraphMetaData_Type.featOkTst && ((GraphMetaData_Type)jcasType).casFeat_GraphClassName == null)
      jcasType.jcas.throwFeatMissing("GraphClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((GraphMetaData_Type)jcasType).casFeatCode_GraphClassName, v);}    
   
    
  //*--------------*
  //* Feature: EdgeClassName

  /** getter for EdgeClassName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getEdgeClassName() {
    if (GraphMetaData_Type.featOkTst && ((GraphMetaData_Type)jcasType).casFeat_EdgeClassName == null)
      jcasType.jcas.throwFeatMissing("EdgeClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GraphMetaData_Type)jcasType).casFeatCode_EdgeClassName);}
    
  /** setter for EdgeClassName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEdgeClassName(String v) {
    if (GraphMetaData_Type.featOkTst && ((GraphMetaData_Type)jcasType).casFeat_EdgeClassName == null)
      jcasType.jcas.throwFeatMissing("EdgeClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    jcasType.ll_cas.ll_setStringValue(addr, ((GraphMetaData_Type)jcasType).casFeatCode_EdgeClassName, v);}    
  }

    