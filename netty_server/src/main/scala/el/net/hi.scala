package el.net;

object hi {
    def main(args : Array[String]): Unit = {

        println("Hi Scala.");

    }
    trait one{
        def a()
    }

    trait two extends one {
        override def a() {print("my ") }
        a
    }

    trait three extends one{
        override def a() {print("name ")}
        a
    }

    class Name { val b = 1 }

    val one = new Name() with two with three
    println("Hello")
    val two = new Name() with three with two
}