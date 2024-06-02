import secrets


def generate_secure_numeric_id(length=8):
    if length < 1:
        raise ValueError("Length must be at least 1.")

    # Chọn ngẫu nhiên các chữ số
    secure_numeric_id = ''.join(secrets.choice('0123456789') for _ in range(length))
    return secure_numeric_id


secure_numeric_id = 'GW'+ generate_secure_numeric_id(4)
print("Secure Numeric ID:", secure_numeric_id)
