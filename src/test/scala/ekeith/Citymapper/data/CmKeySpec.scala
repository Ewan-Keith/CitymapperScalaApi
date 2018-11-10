package ekeith.Citymapper.data

import org.scalatest.{AsyncFlatSpec, Matchers}

class CmKeySpec extends AsyncFlatSpec with Matchers {

  "CmKey.toString" should "return the users Api key" in {
    CmKey("abcd").toString shouldEqual "abcd"
  }

}
