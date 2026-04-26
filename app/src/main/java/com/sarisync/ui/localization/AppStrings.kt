package com.sarisync.ui.localization

/**
 * Holds every user-facing string in the app.
 * Two pre-built instances are provided: [EnglishStrings] and [FilipinoStrings].
 */
data class AppStrings(

    // ── General / Shared ───────────────────────────────
    val appName: String,
    val save: String,
    val cancel: String,
    val confirm: String,
    val ok: String,

    // ── Bottom Navigation ──────────────────────────────
    val navInventory: String,
    val navCredit: String,
    val navDashboard: String,
    val navSettings: String,

    // ── Inventory Screen ───────────────────────────────
    val inventoryTitle: String,
    val addNewItem: String,
    val addItemTitle: String,
    val itemNameLabel: String,
    val categoryLabel: String,
    val priceLabel: String,
    val costPriceLabel: String,
    val stockLabel: String,
    val saveItem: String,
    val saveButton: String,
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

    // ── Search ─────────────────────────────────────────
    val searchLabel: String,
    val searchCustomerLabel: String,
    val noSearchResults: String,

    // ── Stock status (short labels for cards) ──────────
    val stockOut: String,
    val stockLow: String,
    val stockHealthy: String,

    // ── Sell Dialog (Cash / Utang) ─────────────────────
    val sellDialogTitle: String,
    val sellDialogQuestion: String,
    val cashButton: String,
    val utangButton: String,
    val cancelButton: String,

    // ── Restock Dialog ─────────────────────────────────
    val restockButton: String,
    val restockDialogTitle: String,
    val currentStockLabel: String,
    val restockQuantityLabel: String,
    val restockConfirmButton: String,

    // ── Delete Confirmation Dialog ─────────────────────
    val deleteButton: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: String,
    val deleteConfirmButton: String,

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

    // ── Payer Behavior Badges (time-based) ────────────────────────
    val payerFullyPaid: String,      // Paid all debts
    val payerGood: String,           // Pays within 7 days
    val payerAverage: String,        // Pays within 30 days
    val payerBad: String,            // Unpaid for 30+ days
    val payerNew: String,            // No payment history,
    val paymentHistory: String,
    val transactionsPaid: String,

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
    val welcomeSkip: String,

    // ── Language Switcher ──────────────────────────────
    val languageLabel: String,
    val languageEnglish: String,
    val languageFilipino: String,

    // ── Dashboard Screen ──────────────────────────────
    val dashboardTitle: String,
    val dashboardLoading: String,
    val dashboardKeyMetrics: String,
    val dashboardRevenue: String,
    val dashboardCost: String,
    val dashboardNetProfit: String,
    val dashboardProfitMargin: String,
    val dashboardUnitsSold: String,
    val dashboardOutstandingCredit: String,
    val dashboardSalesTrend: String,
    val dashboardDailyProfit: String,
    val dashboardTopSellers: String,
    val dashboardSold: String,
    val dashboardInventoryHealth: String,
    val dashboardTotalProducts: String,
    val dashboardHealthy: String,
    val dashboardLowStock: String,
    val dashboardOutOfStock: String,
    val dashboardLowStockAlerts: String,
    val dashboardOutOfStockAlerts: String,
    val dashboardRemaining: String,
    val dashboardToday: String,
    val dashboardWeek: String,
    val dashboardMonth: String,
    val dashboardTopDebtor: String,
    val dashboardHighestUtang: String,

    // ── Settings Screen ───────────────────────────────
    val settingsTitle: String,

    // Business Info
    val settingsBusinessInfo: String,
    val settingsBusinessInfoDesc: String,
    val settingsBusinessName: String,
    val settingsBusinessNameHint: String,
    val settingsBusinessNameSaved: String,

    // Language
    val settingsLanguage: String,
    val settingsLanguageDesc: String,

    // Money Format
    val settingsMoneyFormat: String,
    val settingsMoneyFormatDesc: String,
    val settingsCurrency: String,
    val settingsCurrencyPeso: String,
    val settingsCurrencyDollar: String,

    // App Appearance
    val settingsAppearance: String,
    val settingsAppearanceDesc: String,
    val settingsThemeLight: String,
    val settingsThemeDark: String,
    val settingsThemeSystem: String,

    // Notifications
    val settingsNotifications: String,
    val settingsNotificationsDesc: String,
    val settingsNotifLowStock: String,
    val settingsNotifLowStockDesc: String,
    val settingsNotifSales: String,
    val settingsNotifSalesDesc: String,

    // Data Management
    val settingsDataManagement: String,
    val settingsDataManagementDesc: String,
    val settingsDeleteAll: String,
    val settingsDeleteAllDesc: String,
    val settingsDeleteAllWarning: String,
    val settingsDeleteAllConfirm: String,
    val settingsDeleteAllDone: String,

    // Help
    val settingsHelp: String,
    val settingsHelpDesc: String,
    val settingsHelpTitle: String,
    val settingsHelpContent: String,

    // App version
    val settingsVersion: String,

    // ── Stock Prediction (Inventory warnings) ──────────
    val predictionUrgent: String,
    val predictionWarning: String,
    val predictionOutOfStock: String,

    // ── Customer Dropdown (Utang/Payment) ──────────────
    val selectCustomerLabel: String,
    val noCustomersYet: String
)

// ════════════════════════════════════════════════════════
// ENGLISH
// ════════════════════════════════════════════════════════
val EnglishStrings = AppStrings(
    appName = "Sari-Sync",
    save = "Save",
    cancel = "Cancel",
    confirm = "Confirm",
    ok = "OK",

    navInventory = "Inventory",
    navCredit = "Credit",
    navDashboard = "Dashboard",
    navSettings = "Settings",

    inventoryTitle = "Sari-Sync Inventory",
    addNewItem = "Add New Item",
    addItemTitle = "Add New Item",
    itemNameLabel = "Item Name",
    categoryLabel = "Category",
    priceLabel = "Price (₱)",
    costPriceLabel = "Cost Price (₱)",
    stockLabel = "Stock",
    saveItem = "Save Item",
    saveButton = "Save Item",
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

    // ── Search ─────────────────────────────────────────
    searchLabel = "Search items...",
    searchCustomerLabel = "Search customers...",
    noSearchResults = "No items found matching your search.",

    // ── Stock status (short labels) ────────────────────
    stockOut = "UBOS NA",
    stockLow = "na lang (Mababa!)",
    stockHealthy = "in stock",

    // ── Sell Dialog ────────────────────────────────────
    sellDialogTitle = "How will they pay?",
    sellDialogQuestion = "Choose payment method:",
    cashButton = "💵 Cash",
    utangButton = "📝 Utang (Credit)",
    cancelButton = "Cancel",

    // ── Restock Dialog ─────────────────────────────────
    restockButton = "Restock",
    restockDialogTitle = "Restock Item",
    currentStockLabel = "Current stock",
    restockQuantityLabel = "Quantity to add",
    restockConfirmButton = "Restock",

    // ── Delete Dialog ──────────────────────────────────
    deleteButton = "Delete",
    deleteDialogTitle = "Delete Item?",
    deleteDialogMessage = "Are you sure you want to remove",
    deleteConfirmButton = "Yes, Delete",

    // ── Categories ─────────────────────────────────────
    catDrinks = "Drinks",
    catFood = "Food",
    catCooking = "Cooking",
    catHousehold = "Household",
    catSnacks = "Snacks",
    catOthers = "Others",

    // ── Utang / Credit ─────────────────────────────────
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

    // ── Payer Behavior Badges ──────────────────────────
    payerFullyPaid = "✓ Fully Paid",
    payerGood = "👍 Good Payer (pays within 7 days)",
    payerAverage = "👌 Average Payer (pays within 30 days)",
    payerBad = "⚠️ Bad Payer (unpaid 30+ days)",
    payerNew = "🆕 No Payment History",
    paymentHistory = "Payments",
    transactionsPaid = "transactions paid",

    // ── Scan ───────────────────────────────────────────
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

    // ── Welcome ────────────────────────────────────────
    welcomeHeading = "Welcome to Sari-Sync!",
    welcomeSubheading = "Your all-in-one sari-sari store manager.\nTrack inventory, manage credit, and grow your business.",
    welcomeGetStarted = "Get Started",
    welcomeSelectLanguage = "Choose your language",
    welcomeSkip = "Skip",

    languageLabel = "Language",
    languageEnglish = "English",
    languageFilipino = "Filipino",

    // ── Dashboard ──────────────────────────────────────
    dashboardTitle = "Dashboard",
    dashboardLoading = "Loading dashboard...",
    dashboardKeyMetrics = "Key Metrics",
    dashboardRevenue = "Revenue",
    dashboardCost = "Expenses",
    dashboardNetProfit = "Net Profit",
    dashboardProfitMargin = "Profit Margin",
    dashboardUnitsSold = "Units Sold",
    dashboardOutstandingCredit = "Outstanding Credit",
    dashboardSalesTrend = "Sales Trend (Revenue vs Expenses)",
    dashboardDailyProfit = "Daily Profit",
    dashboardTopSellers = "Top Selling Items",
    dashboardSold = "sold",
    dashboardInventoryHealth = "Inventory Health",
    dashboardTotalProducts = "Total",
    dashboardHealthy = "Healthy",
    dashboardLowStock = "Low",
    dashboardOutOfStock = "Out",
    dashboardLowStockAlerts = "Low Stock Alerts",
    dashboardOutOfStockAlerts = "Out of Stock Alerts",
    dashboardRemaining = "left",
    dashboardToday = "Today",
    dashboardWeek = "7 Days",
    dashboardMonth = "30 Days",
    dashboardTopDebtor = "Top Debtor",
    dashboardHighestUtang = "Highest outstanding credit",

    // ── Settings ───────────────────────────────────────
    settingsTitle = "Settings",

    settingsBusinessInfo = "Business Info",
    settingsBusinessInfoDesc = "Set up your store details",
    settingsBusinessName = "Store Name",
    settingsBusinessNameHint = "e.g. Aling Maria's Store",
    settingsBusinessNameSaved = "Store name saved!",

    settingsLanguage = "Language",
    settingsLanguageDesc = "Switch between English and Filipino",

    settingsMoneyFormat = "Money Format",
    settingsMoneyFormatDesc = "Choose the currency you use",
    settingsCurrency = "Currency",
    settingsCurrencyPeso = "₱ Peso",
    settingsCurrencyDollar = "$ Dollar",

    settingsAppearance = "App Look",
    settingsAppearanceDesc = "Choose light or dark mode",
    settingsThemeLight = "Light",
    settingsThemeDark = "Dark",
    settingsThemeSystem = "Auto",

    settingsNotifications = "Reminders",
    settingsNotificationsDesc = "Get alerts about your store",
    settingsNotifLowStock = "Low stock warning",
    settingsNotifLowStockDesc = "Notify me when items are running low",
    settingsNotifSales = "Sales update",
    settingsNotifSalesDesc = "Notify me about daily sales summary",

    settingsDataManagement = "Data Management",
    settingsDataManagementDesc = "Manage your store records",
    settingsDeleteAll = "Delete All Records",
    settingsDeleteAllDesc = "Remove all inventory, sales, and credit data",
    settingsDeleteAllWarning = "Are you sure? This will delete ALL your data — inventory, sales records, and credit history. This cannot be undone!",
    settingsDeleteAllConfirm = "Yes, Delete Everything",
    settingsDeleteAllDone = "All records have been deleted.",

    settingsHelp = "Help",
    settingsHelpDesc = "Learn how to use the app",
    settingsHelpTitle = "How to Use Sari-Sync",
    settingsHelpContent = """
**Inventory Tab**
• Tap the "+" button to add products to your store
• Set the selling price and cost price for each item
• Tap "Sell" to choose Cash or Utang payment
• Tap "Restock" to add more stock
• Use the search bar to find items quickly
• The app tracks your stock automatically

**Credit Tab**
• Record when a customer buys on credit (utang)
• Record payments when they pay back
• See who owes you and how much
• Check payer behavior badges (Good/Average/Bad)

**Dashboard Tab**
• View your daily, weekly, or monthly earnings
• See charts showing your sales trend
• Check which items sell the most
• Monitor your stock levels
• See your top debtor

**Settings Tab**
• Change language anytime
• Set your store name
• Switch between light and dark mode
• Manage your data

**Tip:** Sell items through the app so your dashboard stays accurate!
""",

    settingsVersion = "Sari-Sync v1.0",

    // Stock Prediction
    predictionUrgent = "Will run out in %d day(s)!",
    predictionWarning = "Will run out in %d days",
    predictionOutOfStock = "Out of stock!",

    // Customer Dropdown
    selectCustomerLabel = "Select Customer",
    noCustomersYet = "No customers yet"
)

// ════════════════════════════════════════════════════════
// FILIPINO
// ════════════════════════════════════════════════════════
val FilipinoStrings = AppStrings(
    appName = "Sari-Sync",
    save = "I-save",
    cancel = "Kanselahin",
    confirm = "Kumpirmahin",
    ok = "OK",

    navInventory = "Imbentaryo",
    navCredit = "Utang",
    navDashboard = "Dashboard",
    navSettings = "Settings",

    inventoryTitle = "Sari-Sync Imbentaryo",
    addNewItem = "Magdagdag ng Bagong Paninda",
    addItemTitle = "Magdagdag ng Bagong Paninda",
    itemNameLabel = "Pangalan ng Paninda",
    categoryLabel = "Kategorya",
    priceLabel = "Presyo (₱)",
    costPriceLabel = "Puhunan (₱)",
    stockLabel = "Stok",
    saveItem = "I-save ang Paninda",
    saveButton = "I-save ang Paninda",
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

    // ── Search ─────────────────────────────────────────
    searchLabel = "Maghanap ng paninda...",
    searchCustomerLabel = "Maghanap ng customer...",
    noSearchResults = "Walang nakitang paninda sa hinahanap mo.",

    // ── Stock status (short labels) ────────────────────
    stockOut = "UBOS NA",
    stockLow = "na lang (Mababa!)",
    stockHealthy = "ang stok",

    // ── Sell Dialog ────────────────────────────────────
    sellDialogTitle = "Paano magbabayad?",
    sellDialogQuestion = "Pumili ng paraan ng bayad:",
    cashButton = "💵 Cash",
    utangButton = "📝 Utang",
    cancelButton = "Kanselahin",

    // ── Restock Dialog ─────────────────────────────────
    restockButton = "Dagdag Stok",
    restockDialogTitle = "Dagdagan ang Stok",
    currentStockLabel = "Kasalukuyang stok",
    restockQuantityLabel = "Ilang idadagdag",
    restockConfirmButton = "Dagdagan",

    // ── Delete Dialog ──────────────────────────────────
    deleteButton = "Tanggalin",
    deleteDialogTitle = "Tanggalin ang Paninda?",
    deleteDialogMessage = "Sigurado ka bang gusto mong tanggalin ang",
    deleteConfirmButton = "Oo, Tanggalin",

    // ── Categories ─────────────────────────────────────
    catDrinks = "Inumin",
    catFood = "Pagkain",
    catCooking = "Pangluto",
    catHousehold = "Gamit sa Bahay",
    catSnacks = "Meryenda",
    catOthers = "Iba Pa",

    // ── Utang / Credit ─────────────────────────────────
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

    // ── Payer Behavior Badges ──────────────────────────
    payerFullyPaid = "✓ Bayad Na Lahat",
    payerGood = "👍 Mabuting Nagbabayad (nagbabayad sa loob ng 7 araw)",
    payerAverage = "👌 Katamtamang Nagbabayad (nagbabayad sa loob ng 30 araw)",
    payerBad = "⚠️ Masamang Nagbabayad (walang bayad 30+ araw)",
    payerNew = "🆕 Walang Payment History",
    paymentHistory = "Mga Bayad",
    transactionsPaid = "transaksyon na binayaran",

    // ── Scan ───────────────────────────────────────────
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

    // ── Welcome ────────────────────────────────────────
    welcomeHeading = "Maligayang pagdating sa Sari-Sync!",
    welcomeSubheading = "Ang iyong all-in-one na tagapamahala ng sari-sari store.\nSubaybayan ang imbentaryo, pamahalaan ang utang, at palaguin ang negosyo mo.",
    welcomeGetStarted = "Magsimula Na",
    welcomeSelectLanguage = "Pumili ng wika",
    welcomeSkip = "Laktawan",

    languageLabel = "Wika",
    languageEnglish = "English",
    languageFilipino = "Filipino",

    // ── Dashboard ──────────────────────────────────────
    dashboardTitle = "Dashboard",
    dashboardLoading = "Nilo-load ang dashboard...",
    dashboardKeyMetrics = "Mga Pangunahing Sukatan",
    dashboardRevenue = "Kita",
    dashboardCost = "Gastos",
    dashboardNetProfit = "Netong Tubo",
    dashboardProfitMargin = "Margin ng Tubo",
    dashboardUnitsSold = "Naibentang Unit",
    dashboardOutstandingCredit = "Kabuuang Utang",
    dashboardSalesTrend = "Takbo ng Benta (Kita vs Gastos)",
    dashboardDailyProfit = "Araw-araw na Tubo",
    dashboardTopSellers = "Pinakamabentang Paninda",
    dashboardSold = "naibenta",
    dashboardInventoryHealth = "Kalagayan ng Imbentaryo",
    dashboardTotalProducts = "Lahat",
    dashboardHealthy = "Maayos",
    dashboardLowStock = "Mababa",
    dashboardOutOfStock = "Ubos",
    dashboardLowStockAlerts = "Mga Babala sa Mababang Stok",
    dashboardOutOfStockAlerts = "Mga Ubos na Paninda",
    dashboardRemaining = "natitira",
    dashboardToday = "Ngayon",
    dashboardWeek = "7 Araw",
    dashboardMonth = "30 Araw",
    dashboardTopDebtor = "Pinaka-Malaking Utang",
    dashboardHighestUtang = "Pinakamalaking natitirang utang",

    // ── Settings ───────────────────────────────────────
    settingsTitle = "Settings",

    settingsBusinessInfo = "Impormasyon ng Negosyo",
    settingsBusinessInfoDesc = "I-set up ang detalye ng tindahan mo",
    settingsBusinessName = "Pangalan ng Tindahan",
    settingsBusinessNameHint = "hal. Tindahan ni Aling Maria",
    settingsBusinessNameSaved = "Na-save na ang pangalan ng tindahan!",

    settingsLanguage = "Wika",
    settingsLanguageDesc = "Magpalit ng English o Filipino",

    settingsMoneyFormat = "Pera na Ginagamit",
    settingsMoneyFormatDesc = "Piliin ang currency na ginagamit mo",
    settingsCurrency = "Currency",
    settingsCurrencyPeso = "₱ Peso",
    settingsCurrencyDollar = "$ Dollar",

    settingsAppearance = "Itsura ng App",
    settingsAppearanceDesc = "Pumili ng light o dark mode",
    settingsThemeLight = "Maliwanag",
    settingsThemeDark = "Madilim",
    settingsThemeSystem = "Auto",

    settingsNotifications = "Mga Paalala",
    settingsNotificationsDesc = "Makatanggap ng mga alerto tungkol sa tindahan mo",
    settingsNotifLowStock = "Babala sa mababang stok",
    settingsNotifLowStockDesc = "Paalala kapag mauubos na ang paninda",
    settingsNotifSales = "Update sa benta",
    settingsNotifSalesDesc = "Paalala tungkol sa araw-araw na benta",

    settingsDataManagement = "Pamamahala ng Data",
    settingsDataManagementDesc = "Pamahalaan ang mga record ng tindahan mo",
    settingsDeleteAll = "Burahin Lahat ng Record",
    settingsDeleteAllDesc = "Tanggalin lahat ng imbentaryo, benta, at utang",
    settingsDeleteAllWarning = "Sigurado ka ba? Mabubura ang LAHAT ng data mo — imbentaryo, mga record ng benta, at listahan ng utang. Hindi na ito mababalik!",
    settingsDeleteAllConfirm = "Oo, Burahin Lahat",
    settingsDeleteAllDone = "Nabura na ang lahat ng record.",

    settingsHelp = "Tulong",
    settingsHelpDesc = "Alamin kung paano gamitin ang app",
    settingsHelpTitle = "Paano Gamitin ang Sari-Sync",
    settingsHelpContent = """
**Imbentaryo Tab**
• Pindutin ang "+" button para magdagdag ng produkto
• I-set ang presyo at puhunan ng bawat paninda
• Pindutin ang "Ibenta" para pumili ng Cash o Utang
• Pindutin ang "Dagdag Stok" para magdagdag ng stok
• Gamitin ang search bar para mabilis na maghanap
• Awtomatikong binabawasan ng app ang stok

**Utang Tab**
• I-record kapag may bumili ng utang
• I-record kapag nagbayad sila
• Makikita mo kung sino ang may utang at magkano
• Tingnan ang payer behavior badges (Mabuti/Katamtaman/Masama)

**Dashboard Tab**
• Tingnan ang kita mo araw-araw, lingguhan, o buwanan
• Makikita ang mga chart ng iyong benta
• Alamin kung anong paninda ang pinakamabenta
• Subaybayan ang mga stok levels
• Makita kung sino ang may pinakamalaking utang

**Settings Tab**
• Magpalit ng wika kahit kailan
• I-set ang pangalan ng tindahan mo
• Magpalit ng light o dark mode
• Pamahalaan ang iyong data

**Tip:** Ibenta ang mga paninda sa app para accurate ang dashboard mo!
""",

    settingsVersion = "Sari-Sync v1.0",

    // Stock Prediction
    predictionUrgent = "Mauubos sa loob ng %d araw!",
    predictionWarning = "Mauubos sa loob ng %d araw",
    predictionOutOfStock = "Ubos na!",

    // Customer Dropdown
    selectCustomerLabel = "Pumili ng Customer",
    noCustomersYet = "Walang customer pa"
)
