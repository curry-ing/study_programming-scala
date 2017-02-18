import scala.xml.XML

val xml2 =
  <sammich>
    <bread>wheat</bread>
    <meat>salami</meat>
    <condiments>
      <condiment expired="true">mayo</condiment>
      <condiment expired="false">mustard</condiment>
    </condiments>
  </sammich>

//XML.save("sammich.xml", xml2, "UTF-8")