
/* First created by JCasGen Tue Mar 01 14:15:04 CET 2016 */
package de.unistuttgart.quadrama.graph.ext.api;

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
 * Updated by JCasGen Tue Mar 01 14:15:04 CET 2016
 * @generated */
public class GraphMetaData_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (GraphMetaData_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = GraphMetaData_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new GraphMetaData(addr, GraphMetaData_Type.this);
  			   GraphMetaData_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new GraphMetaData(addr, GraphMetaData_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = GraphMetaData.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
 
  /** @generated */
  final Feature casFeat_GraphClassName;
  /** @generated */
  final int     casFeatCode_GraphClassName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getGraphClassName(int addr) {
        if (featOkTst && casFeat_GraphClassName == null)
      jcas.throwFeatMissing("GraphClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    return ll_cas.ll_getStringValue(addr, casFeatCode_GraphClassName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setGraphClassName(int addr, String v) {
        if (featOkTst && casFeat_GraphClassName == null)
      jcas.throwFeatMissing("GraphClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    ll_cas.ll_setStringValue(addr, casFeatCode_GraphClassName, v);}
    
  
 
  /** @generated */
  final Feature casFeat_EdgeClassName;
  /** @generated */
  final int     casFeatCode_EdgeClassName;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getEdgeClassName(int addr) {
        if (featOkTst && casFeat_EdgeClassName == null)
      jcas.throwFeatMissing("EdgeClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    return ll_cas.ll_getStringValue(addr, casFeatCode_EdgeClassName);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEdgeClassName(int addr, String v) {
        if (featOkTst && casFeat_EdgeClassName == null)
      jcas.throwFeatMissing("EdgeClassName", "de.unistuttgart.quadrama.graph.ext.api.GraphMetaData");
    ll_cas.ll_setStringValue(addr, casFeatCode_EdgeClassName, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public GraphMetaData_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_GraphClassName = jcas.getRequiredFeatureDE(casType, "GraphClassName", "uima.cas.String", featOkTst);
    casFeatCode_GraphClassName  = (null == casFeat_GraphClassName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_GraphClassName).getCode();

 
    casFeat_EdgeClassName = jcas.getRequiredFeatureDE(casType, "EdgeClassName", "uima.cas.String", featOkTst);
    casFeatCode_EdgeClassName  = (null == casFeat_EdgeClassName) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_EdgeClassName).getCode();

  }
}



    