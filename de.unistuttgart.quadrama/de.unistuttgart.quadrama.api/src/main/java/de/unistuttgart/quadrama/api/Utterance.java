

/* First created by JCasGen Sun Feb 14 19:09:00 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Feb 15 08:35:31 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.api/src/main/java/de/unistuttgart/quadrama/api/Types.xml
 * @generated */
public class Utterance extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Utterance.class);
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
  protected Utterance() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Utterance(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Utterance(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Utterance(JCas jcas, int begin, int end) {
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
  //* Feature: Speaker

  /** getter for Speaker - gets 
   * @generated
   * @return value of the feature 
   */
  public Speaker getSpeaker() {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_Speaker == null)
      jcasType.jcas.throwFeatMissing("Speaker", "de.unistuttgart.quadrama.api.Utterance");
    return (Speaker)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Utterance_Type)jcasType).casFeatCode_Speaker)));}
    
  /** setter for Speaker - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeaker(Speaker v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_Speaker == null)
      jcasType.jcas.throwFeatMissing("Speaker", "de.unistuttgart.quadrama.api.Utterance");
    jcasType.ll_cas.ll_setRefValue(addr, ((Utterance_Type)jcasType).casFeatCode_Speaker, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: Stage

  /** getter for Stage - gets 
   * @generated
   * @return value of the feature 
   */
  public StageDirection getStage() {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_Stage == null)
      jcasType.jcas.throwFeatMissing("Stage", "de.unistuttgart.quadrama.api.Utterance");
    return (StageDirection)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Utterance_Type)jcasType).casFeatCode_Stage)));}
    
  /** setter for Stage - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStage(StageDirection v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_Stage == null)
      jcasType.jcas.throwFeatMissing("Stage", "de.unistuttgart.quadrama.api.Utterance");
    jcasType.ll_cas.ll_setRefValue(addr, ((Utterance_Type)jcasType).casFeatCode_Stage, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: Speech

  /** getter for Speech - gets 
   * @generated
   * @return value of the feature 
   */
  public Speech getSpeech() {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_Speech == null)
      jcasType.jcas.throwFeatMissing("Speech", "de.unistuttgart.quadrama.api.Utterance");
    return (Speech)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Utterance_Type)jcasType).casFeatCode_Speech)));}
    
  /** setter for Speech - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpeech(Speech v) {
    if (Utterance_Type.featOkTst && ((Utterance_Type)jcasType).casFeat_Speech == null)
      jcasType.jcas.throwFeatMissing("Speech", "de.unistuttgart.quadrama.api.Utterance");
    jcasType.ll_cas.ll_setRefValue(addr, ((Utterance_Type)jcasType).casFeatCode_Speech, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    