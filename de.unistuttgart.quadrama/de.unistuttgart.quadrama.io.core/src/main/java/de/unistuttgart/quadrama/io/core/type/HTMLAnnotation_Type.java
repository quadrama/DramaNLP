
/* First created by JCasGen Tue Feb 16 16:02:10 CET 2016 */
package de.unistuttgart.quadrama.io.core.type;

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
 * Updated by JCasGen Tue Feb 16 16:02:10 CET 2016
 * @generated */
public class HTMLAnnotation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (HTMLAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = HTMLAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new HTMLAnnotation(addr, HTMLAnnotation_Type.this);
  			   HTMLAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new HTMLAnnotation(addr, HTMLAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = HTMLAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.io.core.type.HTMLAnnotation");
 
  /** @generated */
  final Feature casFeat_Tag;
  /** @generated */
  final int     casFeatCode_Tag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTag(int addr) {
        if (featOkTst && casFeat_Tag == null)
      jcas.throwFeatMissing("Tag", "de.unistuttgart.quadrama.io.core.type.HTMLAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Tag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTag(int addr, String v) {
        if (featOkTst && casFeat_Tag == null)
      jcas.throwFeatMissing("Tag", "de.unistuttgart.quadrama.io.core.type.HTMLAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Tag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Id;
  /** @generated */
  final int     casFeatCode_Id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getId(int addr) {
        if (featOkTst && casFeat_Id == null)
      jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.io.core.type.HTMLAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_Id == null)
      jcas.throwFeatMissing("Id", "de.unistuttgart.quadrama.io.core.type.HTMLAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Cls;
  /** @generated */
  final int     casFeatCode_Cls;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getCls(int addr) {
        if (featOkTst && casFeat_Cls == null)
      jcas.throwFeatMissing("Cls", "de.unistuttgart.quadrama.io.core.type.HTMLAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Cls);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCls(int addr, String v) {
        if (featOkTst && casFeat_Cls == null)
      jcas.throwFeatMissing("Cls", "de.unistuttgart.quadrama.io.core.type.HTMLAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_Cls, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public HTMLAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Tag = jcas.getRequiredFeatureDE(casType, "Tag", "uima.cas.String", featOkTst);
    casFeatCode_Tag  = (null == casFeat_Tag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Tag).getCode();

 
    casFeat_Id = jcas.getRequiredFeatureDE(casType, "Id", "uima.cas.String", featOkTst);
    casFeatCode_Id  = (null == casFeat_Id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Id).getCode();

 
    casFeat_Cls = jcas.getRequiredFeatureDE(casType, "Cls", "uima.cas.String", featOkTst);
    casFeatCode_Cls  = (null == casFeat_Cls) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Cls).getCode();

  }
}



    