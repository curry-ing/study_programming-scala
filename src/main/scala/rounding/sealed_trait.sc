sealed trait Breed { val name: String }

case object doberman extends Breed { val name = "Doberman Pinscher" }
case object yorkie   extends Breed { val name = "Yorkshir Terrier" }
case object scottie  extends Breed { val name = "Scottish Terrier" }
case object dane     extends Breed { val name = "Great Dane" }
case object portie   extends Breed { val name = "Portuguese Water Dog" }
