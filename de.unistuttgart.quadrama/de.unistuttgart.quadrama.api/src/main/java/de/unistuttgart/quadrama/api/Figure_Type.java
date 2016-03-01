
/* First created by JCasGen Wed Feb 17 17:32:06 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Represents on figure in the play. Each figure should only have one Figure annotation (and multiple Speaker annotations)
 * Updated by JCasGen Tue Mar 01 20:10:54 CET 2016
 * @generated */
public class Figure_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Figure_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Figure_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Figure(addr, Figure_Type.this);
  			   Figure_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Figure(addr, Figure_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Figure.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.api.Figure");



  /** @generated */
  final Feature casFeat_Id;
  /** @generated */
  final int     casFeatCode_Id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getId(int addr) {
        if (featOkTst && casFeat_Id == null)
      jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getIntValue(addr, casFeatCode_Id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setId(int addr, int v) {
        if (featOkTst && casFeat_Id == null)
      jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setIntValue(addr, casFeatCode_Id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Description;
  /** @generated */
  final int     casFeatCode_Description;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDescription(int addr) {
        if (featOkTst && casFeat_Description == null)
      jcas.throwFeatMissing("Description", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Description);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDescription(int addr, String v) {
        if (featOkTst && casFeat_Description == null)
      jcas.throwFeatMissing("Description", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setStringValue(addr, casFeatCode_Description, v);}
    
  
 
  /** @generated */
  final Feature casFeat_NumberOfUtterances;
  /** @generated */
  final int     casFeatCode_NumberOfUtterances;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public long getNumberOfUtterances(int addr) {
        if (featOkTst && casFeat_NumberOfUtterances == null)
      jcas.throwFeatMissing("NumberOfUtterances", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getLongValue(addr, casFeatCode_NumberOfUtterances);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumberOfUtterances(int addr, long v) {
        if (featOkTst && casFeat_NumberOfUtterances == null)
      jcas.throwFeatMissing("NumberOfUtterances", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setLongValue(addr, casFeatCode_NumberOfUtterances, v);}
    
  
 
  /** @generated */
  final Feature casFeat_NumberOfWords;
  /** @generated */
  final int     casFeatCode_NumberOfWords;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNumberOfWords(int addr) {
        if (featOkTst && casFeat_NumberOfWords == null)
      jcas.throwFeatMissing("NumberOfWords", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getIntValue(addr, casFeatCode_NumberOfWords);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumberOfWords(int addr, int v) {
        if (featOkTst && casFeat_NumberOfWords == null)
      jcas.throwFeatMissing("NumberOfWords", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setIntValue(addr, casFeatCode_NumberOfWords, v);}
    
  
 
  /** @generated */
  final Feature casFeat_UtteranceLengthArithmeticMean;
  /** @generated */
  final int     casFeatCode_UtteranceLengthArithmeticMean;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getUtteranceLengthArithmeticMean(int addr) {
        if (featOkTst && casFeat_UtteranceLengthArithmeticMean == null)
      jcas.throwFeatMissing("UtteranceLengthArithmeticMean", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_UtteranceLengthArithmeticMean);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUtteranceLengthArithmeticMean(int addr, double v) {
        if (featOkTst && casFeat_UtteranceLengthArithmeticMean == null)
      jcas.throwFeatMissing("UtteranceLengthArithmeticMean", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_UtteranceLengthArithmeticMean, v);}
    
  
 
  /** @generated */
  final Feature casFeat_UtteranceLengthStandardDeviation;
  /** @generated */
  final int     casFeatCode_UtteranceLengthStandardDeviation;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getUtteranceLengthStandardDeviation(int addr) {
        if (featOkTst && casFeat_UtteranceLengthStandardDeviation == null)
      jcas.throwFeatMissing("UtteranceLengthStandardDeviation", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_UtteranceLengthStandardDeviation);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUtteranceLengthStandardDeviation(int addr, double v) {
        if (featOkTst && casFeat_UtteranceLengthStandardDeviation == null)
      jcas.throwFeatMissing("UtteranceLengthStandardDeviation", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_UtteranceLengthStandardDeviation, v);}
    
  
 
  /** @generated */
  final Feature casFeat_UtteranceLengthMax;
  /** @generated */
  final int     casFeatCode_UtteranceLengthMax;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getUtteranceLengthMax(int addr) {
        if (featOkTst && casFeat_UtteranceLengthMax == null)
      jcas.throwFeatMissing("UtteranceLengthMax", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getIntValue(addr, casFeatCode_UtteranceLengthMax);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUtteranceLengthMax(int addr, int v) {
        if (featOkTst && casFeat_UtteranceLengthMax == null)
      jcas.throwFeatMissing("UtteranceLengthMax", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setIntValue(addr, casFeatCode_UtteranceLengthMax, v);}
    
  
 
  /** @generated */
  final Feature casFeat_UtteranceLengthMin;
  /** @generated */
  final int     casFeatCode_UtteranceLengthMin;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getUtteranceLengthMin(int addr) {
        if (featOkTst && casFeat_UtteranceLengthMin == null)
      jcas.throwFeatMissing("UtteranceLengthMin", "de.unistuttgart.quadrama.api.Figure");
    return ll_cas.ll_getIntValue(addr, casFeatCode_UtteranceLengthMin);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUtteranceLengthMin(int addr, int v) {
        if (featOkTst && casFeat_UtteranceLengthMin == null)
      jcas.throwFeatMissing("UtteranceLengthMin", "de.unistuttgart.quadrama.api.Figure");
    ll_cas.ll_setIntValue(addr, casFeatCode_UtteranceLengthMin, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Figure_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Id = jcas.getRequiredFeatureDE(casType, "Id", "uima.cas.Integer", featOkTst);
    casFeatCode_Id  = (null == casFeat_Id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Id).getCode();

 
    casFeat_Description = jcas.getRequiredFeatureDE(casType, "Description", "uima.cas.String", featOkTst);
    casFeatCode_Description  = (null == casFeat_Description) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Description).getCode();

 
    casFeat_NumberOfUtterances = jcas.getRequiredFeatureDE(casType, "NumberOfUtterances", "uima.cas.Long", featOkTst);
    casFeatCode_NumberOfUtterances  = (null == casFeat_NumberOfUtterances) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_NumberOfUtterances).getCode();

 
    casFeat_NumberOfWords = jcas.getRequiredFeatureDE(casType, "NumberOfWords", "uima.cas.Integer", featOkTst);
    casFeatCode_NumberOfWords  = (null == casFeat_NumberOfWords) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_NumberOfWords).getCode();

 
    casFeat_UtteranceLengthArithmeticMean = jcas.getRequiredFeatureDE(casType, "UtteranceLengthArithmeticMean", "uima.cas.Double", featOkTst);
    casFeatCode_UtteranceLengthArithmeticMean  = (null == casFeat_UtteranceLengthArithmeticMean) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UtteranceLengthArithmeticMean).getCode();

 
    casFeat_UtteranceLengthStandardDeviation = jcas.getRequiredFeatureDE(casType, "UtteranceLengthStandardDeviation", "uima.cas.Double", featOkTst);
    casFeatCode_UtteranceLengthStandardDeviation  = (null == casFeat_UtteranceLengthStandardDeviation) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UtteranceLengthStandardDeviation).getCode();

 
    casFeat_UtteranceLengthMax = jcas.getRequiredFeatureDE(casType, "UtteranceLengthMax", "uima.cas.Integer", featOkTst);
    casFeatCode_UtteranceLengthMax  = (null == casFeat_UtteranceLengthMax) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UtteranceLengthMax).getCode();

 
    casFeat_UtteranceLengthMin = jcas.getRequiredFeatureDE(casType, "UtteranceLengthMin", "uima.cas.Integer", featOkTst);
    casFeatCode_UtteranceLengthMin  = (null == casFeat_UtteranceLengthMin) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_UtteranceLengthMin).getCode();

  }
}



    