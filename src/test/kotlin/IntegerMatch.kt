// strings/IntegerMatch.java
// (c)2021 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
object IntegerMatch {
    @JvmStatic
    fun main(args: Array<String>) {
        println("1".matches("\\d+".toRegex()))
        println("1a".matches("^\\d+".toRegex()))
        println("1.2.3".matches("\\d+".toRegex()))
        println("11.2.3".matches("\\d+\\.".toRegex()))
    }
}