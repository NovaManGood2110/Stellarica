import sun.misc.Unsafe

println("hello world!")

val f = Unsafe::class.java.getDeclaredField("theUnsafe")
f.setAccessible(true)
val unsafe = f.get(null) as Unsafe

println("goodbye world! $unsafe")
