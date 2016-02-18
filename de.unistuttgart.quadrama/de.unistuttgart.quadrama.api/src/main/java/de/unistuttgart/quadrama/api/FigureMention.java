

/* First created by JCasGen Thu Feb 18 17:30:21 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu Feb 18 17:30:21 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.api/src/main/java/de/unistuttgart/quadrama/api/Types.xml
 * @generated */
public class FigureMention extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(FigureMention.class);
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
  protected FigureMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public FigureMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public FigureMention(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public FigureMention(JCas jcas, int begin, int end) {
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
  //* Feature: Figure

  /** getter for Figure - gets 
   * @generated
   * @return value of the feature 
   */
  public Figure getFigure() {
    if (FigureMention_Type.featOkTst && ((FigureMention_Type)jcasType).casFeat_Figure == null)
      jcasType.jcas.throwFeatMissing("Figure", "de.unistuttgart.quadrama.api.FigureMention");
    return (Figure)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((FigureMention_Type)jcasType).casFeatCode_Figure)));}
    
  /** setter for Figure - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFigure(Figure v) {
    if (FigureMention_Type.featOkTst && ((FigureMention_Type)jcasType).casFeat_Figure == null)
      jcasType.jcas.throwFeatMissing("Figure", "de.unistuttgart.quadrama.api.FigureMention");
    jcasType.ll_cas.ll_setRefValue(addr, ((FigureMention_Type)jcasType).casFeatCode_Figure, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    