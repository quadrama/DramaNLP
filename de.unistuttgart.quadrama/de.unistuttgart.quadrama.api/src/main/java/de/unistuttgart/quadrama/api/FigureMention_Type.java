
/* First created by JCasGen Thu Feb 18 17:30:21 CET 2016 */
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
 * Updated by JCasGen Thu Feb 18 17:30:21 CET 2016
 * @generated */
public class FigureMention_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (FigureMention_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = FigureMention_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new FigureMention(addr, FigureMention_Type.this);
  			   FigureMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new FigureMention(addr, FigureMention_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = FigureMention.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.api.FigureMention");
 
  /** @generated */
  final Feature casFeat_Figure;
  /** @generated */
  final int     casFeatCode_Figure;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getFigure(int addr) {
        if (featOkTst && casFeat_Figure == null)
      jcas.throwFeatMissing("Figure", "de.unistuttgart.quadrama.api.FigureMention");
    return ll_cas.ll_getRefValue(addr, casFeatCode_Figure);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFigure(int addr, int v) {
        if (featOkTst && casFeat_Figure == null)
      jcas.throwFeatMissing("Figure", "de.unistuttgart.quadrama.api.FigureMention");
    ll_cas.ll_setRefValue(addr, casFeatCode_Figure, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public FigureMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Figure = jcas.getRequiredFeatureDE(casType, "Figure", "de.unistuttgart.quadrama.api.Figure", featOkTst);
    casFeatCode_Figure  = (null == casFeat_Figure) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Figure).getCode();

  }
}



    