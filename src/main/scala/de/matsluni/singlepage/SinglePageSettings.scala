package de.matsluni.singlepage

import akka.actor.{ExtensionKey, ExtendedActorSystem, Extension}
import com.github.nscala_time.time.StaticDateTimeFormat

import com.github.nscala_time.time.Imports._

object SinglePageSettings extends ExtensionKey[SinglePageSettings]

/**
 * The settings for SinglePage as an Akka extension:
 *   - `interface`: the network interface the service gets bound to, e.g. `"localhost"`.
 *   - `port`: the port the service gets bound to, e.g. `8080`.
 */
class SinglePageSettings(system: ExtendedActorSystem) extends Extension {

  val parser = StaticDateTimeFormat.forPattern("ddMMyyyy")
  /**
   * The network interface the App gets bound to, e.g. `"localhost"`.
   */
  val interface: String = system.settings.config.getString("singlepage.interface")

  /**
   * The port the service gets bound to, e.g. `8080`.
   */
  val port: Int = system.settings.config.getInt("singlepage.port")

  /**
   * The startdate for the data to be retrieved by StockDateProducer
   */
  val startdate = parser.parseDateTime(system.settings.config.getString("singlepage.startdate"))

  /**
   * The enddate for the data to be retrieved by StockDataProducer
   */
  val enddate = parser.parseDateTime(system.settings.config.getString("singlepage.enddate"))
}
