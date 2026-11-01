# Laxmi Furniture - Android App (Kotlin + Room Database)

Furniture shop ki mobile application. Desktop (Java Swing) version jaisa hi feature set,
lekin Android ke liye - data **Room (SQLite)** database mein permanently save hota hai.

## Screens (Black Book - Section 13 ke figures ke hisab se)
| Figure | Screen | File |
|---|---|---|
| 13.1 | Login / Sign-up | `ui/LoginActivity.kt` |
| 13.2 | Home (Product Grid) | `ui/MainActivity.kt` |
| 13.3 | Navigation Drawer | `MainActivity` (hamburger icon se khulta hai) |
| 13.4 | Edit Profile | `ui/EditProfileActivity.kt` |
| 13.5 | Product Details | `ui/ProductDetailActivity.kt` |
| 13.6 | Cart | `ui/CartActivity.kt` |
| 13.7 | Checkout | `ui/CheckoutActivity.kt` |
| 13.8 | Order Placed Successfully | `ui/OrderSuccessActivity.kt` |
| extra | Wishlist, My Orders, Admin Panel, Host Dashboard | `WishlistActivity`, `OrderHistoryActivity`, `ui/admin/*` (Host Dashboard: `ui/admin/HostDashboardActivity.kt`) |

## Features
- Login / Sign-up (Room DB mein save), Guest browsing, session yaad rehta hai
- Product grid (16 products, real photos), search, category chips, sort
- Product detail screen, Wishlist (logged-in user ke liye DB mein save)
- Cart: quantity +/-, remove, coupon codes (`LAXMI10`, `WELCOME15`, `FLAT500`), GST 18%
- Checkout (address + payment method) -> Order success -> Order history (DB)
- Admin Panel: products add / edit / delete  (login: `admin@laxmi.com` / `admin123`)
- Edit Profile (naam / password badalna)
- **Host (Seller) function** - neeche dekhein

## Host (Seller) function
- **Host kaise banein:** Sign Up ke time "Register as Host" tick karein, ya login ke baad drawer se **Become a Host** dabayein.
- **Host Dashboard** (drawer > Host Dashboard):
  - Stats: total listings, units sold, earnings
  - **My Listings:** apne products add / edit / delete (host sirf apne hi products edit kar sakta hai)
  - **Sales:** kaun sa product kab bika (order id, date, amount)
- Product detail screen par "Sold by <host ka naam>" dikhta hai (admin ke products par "Laxmi Furniture").
- Checkout ke time har order line par host ka email save hota hai, isliye host ki earnings sahi calculate hoti hai.
- Guest orders bhi database mein "guest" ke naam se save hote hain taaki host ki sale miss na ho.

**Note:** database version 2 hai (host fields add hue). Agar purana version phone/emulator par pehle install kar chuke ho
to app ko ek baar **uninstall** karke dobara run karein (purana data reset ho jayega).

## Run kaise karein
1. **Android Studio** (Hedgehog / Iguana ya naya) install karein - JDK 17 ke saath aata hai.
2. `File > Open` se is `LaxmiFurnitureMobile` folder ko open karein.
3. Gradle sync hone dein (pehli baar internet chahiye - libraries download hongi).
4. Emulator ya USB se phone connect karke **Run** dabayein.

## Architecture (viva ke liye)
- `data/entity` - Room entities (Product, User, Order, OrderItem, Wishlist)
- `data/dao` - DAOs (suspend functions, coroutines)
- `data/AppDatabase.kt` - Room database singleton
- `adapter` - RecyclerView adapters
- `ui` - Activities (screens)
- `util` - SessionManager (SharedPreferences), CartManager, CouponUtil, etc.

## Note
Cart sirf current session ke liye hota hai (checkout ke baad clear) - yeh design choice desktop
app jaisi hi hai. Guest user ki wishlist/orders save nahi hoti, sirf logged-in user ki.
