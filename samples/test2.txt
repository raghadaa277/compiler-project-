# Add product


ADD_PAGE_CONTENT = """
<div style="max-width: 400px; margin: auto; border: 2px solid #333; padding: 15px;">
    <h3 style="color: #333;">New Product Form</h3>

    <form style="display: flex; flex-direction: column;">
        <input type="text" placeholder="Name" style="margin-bottom: 5px;">
        <input type="number" placeholder="Price" style="margin-bottom: 5px;">
        <textarea style="height: 50px; margin-bottom: 5px;"></textarea>
        <button type="submit" style="background: black; color: white;">Save Product</button>
    </form>
</div>
"""

def save_new_item(name_val, price_val):
    print("Action: Save to Symbol Table")
    status = "Success"
    return status

save_new_item("Smart Watch", 150.0)
