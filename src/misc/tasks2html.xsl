<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="html" indent="yes"/>

<xsl:template match="tasklist">

   <html>
      <head>
         <title> <xsl:value-of select="@name"/> </title>
      </head>
      <body>
         <h1> <xsl:value-of select="@name"/> </h1>
         <xsl:apply-templates select="group"/>
      </body>
   </html>

</xsl:template>

<xsl:template match="group">
   <h2> <xsl:value-of select="@name"/> </h2>

   <table width="90%" border="1">
      <tr bgcolor="#aaaaaa">
         <td>Status</td>
         <td>Description</td>
      </tr>
      <xsl:for-each select="item">
         <tr>
            <td> <xsl:value-of select="@status"/> </td>
            <td> <xsl:value-of select="."/> </td>
         </tr>
      </xsl:for-each>
   </table>

</xsl:template>

</xsl:stylesheet>

