# Product details


ITEM_DETAILS_UI = """
<div style="display: flex; border-left: 5px solid green; padding: 20px;">
    <div style="margin-right: 20px;">
        <img src="{{ p.img }}" style="width: 200px; box-shadow: 5px 5px 10px #888;">
    </div>
    <div>
        <h1 style="margin: 0;">{{ p.name }}</h1>
        <p style="color: grey;">ID: {{ p.id }}</p>
        <p style="font-size: 20px;">{{ p.description }}</p>
    </div>
</div>
"""

def get_product_details(product_id):
    print("Node Name: DetailLookupNode")
    print("Line Number: 14")
    return ITEM_DETAILS_UI

get_product_details(505)
