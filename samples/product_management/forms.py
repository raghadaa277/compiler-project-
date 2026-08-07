from flask_wtf import FlaskForm
from wtforms import StringField, TextAreaField, FloatField, FileField, SubmitField
from wtforms.validators import DataRequired, Length, NumberRange

class ProductForm(FlaskForm):
    """كلاس نموذج إضافة وتعديل المنتج"""
    
    name = StringField('اسم المنتج', validators=[
        DataRequired(message='اسم المنتج مطلوب'),
        Length(min=2, max=100, message='اسم المنتج يجب أن يكون بين 2 و 100 حرف')
    ])
    
    price = FloatField('سعر المنتج', validators=[
        DataRequired(message='سعر المنتج مطلوب'),
        NumberRange(min=0, message='السعر يجب أن يكون أكبر من أو يساوي 0')
    ])
    
    details = TextAreaField('تفاصيل المنتج', validators=[
        DataRequired(message='تفاصيل المنتج مطلوبة'),
        Length(min=10, max=1000, message='التفاصيل يجب أن تكون بين 10 و 1000 حرف')
    ])
    
    image = FileField('صورة المنتج')
    
    submit = SubmitField('حفظ المنتج')