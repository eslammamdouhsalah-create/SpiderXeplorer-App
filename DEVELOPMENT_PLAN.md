# خطة التطوير والتجميع لتطبيق SpiderXeplorer Controller

هذه الخطة التفصيلية موجهة للمطورين لاستخدام الكود المصدري المرفق وتجميع التطبيق بنجاح في بيئة Android Studio.

## 1. إعداد بيئة العمل

1.  **تثبيت Android Studio:** تأكد من تثبيت أحدث إصدار من Android Studio.
2.  **تثبيت SDK:** تأكد من تثبيت Android SDK Platform 34 (أو أعلى) و SDK Build-Tools.
3.  **فتح المشروع:**
    *   قم بإنشاء مجلد جديد على جهازك (مثلاً: `SpiderXeplorer_Project`).
    *   قم بوضع جميع الملفات والمجلدات المرفقة (مثل `app/` و `build.gradle` الرئيسي) داخل هذا المجلد.
    *   في Android Studio، اختر **File -> Open** وافتح المجلد الذي أنشأته (`SpiderXeplorer_Project`).

## 2. مراجعة وتحديث ملفات Gradle

ملفات `build.gradle` المرفقة تحتوي على جميع التبعيات اللازمة (Kotlin Coroutines, CameraX, Retrofit, Room).

1.  **ملف `build.gradle` (Project):**
    *   تأكد من أن إصدارات المكونات متوافقة مع إعداداتك المحلية.
2.  **ملف `app/build.gradle` (Module: app):**
    *   إذا كنت تخطط لاستخدام مكتبة Room لتخزين بيانات الماكرو والإعدادات، يجب عليك إضافة المكون الإضافي **KAPT** في أعلى الملف:
        ```gradle
        plugins {
            id 'com.android.application'
            id 'org.jetbrains.kotlin.android'
            id 'kotlin-kapt' // أضف هذا السطر
        }
        ```
    *   ثم قم بإلغاء التعليق عن سطر `kapt` في قسم التبعيات:
        ```gradle
        // Room for local database (Settings/Macros)
        def room_version = "2.6.0"
        implementation "androidx.room:room-runtime:$room_version"
        implementation "androidx.room:room-ktx:$room_version"
        kapt "androidx.room:room-compiler:$room_version" // استخدم هذا السطر
        ```
3.  **مزامنة المشروع:** بعد أي تعديل، انقر على **Sync Now** في شريط الإشعارات العلوي.

## 3. تنفيذ كود البلوتوث (BLE)

الكود المرفق يعتمد على خدمة `BluetoothLeService` وواجهة `BleManager` لتجريد عملية الاتصال.

1.  **تحديد UUIDs:** يجب عليك تحديد **UUIDs** الخاصة بخدمة البلوتوث (Service UUID) وخصائص الإرسال والاستقبال (Characteristic UUIDs) التي يستخدمها الروبوت **SPIDERXEPLORER**.
    *   افتح ملف `ble/BleManager.kt` (أو ما يعادله).
    *   قم بتحديث الثوابت التالية بالقيم الصحيحة لروبوتك:
        ```kotlin
        // مثال: يجب استبدال هذه القيم بقيم الروبوت الفعلية
        private val SERVICE_UUID = UUID.fromString("0000FFE0-0000-1000-8000-00805F9B34FB")
        private val CHARACTERISTIC_WRITE_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")
        private val CHARACTERISTIC_NOTIFY_UUID = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")
        ```
2.  **تشفير الأوامر:** تأكد من أن الأوامر المرسلة (مثل "F" للحركة للأمام) تتطابق مع ما يتوقعه متحكم الروبوت (مثل Arduino أو Raspberry Pi).

## 4. إعداد بث الكاميرا

التطبيق مصمم لاستقبال بث الكاميرا عبر شبكة Wi-Fi أو اتصال مباشر (باستخدام Retrofit أو مكتبة بث مخصصة).

1.  **تحديد عنوان IP:** إذا كان الروبوت يبث الفيديو عبر Wi-Fi، يجب تحديد عنوان IP الخاص بالروبوت في الكود المسؤول عن عرض الفيديو (مثلاً في `CameraFragment.kt`).
2.  **تكامل المكتبة:** الكود المرفق يستخدم CameraX للتعامل مع كاميرا الهاتف المحلي، ولكن لبث الفيديو من الروبوت، قد تحتاج إلى دمج مكتبة بث فيديو (مثل `ExoPlayer` أو مكتبة مخصصة لـ RTSP/WebRTC) إذا كان الروبوت لا يستخدم Retrofit.

## 5. تجميع التطبيق (Build)

1.  **اختيار جهاز:** قم بتوصيل هاتف Android فعلي (Android 10+) عبر USB وتأكد من تفعيل وضع تصحيح الأخطاء (USB Debugging)، أو قم بتشغيل محاكي (Emulator).
2.  **تشغيل التطبيق:** انقر على زر **Run 'app'** (الأيقونة الخضراء المثلثة) في شريط الأدوات العلوي. سيقوم Android Studio بتجميع الكود وتثبيت التطبيق على الجهاز المحدد.
3.  **توليد APK (للتثبيت اليدوي):**
    *   اختر **Build -> Build Bundles / APKs -> Build APKs**.
    *   بعد اكتمال العملية، ستظهر رسالة منبثقة. انقر على **Locate** للعثور على ملف `app-debug.apk`.
    *   لنشر التطبيق بشكل نهائي، يجب توليد **Signed APK** باستخدام مفتاح توقيع (Keystore) عبر **Build -> Generate Signed Bundle / APK...**.

## 6. ملاحظات هامة

*   **الأذونات:** عند تشغيل التطبيق لأول مرة، سيطلب أذونات الموقع (للبلوتوث) والكاميرا والتخزين. يجب الموافقة عليها جميعًا ليعمل التطبيق بشكل صحيح.
*   **التصميم:** تم تطبيق سمة **Cyberpunk** في ملف `themes.xml`، ولكن قد تحتاج إلى تعديل ملفات XML الخاصة بالواجهات (Layouts) لإضافة تأثيرات الوهج (Glow Effect) والأيقونات ثلاثية الأبعاد المطلوبة.
*   **التخصيص:** هذا الكود هو هيكل كامل. ستحتاج إلى تخصيص بعض الأجزاء (مثل UUIDs البلوتوث) لتتناسب تمامًا مع إعدادات الروبوت الخاص بك.
