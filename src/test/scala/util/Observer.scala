package uno.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ObserverSpec extends AnyFlatSpec with Matchers {
  "An Observable" should "add, remove, and notify observers" in {
    var updated = false
    val observer = new uno.util.Observer {
      override def update(): Unit = updated = true
    }
    val observable = new Observable {}
    
    observable.add(observer)
    observable.subscribers should contain (observer)
    
    observable.notifyObservers()
    updated should be(true)
    
    observable.remove(observer)
    observable.subscribers should not contain (observer)
  }
}