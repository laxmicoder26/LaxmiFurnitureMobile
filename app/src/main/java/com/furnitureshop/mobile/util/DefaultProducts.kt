package com.furnitureshop.mobile.util

import com.furnitureshop.mobile.data.entity.ProductEntity

object DefaultProducts {
    fun list(): List<ProductEntity> = listOf(
        ProductEntity(1, "Classic 3-Seater Sofa", "Sofa", 24999.0, "Comfortable fabric sofa with cushioned armrests, perfect for the living room.", true),
        ProductEntity(2, "L-Shape Corner Sofa", "Sofa", 34999.0, "Spacious L-shaped sofa with soft upholstery, ideal for large halls.", true),
        ProductEntity(3, "Recliner Couch", "Sofa", 27999.0, "Relaxing recliner couch with premium cushioning and sturdy wooden legs.", false),
        ProductEntity(4, "Queen Size Bed", "Bed", 18999.0, "Elegant queen size bed with a solid wood frame and headboard.", true),
        ProductEntity(5, "King Size Bed with Storage", "Bed", 26999.0, "King size bed with hydraulic storage, great for small bedrooms.", true),
        ProductEntity(6, "Wooden Single Bed", "Bed", 12999.0, "Compact single bed, sturdy and space-saving, great for kids' rooms.", true),
        ProductEntity(7, "Dining Chair (Set of 4)", "Chair", 8999.0, "Set of 4 matching dining chairs with cushioned seats.", true),
        ProductEntity(8, "Ergonomic Office Chair", "Chair", 5999.0, "Adjustable office chair with lumbar support and smooth-roll wheels.", true),
        ProductEntity(9, "Wooden Rocking Chair", "Chair", 6499.0, "Classic rocking chair, handcrafted from solid sheesham wood.", false),
        ProductEntity(10, "3-Door Wardrobe", "Wardrobe", 21999.0, "Spacious 3-door wardrobe with mirror and multiple shelves.", true),
        ProductEntity(11, "Sliding Door Wardrobe Cabinet", "Wardrobe", 28999.0, "Modern sliding-door wardrobe cabinet that saves floor space.", true),
        ProductEntity(12, "6-Seater Dining Table", "Table", 19999.0, "Solid wood dining table set, seats 6 comfortably.", true),
        ProductEntity(13, "Study Table with Drawer", "Table", 6999.0, "Compact study table with a drawer, ideal for students.", true),
        ProductEntity(14, "Round Coffee Table", "Table", 3999.0, "Elegant round coffee table for your living room centrepiece.", true),
        ProductEntity(15, "Wall-Mounted Bookshelf", "Others", 4999.0, "5-tier wall-mounted bookshelf, sturdy and stylish.", true),
        ProductEntity(16, "Cozy Bean Bag", "Others", 1499.0, "Soft and comfy bean bag, available in multiple colours.", true)
    )
}
