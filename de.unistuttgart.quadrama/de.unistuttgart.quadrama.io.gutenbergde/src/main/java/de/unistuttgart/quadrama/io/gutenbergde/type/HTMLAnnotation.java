

/* First created by JCasGen Sun Feb 14 18:44:13 CET 2016 */
package de.unistuttgart.quadrama.io.gutenbergde.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Sun Feb 14 18:44:13 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.io.gutenbergde/src/main/java/de/unistuttgart/quadrama/io/gutenbergde/Types.xml
 * @generated */
public class HTMLAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HTMLAnnotation.class);
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
  protected HTMLAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public HTMLAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public HTMLAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public HTMLAnnotation(JCas jcas, int begin, int end) {
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
  //* Feature: Tag

  /** getter for Tag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTag() {
    if (HTMLAnnotation_Type.featOkTst && ((HTMLAnnotation_Type)jcasType).casFeat_Tag == null)
      jcasType.jcas.throwFeatMissing("Tag", "de.unistuttgart.quadrama.io.gutenbergde.type.HTMLAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((HTMLAnnotation_Type)jcasType).casFeatCode_Tag);}
    
  /** setter for Tag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTag(String v) {
    if (HTMLAnnotation_Type.featOkTst && ((HTMLAnnotation_Type)jcasType).casFeat_Tag == null)
      jcasType.jcas.throwFeatMissing("Tag", "de.unistuttgart.quadrama.io.gutenbergde.type.HTMLAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((HTMLAnnotation_Type)jcasType).casFeatCode_Tag, v);}    
  }

    