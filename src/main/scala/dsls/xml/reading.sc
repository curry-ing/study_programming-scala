import scala.xml._

val xmlAsString = "<sammich>...</sammich>"
val xml1 = XML.loadString(xmlAsString)

val xml2 =
  <sammich>
    <bread>wheat</bread>
    <meat>salami</meat>
    <condiments>
      <condiment expired="true">mayo</condiment>
      <condiment expired="false">mustard</condiment>
    </condiments>
  </sammich>

for {
  condiment <- xml2 \\ "condiment"
  if (condiment \ "@expired").text == "true"
} println(s"the ${condiment.text} has expired!")

def isExpired(condiment: Node): String =
  condiment.attribute("expired") match {
    case Some(Nil) | None => "unknown!"
    case Some(nodes) => nodes.head.text
  }

xml2 match {
  case <sammich>{ingredients @ _*}</sammich> => {
    for {
      condiments @ <condiments>{_*}</condiments> <- ingredients
      cond <- condiments \ "condiment"
    } println(s" condiment: ${cond.text} is expired? ${isExpired(cond)}")
  }
}