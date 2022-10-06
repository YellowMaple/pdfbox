fun testFull(regex: String,input:String){
    if (regex.toRegex().matches(input))
        println("pass")
    else
        println("false")
}

fun testPart(regex: String,input:String){
    if (regex.toRegex().containsMatchIn(input))
        println("pass")
    else
        println("false")
}

fun testRegex(){
    testPart("""\d+\.\d+""","a1.23")
    testPart("""^a""","a1.23")
    testPart("""^\d+\.\d+""","1.2.3")
    testPart("""^\d+\.\d+""","123")
}
fun testIterator(){
    val list = listOf(1,2,3,4,5)
    val iterator = list.iterator()
    while(iterator.hasNext()){
        val next = iterator.next()
        println(next)
    }
}
fun main(args: Array<String>) {
    val next = "     4"
    val next1 = "     4 "
    if ("""\d$""".toRegex().containsMatchIn(next))
        println("pass")
    if ("""\d$""".toRegex().containsMatchIn(next1))
        println("pass 1")

}