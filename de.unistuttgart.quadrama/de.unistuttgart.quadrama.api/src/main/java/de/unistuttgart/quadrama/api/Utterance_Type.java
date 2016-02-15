
/* First created by JCasGen Sun Feb 14 19:09:00 CET 2016 */
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

/** 
 * Updated by JCasGen Mon Feb 15 08:35:31 CET 2016
 * @generated */
public class Utterance_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Utterance_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Utterance_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Utterance(addr, Utterance_Type.this);
  			   Utterance_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Utterance(addr, Utterance_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Utterance.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.api.Utterance");
 
  /** @generated */
  final Feature casFeat_Speaker;
  /** @generated */
  final int     casFeatCode_Speaker;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSpeaker(int addr) {
        if (featOkTst && casFeat_Speaker == null)
      jcas.throwFeatMissing("Speaker", "de.unistuttgart.quadrama.api.Utterance");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Speaker);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpeaker(int addr, int v) {
        if (featOkTst && casFeat_Speaker == null)
      jcas.throwFeatMissing("Speaker", "de.unistuttgart.quadrama.api.Utterance");
    ll_cas.ll_setRefValue(addr, casFeatCode_Speaker, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Stage;
  /** @generated */
  final int     casFeatCode_Stage;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getStage(int addr) {
        if (featOkTst && casFeat_Stage == null)
      jcas.throwFeatMissing("Stage", "de.unistuttgart.quadrama.api.Utterance");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Stage);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStage(int addr, int v) {
        if (featOkTst && casFeat_Stage == null)
      jcas.throwFeatMissing("Stage", "de.unistuttgart.quadrama.api.Utterance");
    ll_cas.ll_setRefValue(addr, casFeatCode_Stage, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Speech;
  /** @generated */
  final int     casFeatCode_Speech;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSpeech(int addr) {
        if (featOkTst && casFeat_Speech == null)
      jcas.throwFeatMissing("Speech", "de.unistuttgart.quadrama.api.Utterance");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Speech);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpeech(int addr, int v) {
        if (featOkTst && casFeat_Speech == null)
      jcas.throwFeatMissing("Speech", "de.unistuttgart.quadrama.api.Utterance");
    ll_cas.ll_setRefValue(addr, casFeatCode_Speech, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Utterance_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Speaker = jcas.getRequiredFeatureDE(casType, "Speaker", "de.unistuttgart.quadrama.api.Speaker", featOkTst);
    casFeatCode_Speaker  = (null == casFeat_Speaker) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Speaker).getCode();

 
    casFeat_Stage = jcas.getRequiredFeatureDE(casType, "Stage", "de.unistuttgart.quadrama.api.StageDirection", featOkTst);
    casFeatCode_Stage  = (null == casFeat_Stage) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Stage).getCode();

 
    casFeat_Speech = jcas.getRequiredFeatureDE(casType, "Speech", "de.unistuttgart.quadrama.api.Speech", featOkTst);
    casFeatCode_Speech  = (null == casFeat_Speech) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Speech).getCode();

  }
}



    