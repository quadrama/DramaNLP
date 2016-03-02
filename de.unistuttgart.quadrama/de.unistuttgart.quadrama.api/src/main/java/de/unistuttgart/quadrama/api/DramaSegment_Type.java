
/* First created by JCasGen Wed Mar 02 11:54:15 CET 2016 */
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
 * Updated by JCasGen Wed Mar 02 11:54:15 CET 2016
 * @generated */
public class DramaSegment_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DramaSegment_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DramaSegment_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DramaSegment(addr, DramaSegment_Type.this);
  			   DramaSegment_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DramaSegment(addr, DramaSegment_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DramaSegment.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.api.DramaSegment");
 
  /** @generated */
  final Feature casFeat_Number;
  /** @generated */
  final int     casFeatCode_Number;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getNumber(int addr) {
        if (featOkTst && casFeat_Number == null)
      jcas.throwFeatMissing("Number", "de.unistuttgart.quadrama.api.DramaSegment");
    return ll_cas.ll_getIntValue(addr, casFeatCode_Number);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNumber(int addr, int v) {
        if (featOkTst && casFeat_Number == null)
      jcas.throwFeatMissing("Number", "de.unistuttgart.quadrama.api.DramaSegment");
    ll_cas.ll_setIntValue(addr, casFeatCode_Number, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DramaSegment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Number = jcas.getRequiredFeatureDE(casType, "Number", "uima.cas.Integer", featOkTst);
    casFeatCode_Number  = (null == casFeat_Number) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Number).getCode();

  }
}



    