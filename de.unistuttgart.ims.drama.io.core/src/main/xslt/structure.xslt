<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:tei="http://www.tei-c.org/ns/1.0" xmlns="http://www.tei-c.org/ns/1.0">
	<xsl:output method="xml" />

	<xsl:template match="node()|@*" name="identity">
		 <xsl:copy>
				 <xsl:apply-templates select="node()|@*"/>
		 </xsl:copy>
 </xsl:template>

	<!-- Detecting acts -->

	<xsl:template match="tei:div[ends-with(@n,'Aufzug')]">
		<xsl:copy>
			<xsl:attribute name="type">act</xsl:attribute>
			<xsl:apply-templates  select="node() | @*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="tei:div[matches(@n,'Akt\]?$')]">
		<xsl:copy>
			<xsl:attribute name="type">act</xsl:attribute>
			<xsl:apply-templates  select="node() | @*"/>
		</xsl:copy>
	</xsl:template>

	<!-- Detecting scenes -->

	<xsl:template match="tei:div[matches(@n,'Auftritt\]?$')]">
	 <xsl:copy>
		 <xsl:attribute name="type">scene</xsl:attribute>
		 <xsl:apply-templates  select="node() | @*"/>
	 </xsl:copy>
 </xsl:template>

	<xsl:template match="tei:div[@type='scene']/tei:div[ends-with(@n,'Auftritt')]" priority="5">
		<xsl:copy>
			<xsl:apply-templates  select="node() | @*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="tei:div[matches(@n,'[0-9]+. Akt\]?/.*$')]" priority="4">
		<xsl:copy>
			<xsl:attribute name="type">scene</xsl:attribute>
			<xsl:apply-templates  select="node() | @*"/>
		</xsl:copy>
	</xsl:template>


	<xsl:template match="tei:div[matches(@n,'Scene\]?$')]">
		<xsl:copy>
			<xsl:attribute name="type">scene</xsl:attribute>
			<xsl:apply-templates  select="node() | @*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="tei:div[matches(@n,'Szene\]?$')]">
		<xsl:copy>
			<xsl:attribute name="type">scene</xsl:attribute>
			<xsl:apply-templates  select="node() | @*"/>
		</xsl:copy>
	</xsl:template>


</xsl:stylesheet>
