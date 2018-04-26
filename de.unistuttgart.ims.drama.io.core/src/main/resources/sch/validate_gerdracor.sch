<!-- TODO: -->
<schema xmlns="http://purl.oclc.org/dsdl/schematron" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" queryBinding="xslt">

  <title>ISO-Schematron schema to validate TEI-documents from the 'gerdracor' corpus</title>
  <ns prefix="tei" uri="http://www.tei-c.org/ns/1.0"/>


  <!-- global cast_ids xslt-key -->
  <xsl:key name="cast_ids" match="tei:listPerson/*[self::tei:person or child::tei:persName]" use="@xml:id"/>

  <!-- TODO: tests for basic structure and mandatory elements-->
  <pattern id="structure">
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
    <rule context="tei:TEI/tei:teiHeader/tei:fileDesc">
      <assert test="count(tei:titleStmt) = 1">
        The 'fileDesc'-element must contain one 'titleStmt'-element.
      </assert>
    </rule>
    <rule context="tei:TEI/tei:teiHeader/tei:fileDesc/tei:titleStmt">
      <assert test="count(tei:title) = 1">
        The 'titleStmt'-element must contain one 'title'-element.
      </assert>
      <assert test="tei:title/text()">
        The 'title'-element in the 'titleStmt'-element can not be empty.
      </assert>
      <assert test="count(tei:author) = 1">
        The 'titleStmt'-element must contain one 'author'-element.
      </assert>
      <assert test="tei:author/text()">
        The 'author'-element in the 'titleStmt'-element can not be empty.
      </assert>
    </rule>
  </pattern>

  <!-- test for drama-id -->
  <pattern id="drama-id">
    <rule context="tei:TEI/teiHeader/tei:fileDesc">
      <assert test="count(tei:sourceDesc) = 1">
        The 'file-Desc'-element must contain one 'sourceDesc'-element.
      </assert>
    </rule>
    <rule context="tei:TEI/tei:teiHeader/tei:fileDesc/tei:sourceDesc">
      <assert test ="count(tei:bibl) = 1">
        The 'sourceDesc'-element must contain one 'bibl'-element
      </assert>
    </rule>
    <rule context="tei:TEI/tei:teiHeader/tei:fileDesc/tei:sourceDesc/tei:bibl/tei:idno">
      <assert test=".">
        The 'sourceDesc'-element in the 'fileDesc'-element must contain a
        'bibl'-element with an 'idno'-element of type 'URL'.
      </assert>
      <assert test="text()">
        The 'idno'-element in the 'bibl'-element in the 'sourceDesc' can not be empty.
      </assert>
    </rule>
  </pattern>

  <!-- test for listPerson -->
  <pattern id="listPerson">
    <rule context="tei:TEI/tei:teiHeader/tei:profileDesc/tei:particDesc/tei:listPerson">
      <assert test=".">
        The 'profileDesc'-element must contain a 'particDesc'-element, which must contain a 'listPerson'-element.
      </assert>
      <assert test="count(tei:person) >= 1">
        There must be at least one 'person' element in the 'listPerson'-element.
      </assert>
    </rule>
    <rule context="tei:TEI/tei:teiHeader/tei:profileDesc/tei:particDesc/tei:listPerson//tei:person">
      <assert test="@xml:id">
        Each 'person'-element in the 'listPerson'-element has to have an 'xml:id'-attribute.
      </assert>
      <assert test="count(tei:persName) >= 1">
        Each 'person'-element in the 'listPerson'-element hast to have at least one child 'persName'-element.
      </assert>
    </rule>
  </pattern>

  <!-- tests for structure in acts -->
  <!-- TODO: redundancy through count(tei:div[@type='act']) > 0 (might be
  too general) -->
  <pattern id="Act">
    <rule context="tei:TEI/tei:text/tei:body">
      <assert test="((count(tei:div[@type='act']) = count(tei:div) - 1) and not
                    (*[1]/@type='act'))
                    or
                    (count(tei:div[@type='act']) = count(tei:div))
                    or
                    (count(tei:div[@type='act']) > 0)
                    or
                    (count(tei:div[@type='act']) = 0 and count(tei:div//tei:div[@type='text']) > 0)">
        The 'body'-element must have at least one child-element 'div' with
        type = 'act'. Otherwise, the body has to be structured with
        'div'-elements of type 'text'.
      </assert>
    </rule>
  </pattern>

  <!-- tests for structure in scenes -->
  <!-- TODO: possibly too general, error message not final-->
  <pattern id="Scene">
    <rule context="tei:TEI/tei:text/tei:body//tei:div[@type='act']">
      <assert test="(count(//tei:div[@type='scene']) = count(tei:div) - 1)
                    or (count(//tei:div[@type='scene']) = 0)
                    or (count(//tei:div[@type='scene']) > 0)">
        Wrong structure on scene-level.
      </assert>
    </rule>
  </pattern>

  <!-- tests for structure of 'sp'-elements -->
  <pattern id="Speaker">
    <rule context="tei:TEI/tei:text//tei:sp">
      <let name="who" value="@who"/>
      <let name="spid" value="substring($who, 2, string-length($who))"/>
      <assert test="count(tei:speaker) = 1">
        All 'sp'-elements must have one 'speaker' element.
      </assert>
      <assert test="(count(tei:lg) + count(tei:stage) > 0) or
                    (count(tei:l) + count(tei:stage) > 0) or
                    (count(tei:p) + count(tei:stage) > 0)">
        All 'sp'-elements must have at least one 'lg'-, 'l'- or 'stage'-element.
      </assert>

    <!--
    TODO:
    This tests if every id that appears in a sp-element matches one in
    the listPerson.
    PROBLEM WITH GERDRACOR-DATA: there are ids in the 'who'-attributes that do
    not completely match the 'xml:id'-attributes (eg. 'erster_reiter_bamberg'
    in 'listPerson' vs. 'erster_reiter' in 'sp'-elements)
      <assert test="key('cast_ids', $spid)">
        All '@who'-attributes of 'sp' elements must correspond to a
        'xml:id'-attribute of a 'person'- or 'persName'-element in the
        'listPerson' (without '#' at the beginning).
      </assert>
    -->
    </rule>
  </pattern>
</schema>
