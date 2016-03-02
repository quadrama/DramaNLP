

/* First created by JCasGen Wed Feb 17 17:32:06 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Represents on figure in the play. Each figure should only have one Figure annotation (and multiple Speaker annotations)
 * Updated by JCasGen Wed Mar 02 11:54:15 CET 2016
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
   
    
  //*--------------*
  //* Feature: Description

  /** getter for Description - gets 
   * @generated
   * @return value of the feature 
   */
  public FigureDescription getDescription() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_Description == null)
      jcasType.jcas.throwFeatMissing("Description", "de.unistuttgart.quadrama.api.Figure");
    return (FigureDescription)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Figure_Type)jcasType).casFeatCode_Description)));}
    
  /** setter for Description - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDescription(FigureDescription v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_Description == null)
      jcasType.jcas.throwFeatMissing("Description", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setRefValue(addr, ((Figure_Type)jcasType).casFeatCode_Description, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: NumberOfUtterances

  /** getter for NumberOfUtterances - gets 
   * @generated
   * @return value of the feature 
   */
  public long getNumberOfUtterances() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_NumberOfUtterances == null)
      jcasType.jcas.throwFeatMissing("NumberOfUtterances", "de.unistuttgart.quadrama.api.Figure");
    return jcasType.ll_cas.ll_getLongValue(addr, ((Figure_Type)jcasType).casFeatCode_NumberOfUtterances);}
    
  /** setter for NumberOfUtterances - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumberOfUtterances(long v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_NumberOfUtterances == null)
      jcasType.jcas.throwFeatMissing("NumberOfUtterances", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setLongValue(addr, ((Figure_Type)jcasType).casFeatCode_NumberOfUtterances, v);}    
   
    
  //*--------------*
  //* Feature: NumberOfWords

  /** getter for NumberOfWords - gets 
   * @generated
   * @return value of the feature 
   */
  public int getNumberOfWords() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_NumberOfWords == null)
      jcasType.jcas.throwFeatMissing("NumberOfWords", "de.unistuttgart.quadrama.api.Figure");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Figure_Type)jcasType).casFeatCode_NumberOfWords);}
    
  /** setter for NumberOfWords - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumberOfWords(int v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_NumberOfWords == null)
      jcasType.jcas.throwFeatMissing("NumberOfWords", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setIntValue(addr, ((Figure_Type)jcasType).casFeatCode_NumberOfWords, v);}    
   
    
  //*--------------*
  //* Feature: UtteranceLengthArithmeticMean

  /** getter for UtteranceLengthArithmeticMean - gets 
   * @generated
   * @return value of the feature 
   */
  public double getUtteranceLengthArithmeticMean() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthArithmeticMean == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthArithmeticMean", "de.unistuttgart.quadrama.api.Figure");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthArithmeticMean);}
    
  /** setter for UtteranceLengthArithmeticMean - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUtteranceLengthArithmeticMean(double v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthArithmeticMean == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthArithmeticMean", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthArithmeticMean, v);}    
   
    
  //*--------------*
  //* Feature: UtteranceLengthStandardDeviation

  /** getter for UtteranceLengthStandardDeviation - gets 
   * @generated
   * @return value of the feature 
   */
  public double getUtteranceLengthStandardDeviation() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthStandardDeviation == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthStandardDeviation", "de.unistuttgart.quadrama.api.Figure");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthStandardDeviation);}
    
  /** setter for UtteranceLengthStandardDeviation - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUtteranceLengthStandardDeviation(double v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthStandardDeviation == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthStandardDeviation", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthStandardDeviation, v);}    
   
    
  //*--------------*
  //* Feature: UtteranceLengthMax

  /** getter for UtteranceLengthMax - gets 
   * @generated
   * @return value of the feature 
   */
  public int getUtteranceLengthMax() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthMax == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthMax", "de.unistuttgart.quadrama.api.Figure");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthMax);}
    
  /** setter for UtteranceLengthMax - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUtteranceLengthMax(int v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthMax == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthMax", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setIntValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthMax, v);}    
   
    
  //*--------------*
  //* Feature: UtteranceLengthMin

  /** getter for UtteranceLengthMin - gets 
   * @generated
   * @return value of the feature 
   */
  public int getUtteranceLengthMin() {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthMin == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthMin", "de.unistuttgart.quadrama.api.Figure");
    return jcasType.ll_cas.ll_getIntValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthMin);}
    
  /** setter for UtteranceLengthMin - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUtteranceLengthMin(int v) {
    if (Figure_Type.featOkTst && ((Figure_Type)jcasType).casFeat_UtteranceLengthMin == null)
      jcasType.jcas.throwFeatMissing("UtteranceLengthMin", "de.unistuttgart.quadrama.api.Figure");
    jcasType.ll_cas.ll_setIntValue(addr, ((Figure_Type)jcasType).casFeatCode_UtteranceLengthMin, v);}    
  }

    