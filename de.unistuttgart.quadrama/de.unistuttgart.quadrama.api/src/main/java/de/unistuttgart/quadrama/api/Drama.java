
/* First created by JCasGen Tue Feb 16 08:52:51 CET 2016 */
package de.unistuttgart.quadrama.api;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;


import org.apache.uima.jcas.tcas.DocumentAnnotation;

/** 
 * Updated by JCasGen Tue Feb 16 08:58:26 CET 2016
 * XML source: /Users/reiterns/Documents/DH/anglogerman/de.unistuttgart.quadrama/de.unistuttgart.quadrama.api/src/main/java/de/unistuttgart/quadrama/api/Types.xml
 * @generated */
public class Drama extends DocumentMetaData {
	/**
	 * @generated
	 * @ordered
	 */
	@SuppressWarnings("hiding")
	public final static int typeIndexID = JCasRegistry.register(Drama.class);
	/**
	 * @generated
	 * @ordered
	 */
	@SuppressWarnings("hiding")
	public final static int type = typeIndexID;

	/**
	 * @generated
	 * @return index of the type
	 */
	@Override
	public int getTypeIndexID() {return typeIndexID;}
 
	/**
	 * Never called. Disable default constructor
	 * 
	 * @generated
	 */
	protected Drama() {/* intentionally empty block */}
    
	/**
	 * Internal - constructor used by generator
	 * 
	 * @generated
	 * @param addr
	 *            low level Feature Structure reference
	 * @param type
	 *            the type of this Feature Structure
	 */
	public Drama(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
	/**
	 * @generated
	 * @param jcas
	 *            JCas to which this Feature Structure belongs
	 */
	public Drama(JCas jcas) {
    super(jcas);
    readObject();   
  } 

	/**
	 * @generated
	 * @param jcas
	 *            JCas to which this Feature Structure belongs
	 * @param begin
	 *            offset to the begin spot in the SofA
	 * @param end
	 *            offset to the end spot in the SofA
	 */
	public Drama(JCas jcas, int begin, int end) {
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
	private void readObject() {/* default - does nothing empty block */}

}
