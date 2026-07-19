from flask import Flask, render_template, request, redirect, url_for

app = Flask(__name__)

# ---- RAM STORAGE ----
products = []  # each product is a dict

multi = """
hello world
"""

@app.route("/")
def index():
    return render_template("index.html", products=products)


@app.route("/add", methods=["GET", "POST"])
def add_product():
    if request.method == "POST":
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
            "img": img,  # local placeholder
        }

        products.append(product)
        return redirect(url_for("index"))

    return render_template("add.html")


@app.route("/product/<int:product_id>")
def detail(product_id):
    product = next((p for p in products if p["id"] == product_id), None)
    return render_template("detail.html", product=product)


@app.route("/delete/<int:product_id>")
def delete(product_id):
    global products
    products = [p for p in products if p["id"] != product_id]
    return redirect(url_for("index"))


if __name__ == "__main__":
    app.run(debug=True)
