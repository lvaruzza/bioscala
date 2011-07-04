package bio

trait BioSeq {
  val name: String;
  val text: String
}

trait QualitySeq {
  val qual: Array[Int]
}

case class SimpleBioSeq(override val name: String, override val text: String) extends BioSeq

case class IndexedBioSeq(override val name: String, override val text: String, val idx: Int) extends BioSeq

case class BioQualSeq(override val name: String,
                      override val text: String,
                      override val qual: Array[Int]) extends BioSeq with QualitySeq

