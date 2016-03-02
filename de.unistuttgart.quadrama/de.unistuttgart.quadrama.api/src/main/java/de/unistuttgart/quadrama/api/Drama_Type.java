/* First created by JCasGen Tue Feb 16 08:52:51 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData_Type;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.DocumentAnnotation_Type;

/** 
 * Updated by JCasGen Wed Mar 02 06:00:57 CET 2016
 * @generated */
public class Drama_Type extends DocumentMetaData_Type {
	/**
	 * @generated
	 * @return the generator for this type
	 */
	@Override
	protected FSGenerator getFSGenerator() {return fsGenerator;}
	/** @generated */
	private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (Drama_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = Drama_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new Drama(addr, Drama_Type.this);
  			   Drama_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new Drama(addr, Drama_Type.this);
  	  }
    };
	/** @generated */
	@SuppressWarnings("hiding")
	public final static int typeIndexID = Drama.typeIndexID;
	/**
	 * @generated
	 * @modifiable
	 */
	@SuppressWarnings("hiding")
	public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.api.Drama");

  /** @generated */
  final Feature casFeat_Authorname;
  /** @generated */
  final int     casFeatCode_Authorname;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAuthorname(int addr) {
        if (featOkTst && casFeat_Authorname == null)
      jcas.throwFeatMissing("Authorname", "de.unistuttgart.quadrama.api.Drama");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Authorname);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAuthorname(int addr, String v) {
        if (featOkTst && casFeat_Authorname == null)
      jcas.throwFeatMissing("Authorname", "de.unistuttgart.quadrama.api.Drama");
    ll_cas.ll_setStringValue(addr, casFeatCode_Authorname, v);}
    
  
 
  /** @generated */
  final Feature casFeat_DramaId;
  /** @generated */
  final int     casFeatCode_DramaId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDramaId(int addr) {
        if (featOkTst && casFeat_DramaId == null)
      jcas.throwFeatMissing("DramaId", "de.unistuttgart.quadrama.api.Drama");
    return ll_cas.ll_getStringValue(addr, casFeatCode_DramaId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDramaId(int addr, String v) {
        if (featOkTst && casFeat_DramaId == null)
      jcas.throwFeatMissing("DramaId", "de.unistuttgart.quadrama.api.Drama");
    ll_cas.ll_setStringValue(addr, casFeatCode_DramaId, v);}
    
  



	/**
	 * initialize variables to correspond with Cas Type and Features
	 * 
	 * @generated
	 * @param jcas
	 *            JCas
	 * @param casType
	 *            Type
	 */
	public Drama_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Authorname = jcas.getRequiredFeatureDE(casType, "Authorname", "uima.cas.String", featOkTst);
    casFeatCode_Authorname  = (null == casFeat_Authorname) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Authorname).getCode();

 
    casFeat_DramaId = jcas.getRequiredFeatureDE(casType, "DramaId", "uima.cas.String", featOkTst);
    casFeatCode_DramaId  = (null == casFeat_DramaId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_DramaId).getCode();

  }
}
