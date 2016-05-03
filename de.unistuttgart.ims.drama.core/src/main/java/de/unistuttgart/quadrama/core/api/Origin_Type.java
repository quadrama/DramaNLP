
/* First created by JCasGen Tue Feb 16 12:20:34 CET 2016 */
package de.unistuttgart.quadrama.core.api;

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
 * Updated by JCasGen Tue Feb 16 12:20:34 CET 2016
 * @generated */
public class Origin_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Origin_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Origin_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Origin(addr, Origin_Type.this);
  			   Origin_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Origin(addr, Origin_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Origin.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.core.api.Origin");
 
  /** @generated */
  final Feature casFeat_Offset;
  /** @generated */
  final int     casFeatCode_Offset;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOffset(int addr) {
        if (featOkTst && casFeat_Offset == null)
      jcas.throwFeatMissing("Offset", "de.unistuttgart.quadrama.core.api.Origin");
    return ll_cas.ll_getIntValue(addr, casFeatCode_Offset);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOffset(int addr, int v) {
        if (featOkTst && casFeat_Offset == null)
      jcas.throwFeatMissing("Offset", "de.unistuttgart.quadrama.core.api.Origin");
    ll_cas.ll_setIntValue(addr, casFeatCode_Offset, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Origin_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Offset = jcas.getRequiredFeatureDE(casType, "Offset", "uima.cas.Integer", featOkTst);
    casFeatCode_Offset  = (null == casFeat_Offset) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Offset).getCode();

  }
}



    