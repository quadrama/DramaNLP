<schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" queryBinding="xslt">

  <title>ISO-Schematron schema to validate TEI-documents from the 'theatre classique'corpus</title>
  <ns prefix="tei" uri="http://www.tei-c.org/ns/1.0"/>

  <!-- global cast_ids xslt-key -->
  <xsl:key name="cast_ids" match="tei:role" use="@xml:id"/>

  <!-- TODO: add more general tests for basic structure and mandatory elements -->
  <pattern id="teiHeader">
    <rule context="tei:TEI/tei:teiHeader">
      <assert test=".">
        The file must contain a 'teiHeader'-element.
      </assert>
      <assert test="tei:fileDesc">
        The 'teiHeader'-element must contain a 'fileDesc'-element.
      </assert>
    </rule>
  </pattern>

  <!-- tests for title and author of drama -->
  <pattern id="titleStmt">
    <rule context="tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt">
      <assert test="count(tei:title[@type = 'main']) = 1">
        The 'titleStmt'-element must contain one 'title'-element of type 'main'.
      </assert>
      <assert test="tei:title[@type = 'main']/text()">
        The 'title'-element of type 'main' in the 'titleStmt'-element can not be empty.
      </assert>
    </rule>
    <rule context="tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt/tei:author">
      <assert test="count(tei:name[@type = 'full']) = 1">
        The 'titleStmt'-element must contain one 'author'-element with one 'name'-element of type 'full'.
      </assert>
      <assert test="tei:name[@type = 'full']/text()">
        The 'name'-element of type 'full' in the 'author'-element in the 'titleStmt'-element can not be empty.
      </assert>
    </rule>
  </pattern>

  <!-- test for drama-id -->
  <pattern id="drama-id">
    <rule context="tei:TEI/tei:teiHeader/tei:fileDesc/tei:publicationStmt/tei:idno">
      <assert test="@type = 'cligs'">
        The 'publicationStmt'-element must contain an 'idno'-element of type 'cligs'.
      </assert>
      <assert test="text()">
        The 'idno'-element in the 'publicationStmt' can not be empty.
      </assert>
    </rule>
  </pattern>

  <!-- test for the castList -->
  <pattern id="castList">
    <rule context="tei:TEI/tei:text/tei:front/tei:castList">
      <assert test=".">
        The front-element must contain a 'castList'-element.
      </assert>
      <assert test="count(tei:castItem) >= 1">
        There must be at least one 'castItem' element in the castList.
      </assert>
    </rule>
    <rule context="tei:TEI/tei:text/tei:front/tei:castList//tei:castItem">
      <assert test="tei:role/@xml:id">
        Each role-element in the castList has to have an 'xml:id'-attribute.
      </assert>
    </rule>
  </pattern>

  <!-- tests for structure in acts -->
  <pattern id="Act">
    <rule context="tei:TEI/tei:text/tei:body/child::tei:div">
      <assert test="count(.) >= 1">
        There has to be at least one div-element of type 'act' in the body.
      </assert>
      <assert test="@type = 'act'">
        All top-level div-elements have to have the attribute 'type' with the value 'act'.
      </assert>
    </rule>
  </pattern>

  <!-- tests for structure in scenes -->
  <pattern id="Scene">
    <rule context="tei:TEI/tei:text/tei:body/child::tei:div/child::tei:div">
      <assert test="count(.) >= 1">
        There has to be at least on div-element of type scene in each act.
      </assert>
      <assert test="@type = 'scene'">
        All 'div'-elements that are children of 'div'-elements of type 'act', must be of type 'scene'.
      </assert>
    </rule>
  </pattern>

  <!-- tests for structure of 'sp'-elements -->
  <pattern id="Speaker">
    <rule context="tei:TEI/tei:text/tei:body/child::tei:div/child::tei:div/child::tei:sp">
      <assert test="count(./tei:speaker) = 1">
        All 'sp'-elements must have one 'speaker' element.
      </assert>
      <assert test="count(tei:p|tei:l) + count(tei:stage) > 0">
        All 'sp'-elements must have at least one 'p'-, 'l'- or 'stage'-element.
      </assert>

      <!-- This tests if every id that appears in a sp-element matches one in
            the castList. It does not work for the theatre classique corpus
            yet, because the ids do not match for speakers whose names consist
            of more than one word. -->
      <!--
      <assert test="key('cast_ids', @who)">
        All '@who'-attributes of 'sp' elements must correspond to a 'xml:id'-attribute of a 'role'-element in the 'castList'.
      </assert>
      -->
    </rule>
  </pattern>
</schema>
