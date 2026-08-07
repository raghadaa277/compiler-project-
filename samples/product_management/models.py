import uuid
import os
from datetime import datetime

class Product:
    """كلاس لإدارة بيانات المنتج"""
    
    products = {}  # تخزين مؤقت للمنتجات {id: product_data}
    
    def __init__(self, name, price, details, image_filename=None):
        self.id = str(uuid.uuid4())[:8]
        self.name = name
        self.price = float(price)
        self.details = details
        self.image_filename = image_filename
        self.created_at = datetime.now()
    
    def to_dict(self):
        """تحويل بيانات المنتج إلى قاموس"""
        return {
            'id': self.id,
            'name': self.name,
            'price': self.price,
            'details': self.details,
            'image_filename': self.image_filename,
            'created_at': self.created_at.strftime('%Y-%m-%d %H:%M:%S')
        }
    
    @classmethod
    def save(cls, product):
        """حفظ المنتج في التخزين المؤقت"""
        cls.products[product.id] = product.to_dict()
        return product.id
    
    @classmethod
    def get_all(cls):
        """الحصول على جميع المنتجات"""
        return list(cls.products.values())
    
    @classmethod
    def get_by_id(cls, product_id):
        """الحصول على منتج بواسطة ID"""
        return cls.products.get(product_id)
    
    @classmethod
    def delete(cls, product_id):
        """حذف منتج بواسطة ID"""
        if product_id in cls.products:
            # حذف الصورة إذا وجدت
            product = cls.products[product_id]
            if product.get('image_filename'):
                image_path = os.path.join('static/uploads', product['image_filename'])
                if os.path.exists(image_path):
                    try:
                        os.remove(image_path)
                    except:
                        pass
            del cls.products[product_id]
            return True
        return False
    
    @classmethod
    def update(cls, product_id, name, price, details):
        """تحديث بيانات المنتج"""
        if product_id in cls.products:
            cls.products[product_id]['name'] = name
            cls.products[product_id]['price'] = float(price)
            cls.products[product_id]['details'] = details
            return True
        return False