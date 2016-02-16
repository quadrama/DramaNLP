

/* First created by JCasGen Sun Feb 14 17:15:54 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Feb 16 21:59:45 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.api/src/main/java/de/unistuttgart/quadrama/api/Types.xml
 * @generated */
public class Speaker extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Speaker.class);
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
  protected Speaker() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Speaker(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Speaker(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Speaker(JCas jcas, int begin, int end) {
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
    if (Speaker_Type.featOkTst && ((Speaker_Type)jcasType).casFeat_Id == null)
      jcasType.jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.api.Speaker");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Speaker_Type)jcasType).casFeatCode_Id);}
    
  /** setter for Id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(int v) {
    if (Speaker_Type.featOkTst && ((Speaker_Type)jcasType).casFeat_Id == null)
      jcasType.jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.api.Speaker");
    jcasType.ll_cas.ll_setIntValue(addr, ((Speaker_Type)jcasType).casFeatCode_Id, v);}    
   
    
  //*--------------*
  //* Feature: CastMember

  /** getter for CastMember - gets 
   * @generated
   * @return value of the feature 
   */
  public CastMember getCastMember() {
    if (Speaker_Type.featOkTst && ((Speaker_Type)jcasType).casFeat_CastMember == null)
      jcasType.jcas.throwFeatMissing("CastMember", "de.unistuttgart.quadrama.api.Speaker");
    return (CastMember)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Speaker_Type)jcasType).casFeatCode_CastMember)));}
    
  /** setter for CastMember - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCastMember(CastMember v) {
    if (Speaker_Type.featOkTst && ((Speaker_Type)jcasType).casFeat_CastMember == null)
      jcasType.jcas.throwFeatMissing("CastMember", "de.unistuttgart.quadrama.api.Speaker");
    jcasType.ll_cas.ll_setRefValue(addr, ((Speaker_Type)jcasType).casFeatCode_CastMember, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    