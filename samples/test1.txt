#View products


ALL_PRODUCTS_TEMPLATE = """
<div style="background-color: #f0f0f0; padding: 20px; font-family: Arial;">
    <h2 style="text-align: center; color: #2c3e50;">Our Shop</h2>
    <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 10px;">
        {% for item in products_list %}
        <div style="background: white; border: 1px solid #ddd; padding: 10px; text-align: center;">
            <img src="{{ item.img }}" style="width: 100px; border-radius: 4px;">
            <h4 style="margin: 5px 0;">{{ item.name }}</h4>
            <span style="color: #27ae60; font-weight: bold;">${{ item.price }}</span>
        </div>
        {% endfor %}
    </div>
</div>
"""

def render_display(template_str):
    print("Node: RenderNode")
    print("Line: 18")
    return template_str

render_display(ALL_PRODUCTS_TEMPLATE)
