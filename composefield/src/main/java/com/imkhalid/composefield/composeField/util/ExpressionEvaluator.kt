package com.imkhalid.composefield.composeField.util

object ExpressionEvaluator{
    fun evaluateCondition(condition: String, value: Any): Boolean {
        val regex = "([<>=!]+)?(.+)".toRegex()  // Extract operator and value
        val match = regex.find(condition) ?: throw IllegalArgumentException("Invalid condition format")

        var (operator, conditionValue) = match.destructured
        operator = operator.ifEmpty { "==" }  // If no operator, assume equality check

        // Convert conditionValue to Int if possible, else keep it as a String
        val parsedConditionValue: Any = conditionValue.toIntOrNull() ?: conditionValue.trim()

        // Convert input value to Int if possible
        val parsedInputValue: Any = (value as? String)?.toIntOrNull() ?: value

        val operations: Map<String, (Any, Any) -> Boolean> = mapOf(
            ">"  to { a, b -> (a is Int && b is Int) && a > b },
            "<"  to { a, b -> (a is Int && b is Int) && a < b },
            ">=" to { a, b -> (a is Int && b is Int) && a >= b },
            "<=" to { a, b -> (a is Int && b is Int) && a <= b },
            "==" to { a, b -> a == b },  // Works for both Int & String
            "!=" to { a, b -> a != b }   // Works for both Int & String
        )

        return operations[operator]?.invoke(parsedInputValue, parsedConditionValue)
            ?: throw IllegalArgumentException("Invalid operator")
    }

}