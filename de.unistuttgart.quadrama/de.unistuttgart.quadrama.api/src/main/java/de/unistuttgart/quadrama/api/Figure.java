

/* First created by JCasGen Wed Feb 17 17:32:06 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Represents on figure in the play. Each figure should only have one Figure annotation (and multiple Speaker annotations)
 * Updated by JCasGen Wed Feb 17 17:49:40 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.api/src/main/java/de/unistuttgart/quadrama/api/Types.xml
 * @generated */
public class Figure extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Figure.class);
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
  protected Figure() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Figure(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Figure(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Figure(JCas jcas, int begin, int end) {
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
  //* Feature: Id

  /** getter for Id - gets 
   * @generated
   * @return value of the feature 
   */
  public int getId() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_Id == null)
      jcasType.jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.api.Figure");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Figure_Type)jcasType).casFeatCode_Id);}
    
  /** setter for Id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(int v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_Id == null)
      jcasType.jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setIntValue(addr, ((Figure_Type)jcasType).casFeatCode_Id, v);}    
  }

    