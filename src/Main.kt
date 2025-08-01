import Utility.*
import org.nocrala.tools.texttablefmt.BorderStyle
import org.nocrala.tools.texttablefmt.CellStyle
import org.nocrala.tools.texttablefmt.ShownBorders
import org.nocrala.tools.texttablefmt.Table

fun main() {
    val shoppingCart = mutableListOf<String>()
    val centerAlign = CellStyle(CellStyle.HorizontalAlign.center)
    val rightAlign = CellStyle(CellStyle.HorizontalAlign.right)

    // --- Display product catalog ---
    val productTable = Table(3, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.SURROUND_HEADER_AND_COLUMNS)
    productTable.setColumnWidth(0, 5, 5)
    productTable.setColumnWidth(1, 20, 30)
    productTable.setColumnWidth(2, 10, 15)

    productTable.addCell("$GREEN No ", centerAlign)
    productTable.addCell("$GREEN Products ", centerAlign)
    productTable.addCell("$GREEN Price", centerAlign)

    for ((index, name) in productNames.withIndex()) {
        productTable.addCell((index + 1).toString(), centerAlign)
        productTable.addCell(name)
        productTable.addCell("\$ ${"%.2f".format(productPrices[index])}", rightAlign)
    }

    println(productTable.render())
    println()

    // --- User input loop ---
    while (true) {
        print("${CYAN}Enter a product (type 'done' to finish): $RESET")
        val input = readln().trim()

        if (input.equals("done", ignoreCase = true)) break

        if (!input.matches(Regex("^[a-zA-Z\\- ]+$"))) {
            println("${RED}Please enter a valid product name!$RESET")
            continue
        }

        val matchedProduct = productNames.find { it.equals(input, ignoreCase = true) }

        if (matchedProduct != null) {
            shoppingCart.add(matchedProduct)
            println("${MAGENTA}${matchedProduct} has been added to Cart!$RESET")
            println("${YELLOW}You have ${shoppingCart.size} ${if (shoppingCart.size == 1) "item" else "items"} in cart!$RESET")
        } else {
            println("$RED '$input' not found. Try again.$RESET")
        }
    }

    // --- Shopping cart summary ---
    println("$CYAN\nHere are all of your items in the shopping cart:$RESET")

    val cartTable = Table(3, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.SURROUND_HEADER_AND_COLUMNS)
    cartTable.setColumnWidth(0, 5, 5)
    cartTable.setColumnWidth(1, 20, 30)
    cartTable.setColumnWidth(2, 10, 15)

    cartTable.addCell("$GREEN No ", centerAlign)
    cartTable.addCell("$GREEN Products ", centerAlign)
    cartTable.addCell("$GREEN Price", centerAlign)
    var totalCost = 0.0

    for ((index, item) in shoppingCart.withIndex()) {
        val productIndex = productNames.indexOfFirst { it.equals(item, ignoreCase = true) }
        if (productIndex != -1) {
            val price = productPrices[productIndex]
            totalCost += price

            cartTable.addCell((index + 1).toString(), centerAlign)
            cartTable.addCell(productNames[productIndex])
            cartTable.addCell("\$ ${"%.2f".format(price)}", rightAlign)
        } else {
            cartTable.addCell("$YELLOW Warning: '$item' not found.$RESET", centerAlign, 3)
        }
    }
    cartTable.addCell(" ", centerAlign,3)
    cartTable.addCell(" ", centerAlign,3)
    cartTable.addCell(" ", centerAlign,3)

    // --- Combo Discounts ---
    var finalCost = totalCost

    val hasLaptop = shoppingCart.any { it.equals("Laptop", ignoreCase = true) }
    val hasMouse = shoppingCart.any { it.equals("Mouse", ignoreCase = true) }
    val hasHeadphones = shoppingCart.any { it.equals("Headphones", ignoreCase = true) }
    val hasMicrophone = shoppingCart.any { it.equals("Microphone", ignoreCase = true) }

    if (hasLaptop && hasMouse) {
        val mouseIndex = productNames.indexOfFirst { it.equals("Mouse", ignoreCase = true) }
        if (mouseIndex != -1) {
            val discount = productPrices[mouseIndex] * 0.08
            finalCost -= discount
            cartTable.addCell("$YELLOW Combo: 8% off Mouse → -\$${"%.2f".format(discount)}$RESET", rightAlign, 3)
        }
    }

    if (hasHeadphones && hasMicrophone) {
        finalCost -= 10.00
        cartTable.addCell("$YELLOW Combo: \$10 off Headphones + Microphone$RESET", rightAlign, 3)
    }

    // --- Tier Discounts ---
    val tierDiscount = when {
        finalCost > 1500 -> 0.10
        finalCost > 500 -> 0.05
        else -> 0.0
    }

    if (tierDiscount > 0.0) {
        val discountAmount = finalCost * tierDiscount
        finalCost -= discountAmount

        val tierMsg = if (tierDiscount == 0.10) "Premium Tier: 10%" else "Standard Tier: 5%"
        cartTable.addCell("$CYAN $tierMsg discount → -\$${"%.2f".format(discountAmount)}$RESET", rightAlign, 3)
    } else {
        println("$CYAN Basic Tier: No discount applied. $RESET")
    }

    // --- Final Total or Empty ---
    if (shoppingCart.isNotEmpty()) {
        cartTable.addCell("$MAGENTA Total cost: \$${"%.2f".format(finalCost)}$RESET", rightAlign, 3)
    } else {
        cartTable.addCell("$YELLOW No items in cart.$RESET", centerAlign, 3)
    }

    println(cartTable.render())
}
