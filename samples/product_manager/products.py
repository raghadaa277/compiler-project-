class Product:
    def __init__(self, id, image, name, price, details):
        self.id = id
        self.image = image
        self.name = name
        self.price = price
        self.details = details


class ProductManager:
    def __init__(self):
        self.products = []
        self.counter = 1

    def add_product(self, image, name, price, details):
        product = Product(
            self.counter,
            image,
            name,
            price,
            details
        )

        self.products.append(product)
        self.counter += 1

    def get_all_products(self):
        return self.products

    def get_product(self, product_id):
        for product in self.products:
            if product.id == product_id:
                return product

    def delete_product(self, product_id):
        self.products = [
            product for product in self.products
            if product.id != product_id
        ]