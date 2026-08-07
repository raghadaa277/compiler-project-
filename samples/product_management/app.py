from flask import Flask, render_template, request, redirect, url_for, flash
from werkzeug.utils import secure_filename
import os
from models import Product
from forms import ProductForm

app = Flask(__name__)
app.config['SECRET_KEY'] = 'your-secret-key-here-change-this'
app.config['UPLOAD_FOLDER'] = 'static/uploads'
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB max file size

# إنشاء مجلد رفع الصور إذا لم يكن موجوداً
os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)

# الصور المسموحة
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif', 'webp'}

def allowed_file(filename):
    """التحقق من امتداد الملف المسموح"""
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

# إضافة بعض المنتجات التجريبية
def init_sample_products():
    """دالة لإضافة منتجات تجريبية"""
    if not Product.get_all():
        sample_products = [
            Product('لابتوب احترافي', 2499, 'لابتوب عالي الأداء معالج Intel Core i7، رام 16GB، SSD 512GB','2.png'),
            Product('هاتف ذكي', 1899, 'هاتف بشاشة 6.7 بوصة، كاميرا 108 ميجابكسل، بطارية 5000mAh','3.png'),
            Product('سماعات لاسلكية', 299, 'سماعات بلوتوث مع عزل الضوضاء، عمر بطارية 30 ساعة','1.png'),
        ]
        for product in sample_products:
            Product.save(product)

# تهيئة المنتجات التجريبية
init_sample_products()

@app.route('/')
def index():
    """الصفحة الرئيسية - عرض جميع المنتجات"""
    products = Product.get_all()
    # عكس ترتيب المنتجات لعرض الأحدث أولاً
    products.reverse()
    return render_template('index.html', products=products)

@app.route('/product/add', methods=['GET', 'POST'])
def add_product():
    """صفحة إضافة منتج جديد"""
    form = ProductForm()
    
    if form.validate_on_submit():
        # حفظ الصورة إذا تم رفعها
        image_filename = None
        if form.image.data and allowed_file(form.image.data.filename):
            filename = secure_filename(form.image.data.filename)
            # إضافة معرف فريد للملف
            unique_filename = f"{str(os.urandom(8).hex())}_{filename}"
            filepath = os.path.join(app.config['UPLOAD_FOLDER'], unique_filename)
            form.image.data.save(filepath)
            image_filename = unique_filename
        
        # إنشاء منتج جديد
        product = Product(
            name=form.name.data,
            price=form.price.data,
            details=form.details.data,
            image_filename=image_filename
        )
        
        Product.save(product)
        flash('تم إضافة المنتج بنجاح!', 'success')
        return redirect(url_for('index'))
    
    return render_template('add_product.html', form=form)

@app.route('/product/<product_id>')
def product_detail(product_id):
    """صفحة تفاصيل المنتج"""
    product = Product.get_by_id(product_id)
    if not product:
        flash('المنتج غير موجود!', 'danger')
        return redirect(url_for('index'))
    
    return render_template('product_detail.html', product=product)

@app.route('/product/delete/<product_id>')
def delete_product(product_id):
    """حذف منتج"""
    if Product.delete(product_id):
        flash('تم حذف المنتج بنجاح!', 'success')
    else:
        flash('المنتج غير موجود!', 'danger')
    
    return redirect(url_for('index'))

@app.route('/product/edit/<product_id>', methods=['GET', 'POST'])
def edit_product(product_id):
    """تعديل بيانات المنتج"""
    product = Product.get_by_id(product_id)
    if not product:
        flash('المنتج غير موجود!', 'danger')
        return redirect(url_for('index'))
    
    form = ProductForm()
    
    if form.validate_on_submit():
        # تحديث بيانات المنتج
        Product.update(
            product_id,
            form.name.data,
            form.price.data,
            form.details.data
        )
        
        # تحديث الصورة إذا تم رفع صورة جديدة
        if form.image.data and allowed_file(form.image.data.filename):
            filename = secure_filename(form.image.data.filename)
            unique_filename = f"{str(os.urandom(8).hex())}_{filename}"
            filepath = os.path.join(app.config['UPLOAD_FOLDER'], unique_filename)
            form.image.data.save(filepath)
            
            # حذف الصورة القديمة
            if product.get('image_filename'):
                old_image_path = os.path.join(app.config['UPLOAD_FOLDER'], product['image_filename'])
                if os.path.exists(old_image_path):
                    try:
                        os.remove(old_image_path)
                    except:
                        pass
            
            # تحديث اسم الصورة
            product['image_filename'] = unique_filename
        
        flash('تم تحديث المنتج بنجاح!', 'success')
        return redirect(url_for('product_detail', product_id=product_id))
    
    # ملء النموذج بالبيانات الحالية
    form.name.data = product['name']
    form.price.data = product['price']
    form.details.data = product['details']
    
    return render_template('edit_product.html', form=form, product=product)

if __name__ == '__main__':
    app.run(debug=True, port=5000)