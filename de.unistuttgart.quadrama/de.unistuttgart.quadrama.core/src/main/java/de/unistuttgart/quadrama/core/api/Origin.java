

/* First created by JCasGen Tue Feb 16 12:20:34 CET 2016 */
package de.unistuttgart.quadrama.core.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Feb 16 12:20:34 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.core/src/main/java/de/unistuttgart/quadrama/core/api/Types.xml
 * @generated */
public class Origin extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Origin.class);
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
  protected Origin() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Origin(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Origin(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Origin(JCas jcas, int begin, int end) {
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
  //* Feature: Offset

  /** getter for Offset - gets 
   * @generated
   * @return value of the feature 
   */
  public int getOffset() {
    if (Origin_Type.featOkTst && ((Origin_Type)jcasType).casFeat_Offset == null)
      jcasType.jcas.throwFeatMissing("Offset", "de.unistuttgart.quadrama.core.api.Origin");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Origin_Type)jcasType).casFeatCode_Offset);}
    
  /** setter for Offset - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOffset(int v) {
    if (Origin_Type.featOkTst && ((Origin_Type)jcasType).casFeat_Offset == null)
      jcasType.jcas.throwFeatMissing("Offset", "de.unistuttgart.quadrama.core.api.Origin");
    jcasType.ll_cas.ll_setIntValue(addr, ((Origin_Type)jcasType).casFeatCode_Offset, v);}    
  }

    