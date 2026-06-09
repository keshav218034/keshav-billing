package com.keshavgeneralstore.billing.data

object SeedProducts {
    val indianKiranaProducts: List<Product> = listOf(
        product("8901719101013", "Parle-G 250g", "Biscuits", 30_00, 25_00, 24.0, "pcs"),
        product("8901058844001", "Maggi 2-Minute Noodles", "Noodles", 14_00, 12_00, 48.0, "pcs"),
        product("8901262010010", "Tata Salt 1kg", "Staples", 28_00, 24_00, 25.0, "pcs"),
        product("8901030701728", "Surf Excel 1kg", "Detergent", 135_00, 124_00, 12.0, "pcs"),
        product("8901030825783", "Rin Bar 250g", "Detergent", 25_00, 22_00, 30.0, "pcs"),
        product("8901030865376", "Lifebuoy Soap", "Personal Care", 40_00, 35_00, 24.0, "pcs"),
        product("8901396393803", "Good Day Butter Cookies", "Biscuits", 10_00, 9_00, 50.0, "pcs"),
        product("8901491101839", "Kurkure Masala Munch", "Snacks", 20_00, 18_00, 36.0, "pcs"),
        product("8901207011277", "Dairy Milk", "Chocolate", 10_00, 9_00, 48.0, "pcs"),
        product("8901314010093", "Colgate Toothpaste", "Personal Care", 65_00, 58_00, 18.0, "pcs"),
        product(null, "Loose Sugar", "Loose Staples", 42_00, 39_00, 50.0, "kg"),
        product(null, "Loose Rice", "Loose Staples", 55_00, 48_00, 75.0, "kg"),
        product(null, "Loose Wheat Flour", "Loose Staples", 36_00, 32_00, 60.0, "kg"),
        product(null, "Loose Toor Dal", "Loose Staples", 155_00, 142_00, 30.0, "kg"),
        product(null, "Loose Chana Dal", "Loose Staples", 88_00, 80_00, 30.0, "kg"),
        product(null, "Loose Tea", "Beverages", 280_00, 250_00, 10.0, "kg"),
        product(null, "Loose Poha", "Breakfast", 48_00, 42_00, 25.0, "kg"),
        product(null, "Loose Suji", "Staples", 45_00, 40_00, 20.0, "kg")
    )

    private fun product(
        barcode: String?,
        name: String,
        category: String,
        sellingPricePaise: Long,
        purchasePricePaise: Long,
        stockQuantity: Double,
        unit: String
    ): Product {
        return Product(
            barcode = barcode,
            name = name,
            category = category,
            sellingPricePaise = sellingPricePaise,
            purchasePricePaise = purchasePricePaise,
            stockQuantity = stockQuantity,
            unit = unit,
            lowStockAlertQuantity = if (unit == "kg") 5.0 else 3.0
        )
    }
}
