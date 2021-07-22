public class LanguageDictionary {
	public static final String START = """
			سلام 🙋‍♂️
			به کارآموزی تابستانه‌ی کداستار خوش‌آمدید.😊
			برای اطلاع رسانی سریع‌تر و تعامل با شما عزیزان این ربات تلگرامی کداستار را تهیه کردیم.
			لطفا اطلاعات خواسته شده را به ترتیب و با دقت پر کنید.
			
			آدرس، کد پستی و شماره موبایل برای ارسال یک هدیه کوچک از طرف تیم کداستار به شماست.  😊🌺📬
			""";
	public static final String GET_NAME = """
			نام خود را وارد کنید:
			
			فرمت درست:
			محمد
			""";
	public static final String GET_LAST_NAME = """
			نام‌خانوادگی خود را وارد کنید:
			
			فرمت درست:
			محمدی
			""";
	public static final String GET_PHONE_NUMBER = """
			شماره تلفن خود را وارد کنید:
			
			(ترجیحا ایرانسل)
			فرمت درست:
			09121234567
			""";
	public static final String YOU_REGISTERED = "شما قبلا در سامانه ثبت‌نام کرده‌اید.";
	public static final String GET_ADDRESS = "آدرس منزل خود را وارد کنید:";
	public static final String GET_POST_CODE = """
			کدپستی منزل خود را وارد کنید:
			
			فرمت درست:
			1234567890
			""";
	public static final String CHANGED_TO_PRIVATE = """
			ازین پس هر پیامی اینجا بنویسید به صورت ناشناس به دست ما خواهد رسید.
			درصورتی که عبارت
			/public
			را وارد کنید پیام‌هایتان به صورت شناس به دست ما خواهد رسید.
			""";
	public static final String CHANGED_TO_PUBLIC = """
			ازین پس هر پیامی اینجا بنویسید به صورت شناس به دست ما خواهد رسید.
			درصورتی که عبارت
			/private
			را وارد کنید پیام‌هایتان به صورت ناشناس به دست ما خواهد رسید.
			""";
	public static final String FINISH_GET_INFORMATION = """
			ممنون که وقت گذاشتید و فرم مارا پر کردید😊🌺
			ازین پس هر پیامی اینجا بنویسید به دست ما خواهد رسید.
			درصورتی که عبارت
			/private
			را وارد کنید پیام‌هایتان به صورت ناشناس به دست ما خواهد رسید.
			
			در صورتی که عبارت
			/public
			را وارد کنید پیامتان به صورت شناس به دست ما خواهد رسید.
			
			با آرزوی موفقیت برایتان🌺
			""";
	public static final String MESSAGE_SENT_PUBLIC = """
			پیامتان به صورت شناس به دست ما رسید.😊
			ممنون از توجهتون 🌺
			""";
	public static final String MESSAGE_SENT_PRIVATE = """
			پیامتان به صورت ناشناس به دست ما رسید.😊
			ممنون از توجهتون 🌺
			""";
	public static final String HAVE_PRIVATE_MESSAGE = "یک پیام ناشناس دارید.";
	public static final String HAVE_PUBLIC_MESSAGE = "یک پیام شناس دارید.";
	public static final String REGISTER_AGAIN = """
			شما در ربات ثبت‌نام نکردید یا اطلاعاتتان از دیتابیس حذف شده است.😔
			برای استفاده از ربات با استفاده از دستور
			/start
			ابتدا ثبت‌نام نمایید.🌺
			""";
	public static final String SUCCESS_REQUEST = "درخواست شما با موفقیت ثبت شد.";
	public static final String HELP = """
			/start
			ثبت‌نام اولیه
			
			/private
			بعد از وارد کردن این دستور تمام پیام‌هایتان به صورت نا‌شناس برای ما ارسال خواهد شد.
			
			/public
			بعد از وارد کردن این دستور تمام پیام‌هایتان به صورت شناس برای ما ارسال خواهد شد.
			
			/edit_name
			ویرایش نام
			
			/edit_familyname
			ویرایش نام‌خانوادگی
			
			/edit_postcode
			ویرایش کدپستی
			
			/edit_address
			ویرایش آدرس
			
			/edit_phonenumber
			ویرایش شماره موبایل
			
			/edit_interntype
			ویرایش نوع کارآموزی
			
			/displaymyinfo
			مشاهده اطلاعات ثبت شده در ربات
			
			/help
			راهنما
			""";
	public static final String GET_INTERN_TYPE = """
			نوع کارآموزی خود را انتخاب کنید:
			رابط کاربری
			/pa
			فرانت‌اند
			/fe
			مهندسی‌نرم‌افزار
			/se
			""";
}
