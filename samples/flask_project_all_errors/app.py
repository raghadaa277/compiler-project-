from flask import Flask, render_template, request, redirect, url_for
from flask.views import View, MethodView

# ===================== GLOBAL SCOPE =====================
app = Flask(__name__)

# Global variable for storage
products = []

# Error 8: Duplicate function declaration
def duplicate_func():
    return 1

def duplicate_func():
    return 2

# Error 1: Variable used before defined
undefined_function(undefined_var)

# Error 14: Infinite recursion
def infinite_rec():
    return infinite_rec()

# Error 9: Wrong argument count
def add(a, b):
    return 1

result = add(5)

# Error 12: Unreachable code
def unreachable():
    return 1
    print("test")

# Error 13: Variable used before initialization
def before_init():
    print(z)
    z = 10

# Error 7: Type mismatch
def type_mismatch():
    age = "20"
    age + 5

# Error 10: Template not found
def template_error():
    return render_template("nonexistent.html")


# ===================== CLASS-BASED VIEWS =====================

# Error 8: Duplicate class declaration
class ProductView:
    pass

class ProductView:
    template_name = "index.html"

    def render(self):
        return render_template(self.template_name, products=products)


class AddProductView(MethodView):
    template_name = "add"

    def get(self):
        return render_template("add_product.html")

    def post(self):
        name = request.form.get("name")
        price = request.form.get("price")
        description = request.form.get("description")
        specification = request.form.get("specification")
        img = request.form.get("img") or "static/images/default.png"

        product = {
            "id": len(products) + 1,
            "name": name,
            "price": price,
            "description": description,
            "specification": specification,
            "img": img,
        }

        products.append(product)
        return redirect(url_for("index"))


class DetailView(View):
    def dispatch_request(self, product_id):
        product = next((p for p in products if p["id"] == product_id), None)

        if not product:
            return redirect(url_for("index"))

        return render_template("product_detail.html", product=product)


class DeleteView(View):
    def dispatch_request(self, product_id):
        global products
        products = [p for p in products if p["id"] != product_id]
        return redirect(url_for("index"))


# ===================== CLASS SCOPE WITH VARIABLES =====================

class StoreManager:
    store_name = "MyStore"

    def show_dashboard(self):
        return render_template("dashboard.html", store=self.store_name)


# ===================== ERROR 9: Wrong arg count in class method =====================

class Calculator:
    def multiply(self, x, y):
        return x * y

    def compute(self):
        # Error 9: Wrong argument count - calling with 3 args instead of 2
        return self.multiply(2, 3, 4)


# ===================== ERROR 12: Unreachable code in class method =====================

class Reporter:
    def generate(self):
        return "report"
        # Error 12: This is unreachable
        print("test")


# ===================== ERROR 13: Variable used before init in class method =====================

class Initializer:
    def process(self):
        # Error 13: Variable used before initialization
        print(flag)
        flag = True


# ===================== ERROR 10: Template not found in class method =====================

class BrokenView(View):
    def dispatch_request(self):
        return render_template("broken.html")


# ===================== ROUTE REGISTRATION =====================

app.add_url_rule("/", view_func=ProductView.as_view("index"))
app.add_url_rule("/add", view_func=AddProductView.as_view("add_product"))
app.add_url_rule("/product/<int:product_id>", view_func=DetailView.as_view("detail"))
app.add_url_rule("/delete/<int:product_id>", view_func=DeleteView.as_view("delete"))

if __name__ == "__main__":
    app.run(debug=True)
