package dam.exer_2

/**
 * Main function
 *
 * It calculates basic arithmetic operations, boolean operations and bitwise shift operations.
 * Depending on the operations, it shows the result in decimal, hexadecimal and boolean.
 */
fun main() {
    print("Welcome. Which operation would you like to do? ")
    println("You can choose between:\n- Addition (type '+')" +
            "\n- Subtraction (type '-')\n- Multiplication (type '*')" +
            "\n- Division (type '/')\n- Boolean AND (type '&&')" +
            "\n- Boolean OR (type '||')\n- Boolean NOT (type '!')" +
            "\n- Bitwise Left Shift (type 'shl')\n- Bitwise Right Shift (type 'shr')")

    val choice = readln() // read user's operation choice input
    var numA = "" // initialize the variable for the first number
    if (choice == "&&" || choice == "||" || choice == "!"){ // if the chosen operation is a boolean one
        println("Type '1' or '0'")
        numA = readln() // read user's first number input
        while (numA != "1" && numA != "0") { // verify if the first number is different from 1 and 0
            // if it's still not 1 or 0, then it's always asking til the input is 1 or 0
            println("Type only '1' or '0'")
            numA = readln()
        }
    } else { // if the chosen operation isn't a boolean one
        println("Type the first number: ")
        numA = readln() // read user's first number input
    }
    var numB = "" // initialize the variable for the second number
    if (choice != "!") { // if the operation isn't a Boolean NOT
        println("Type the second number: ")
        numB = readln() // read user's second number input
    }

    try {
        val result: Any = when (choice) { // result is Any due to boolean operations
            "+" -> numA.toFloat() + numB.toFloat()
            "-" -> numA.toFloat() - numB.toFloat()
            "*" -> numA.toFloat() * numB.toFloat()
            "/" -> numA.toFloat() / numB.toFloat()
            "&&" -> stringToBool(numA) && stringToBool(numB)
            "||" -> stringToBool(numA) || stringToBool(numB)
            "!" -> !stringToBool(numA) // it's the only unary operation
            "shl" -> numA.toInt() shl numB.toInt()
            "shr" -> numA.toInt() shr numB.toInt()
            else -> throw IllegalArgumentException("Invalid operation")
        }
        when (choice) {
            // aritmethic operations -> decimal
            "+", "-", "*" -> { println("The result in decimal is: $result") }
            "/" -> {
                if (numB.toFloat() == 0f) {
                    println("Division by 0 is not possible")
                }
                println("The result in decimal is: $result")
            }
            // bitwise operations -> hexadecimal
            "shl", "shr" -> { println("The result in hexadecimal is: ${(result as Int).toString(16).uppercase()}") }
            // boolean operators -> boolean
            "&&", "||", "!" -> { println("The result in boolean is: $result") }
        }
    } catch (e: NumberFormatException) {
        println("Please, type only numbers or valid true/false")
    } catch (e: Exception) {
        println("Unknown error: ${e.message}")
    }
}

/**
 * Auxiliary function to check if a value is "1" or "true".
 * It helps with the boolean operations AND, OR and NOT.
 *
 * @param value value to be checked
 * @return true or false depending on the given value
 */
fun stringToBool(value: String): Boolean {
    // auxiliary function for the boolean operations
    return value == "1" || value.equals("true", ignoreCase = true)
}