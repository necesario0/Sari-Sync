package com.sarisync.ui.localization

/**
 * Holds every user-facing string in the app.
 * Two pre-built instances are provided: [EnglishStrings] and [FilipinoStrings].
 */
data class AppStrings(

    // ── General / Shared ───────────────────────────────
    val appName: String,
    val save: String,

    // ── Bottom Navigation ──────────────────────────────
    val navInventory: String,
    val navCredit: String,

    // ── Inventory Screen ───────────────────────────────
    val inventoryTitle: String,
    val addNewItem: String,
    val itemNameLabel: String,
    val categoryLabel: String,
    val priceLabel: String,
    val stockLabel: String,
    val saveItem: String,
    val loadingInventory: String,
    val itemsCount: (Int) -> String,
    val emptyInventory: String,
    val outOfStock: String,
    val lowStock: (Int) -> String,
    val inStock: (Int) -> String,
    val priceEach: String,
    val sellButton: String,
    val sellContentDesc: String,
    val deleteContentDesc: String,
    val errorPrefix: String,

    // ── Category names ─────────────────────────────────
    val catDrinks: String,
    val catFood: String,
    val catCooking: String,
    val catHousehold: String,
    val catSnacks: String,
    val catOthers: String,

    // ── Utang / Credit Screen ──────────────────────────
    val creditTitle: String,
    val recordPayment: String,
    val addCredit: String,
    val customerNameLabel: String,
    val amountLabel: String,
    val addCreditButton: String,
    val paymentButton: String,
    val loadingCredits: String,
    val customersCount: (Int) -> String,
    val emptyCredits: String,
    val statusPaid: String,
    val statusModerate: String,
    val statusHigh: String,

    // ── Scan Button Component ──────────────────────────
    val scanTip: String,
    val scanCamera: String,
    val scanGallery: String,
    val scanning: String,
    val scanNoResult: String,
    val scanError: (String) -> String,
    val scanDialogTitle: String,
    val scanDialogHint: String,
    val scanBestMatch: String,
    val scanOtherResults: String,
    val scanClose: String,
    val cameraPermissionDenied: String,

    // ── Welcome Screen ─────────────────────────────────
    val welcomeHeading: String,
    val welcomeSubheading: String,
    val welcomeGetStarted: String,
    val welcomeSelectLanguage: String,

    // ── Language Switcher ──────────────────────────────
    val languageLabel: String,
    val languageEnglish: String,
    val languageFilipino: String
)

// ════════════════════════════════════════════════════════
// ENGLISH
// ════════════════════════════════════════════════════════
val EnglishStrings = AppStrings(
    appName = "Sari-Sync",
    save = "Save",

    navInventory = "Inventory",
    navCredit = "Credit",

    inventoryTitle = "Sari-Sync Inventory",
    addNewItem = "Add New Item",
    itemNameLabel = "Item Name",
    categoryLabel = "Category",
    priceLabel = "Price (₱)",
    stockLabel = "Stock",
    saveItem = "Save Item",
    loadingInventory = "Loading inventory...",
    itemsCount = { count -> "Items ($count)" },
    emptyInventory = "No items yet. Add your first product!",
    outOfStock = "OUT OF STOCK",
    lowStock = { qty -> "$qty left (Low!)" },
    inStock = { qty -> "$qty in stock" },
    priceEach = "each",
    sellButton = "Sell",
    sellContentDesc = "Sell one",
    deleteContentDesc = "Delete",
    errorPrefix = "Error: ",

    catDrinks = "Drinks",
    catFood = "Food",
    catCooking = "Cooking",
    catHousehold = "Household",
    catSnacks = "Snacks",
    catOthers = "Others",

    creditTitle = "Credit Ledger",
    recordPayment = "Record a Payment",
    addCredit = "Add Credit",
    customerNameLabel = "Customer Name",
    amountLabel = "Amount (₱)",
    addCreditButton = "Add Credit",
    paymentButton = "Payment",
    loadingCredits = "Loading credits...",
    customersCount = { count -> "Customers ($count)" },
    emptyCredits = "No one owes anything. Hopefully it stays that way!",
    statusPaid = "PAID",
    statusModerate = "MODERATE",
    statusHigh = "HIGH DEBT",

    scanTip = "Tip: Point the camera at the product name only, not the entire label.",
    scanCamera = "Scan with Camera",
    scanGallery = "Pick from Gallery",
    scanning = "Scanning text...",
    scanNoResult = "No product name found. Try pointing the camera at the name only.",
    scanError = { msg -> "Scan failed: $msg" },
    scanDialogTitle = "Scanned Text",
    scanDialogHint = "Tap a result to use it as the item name. You can still edit it afterwards.",
    scanBestMatch = "BEST MATCH",
    scanOtherResults = "Other results:",
    scanClose = "Close",
    cameraPermissionDenied = "Camera permission is needed to scan items.",

    welcomeHeading = "Welcome to Sari-Sync!",
    welcomeSubheading = "Your all-in-one sari-sari store manager.\nTrack inventory, manage credit, and scan products with ease.",
    welcomeGetStarted = "Get Started",
    welcomeSelectLanguage = "Select Language",

    languageLabel = "Language",
    languageEnglish = "English",
    languageFilipino = "Filipino"
)

// ════════════════════════════════════════════════════════
// FILIPINO
// ════════════════════════════════════════════════════════
val FilipinoStrings = AppStrings(
    appName = "Sari-Sync",
    save = "I-save",

    navInventory = "Imbentaryo",
    navCredit = "Utang",

    inventoryTitle = "Sari-Sync Imbentaryo",
    addNewItem = "Magdagdag ng Bagong Paninda",
    itemNameLabel = "Pangalan ng Paninda",
    categoryLabel = "Kategorya",
    priceLabel = "Presyo (₱)",
    stockLabel = "Stok",
    saveItem = "I-save ang Paninda",
    loadingInventory = "Nilo-load ang imbentaryo...",
    itemsCount = { count -> "Mga Paninda ($count)" },
    emptyInventory = "Wala pang paninda. Magdagdag na ng una mong produkto!",
    outOfStock = "UBOS NA",
    lowStock = { qty -> "$qty na lang (Mababa!)" },
    inStock = { qty -> "$qty ang stok" },
    priceEach = "bawat isa",
    sellButton = "Ibenta",
    sellContentDesc = "Magbenta ng isa",
    deleteContentDesc = "Tanggalin",
    errorPrefix = "May error: ",

    catDrinks = "Inumin",
    catFood = "Pagkain",
    catCooking = "Pangluto",
    catHousehold = "Gamit sa Bahay",
    catSnacks = "Meryenda",
    catOthers = "Iba Pa",

    creditTitle = "Listahan ng Utang",
    recordPayment = "Mag-record ng Bayad",
    addCredit = "Magdagdag ng Utang",
    customerNameLabel = "Pangalan ng Customer",
    amountLabel = "Halaga (₱)",
    addCreditButton = "Dagdag Utang",
    paymentButton = "Bayad",
    loadingCredits = "Nilo-load ang mga utang...",
    customersCount = { count -> "Mga Customer ($count)" },
    emptyCredits = "Wala pang naka-utang. Sana ganito palagi!",
    statusPaid = "BAYAD NA",
    statusModerate = "KATAMTAMAN",
    statusHigh = "MATAAS ANG UTANG",

    scanTip = "Tip: Itutok ang camera sa pangalan ng produkto lang, hindi sa buong label.",
    scanCamera = "I-scan gamit Camera",
    scanGallery = "Pumili sa Gallery",
    scanning = "Nag-i-scan ng text...",
    scanNoResult = "Walang nakitang pangalan ng produkto. Subukang itutok ang camera sa pangalan lang ng paninda.",
    scanError = { msg -> "Hindi na-scan: $msg" },
    scanDialogTitle = "Na-scan na Text",
    scanDialogHint = "Pindutin ang resulta para gamitin bilang pangalan ng paninda. Puwede mo pa itong i-edit pagkatapos.",
    scanBestMatch = "PINAKAMALAPIT NA RESULTA",
    scanOtherResults = "Iba pang resulta:",
    scanClose = "Isara",
    cameraPermissionDenied = "Kailangan ng camera permission para mag-scan ng paninda.",

    welcomeHeading = "Maligayang pagdating sa Sari-Sync!",
    welcomeSubheading = "Ang iyong all-in-one na tagapamahala ng sari-sari store.\nSubaybayan ang imbentaryo, pamahalaan ang utang, at mag-scan ng mga produkto nang madali.",
    welcomeGetStarted = "Magsimula",
    welcomeSelectLanguage = "Pumili ng Wika",

    languageLabel = "Wika",
    languageEnglish = "English",
    languageFilipino = "Filipino"
)
