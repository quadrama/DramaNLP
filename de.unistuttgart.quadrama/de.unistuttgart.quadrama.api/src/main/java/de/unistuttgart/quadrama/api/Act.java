

/* First created by JCasGen Sun Feb 14 17:15:54 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Feb 14 19:09:00 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.api/src/main/java/de/unistuttgart/quadrama/api/Types.xml
 * @generated */
public class Act extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Act.class);
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
  protected Act() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Act(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Act(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Act(JCas jcas, int begin, int end) {
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
  //* Feature: Number

  /** getter for Number - gets 
   * @generated
   * @return value of the feature 
   */
  public int getNumber() {
    if (Act_Type.featOkTst && ((Act_Type)jcasType).casFeat_Number == null)
      jcasType.jcas.throwFeatMissing("Number", "de.unistuttgart.quadrama.api.Act");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Act_Type)jcasType).casFeatCode_Number);}
    
  /** setter for Number - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumber(int v) {
    if (Act_Type.featOkTst && ((Act_Type)jcasType).casFeat_Number == null)
      jcasType.jcas.throwFeatMissing("Number", "de.unistuttgart.quadrama.api.Act");
    jcasType.ll_cas.ll_setIntValue(addr, ((Act_Type)jcasType).casFeatCode_Number, v);}    
  }

    