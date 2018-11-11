package ekeith.CitymapperApi.data

/**
  * A container for a Citymapper API key.
  *
  * An implicit instance of this class will be looked for when executing a `CitymapperRepo.run()` method.
  * @param key A Citymapper API key.
  */
case class CmKey(key: String) {
  override def toString: String = key
}
