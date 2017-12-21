<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0"
	xmlns="http://www.tei-c.org/ns/1.0">
	<xsl:output method="xml" />

	<xsl:template match="node()|@*" name="identity">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>

	<!-- Detecting scenes -->

	<xsl:template match="tei:div[matches(tei:div/tei:desc/tei:title/text(),'Auftritt\]?$')]">
		<xsl:copy>
			<xsl:attribute name="type">scene</xsl:attribute>
			<xsl:apply-templates select="node() | @*" />
		</xsl:copy>
	</xsl:template>
	
	

	

</xsl:stylesheet>
